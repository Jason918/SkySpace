package com.skyspace;

import com.skyspace.element.Item;
import com.skyspace.element.ItemPool;
import com.skyspace.element.Template;
import com.skyspace.element.TemplatePool;
import com.skyspace.util.NetWorker;

import java.util.*;
import java.util.logging.Logger;

public class Sky {
    /**
     * used to make it easier to log ...
     */
    public static Logger logger = Logger.getLogger(Sky.class.getName());
    /**
     * singleton...
     */
    private static Sky ts = null;
    private ItemPool item_pool;
    // private ItemPool item_cache;
    private TemplatePool template_cache; // store other's request.
    private TemplatePool template_pool; // store own's template
    private NetWorker networker;
    private Timer garbage_collector;
    private boolean is_started;
    private int stat_item_sended = 0;
    private int stat_item_received = 0;
    private int stat_template_sended = 0;
    private int stat_template_received = 0;

    /**
     * 构造器,做初始化工作.
     */
    private Sky() {
        networker = new NetWorker();
        item_pool = new ItemPool();
        // item_cache = new ItemPool();
        template_cache = new TemplatePool();
        template_pool = new TemplatePool();
        is_started = false;
        logger.finer("construct done.");
    }

    public static Sky getInstance() {
        if (ts == null)
            ts = new Sky();
        return ts;
    }

    /**
     * write an Item into ENV
     *
     * @param it
     */
    public void write(Item it) {
        stat_item_sended++;
        // handle old groups.
        if (!handleNewItem(it, true)) {
            Sky.logger.fine("adding Item into pool:" + it);
            item_pool.add(it);// did not acquired
        }
    }

    /**
     * send an Template into ENV
     *
     * @param tmpl
     */
    public void sendRequest(Template tmpl) {
        stat_template_sended++;
        template_pool.add(tmpl);
        networker.sendDataToEnviroment(tmpl.pack());
    }

    /**
     * function for handling requst recitved from other node.
     *
     * @param tmpl
     */
    public void handleRequest(Template tmpl) {
        stat_template_received++;
        Sky.logger.entering("Sky", "handling request:", tmpl);
        List<Item> result;
        if (tmpl.isMany()) {
            result = item_pool.getAllMatch(tmpl);
        } else {
            Item it = item_pool.getMatch(tmpl);
            result = new ArrayList<Item>();
            if (it != null) {
                result.add(it);
            }
        }
        if (result.size() == 0) {// not in pool ,trying cache.
            Sky.logger.fine("no result for request");
            // it = item_cache.getMatch(tmpl);
            // if (it == null) { //not in the cache itther.put it in buffer.
            // Sky.logger.fine("no match in envItemCache");
            // template_pool.add(tmpl);
            // } else {//got a match in cache
            // Sky.logger.fine("get match in envItemCache:"+it);
            // networker.sendResult(it,tmpl,false);
            // }
        } else {// got a match in pool
            for (Item it : result) {
                Sky.logger.fine("get match in item_pool:" + it);
                if (tmpl.isAcquire()) {
                    item_pool.lock(it);
                    if (networker.sendResult(it, tmpl, true)) {
                        item_pool.remove(it);
                    } else {
                        item_pool.unlock(it);
                    }
                } else {// subscribe do not need to know target want it
                    networker.sendResult(it, tmpl, true);
                }
            }
        }
        if (result.size()==0 || tmpl.isMany()){
            template_cache.add(tmpl);
        }
        Sky.logger.exiting("ENV", "handle_request");
    }

    /**
     * 1 function for handling result of request it send out.
     *
     * @param it
     * @return
     */
    public boolean handleResult(Item it, Template tmpl) {
        stat_item_received++;
        Sky.logger.info("get Item " + it + " for Template:" + tmpl);

        Template ntmpl = template_pool.get(tmpl);
        if (ntmpl != null) {
            Sky.logger.info("####################\nTemplate get a match:\n"
                    + ntmpl + "\n-----\n" + it + "\n#######################\n");
            // System.out.println(ntmpl.owner.getAction());
            if (ntmpl.isMany()) {
                template_pool.addMatch(it, ntmpl);
                // System.out.println("add match in template_pool item:"+it+"tmpl:"+ntmpl);
            } else {
                ntmpl.getCallback().handle(ntmpl, it);
                template_pool.remove(ntmpl);
            }
            // item_cache.add(it);
            handleNewItem(it, false);
            return true;
        }
        Sky.logger.finer("do not have this Template any more");
        return false;
    }

    /**
     * 处理新加入Item的情况
     *
     * @param it
     * @param isFromOwner
     * @return 返回新加入的是否被成功acquire了
     */
    private boolean handleNewItem(Item it, boolean isFromOwner) {
        Collection<Template> tmpls = template_cache.getMatch(it);
        Template ac = null;
        for (Template tmpl : tmpls) {
            if (!tmpl.isAcquire()) {
                if (!networker.sendResult(it, tmpl, true) && !tmpl.isMany()) {
                    template_cache.add(tmpl);// target do not want this to end.
                }
            } else {
                if (ac == null || tmpl.prior(ac)) {
                    ac = tmpl;
                } else {
                    if (!tmpl.isMany()) {
                        template_cache.add(tmpl);
                    }
                }
            }
        }
        if (ac != null) {
            if (networker.sendResult(it, ac, true)) {
                return true;
            } else if (!ac.isMany()) {
                template_cache.add(ac);
            }
        }
        return false;
    }

    /**
     * tmpl获取的是it的cache，需要向it的owner发出请求
     *
     * @param it
     * @param tmpl
     */
    public void handleCacheAcquireResult(Item it, Template tmpl) {
        Template ntmpl = template_pool.get(tmpl);
        if (ntmpl != null) {
            Item new_it = networker.acquireItem(it, ntmpl);
            if (new_it != null) {
                handleResult(new_it, tmpl);
            }
        }
    }

    public Item acquireFromPool(Item it, Template tmpl) {
        Item nit = item_pool.get(it);
        if (nit != null)
            item_pool.lock(nit);

        return nit;
    }

    public void confirmAcquire(Item it, boolean accept) {
        if (accept) {
            item_pool.remove(it);
        }
    }

    /**
     * start the tuplespace service.
     */
    public void start(int period) {
        if (is_started)
            return;

        logger.info("START service!");
        networker.startRequestListener(this);
        networker.startResponseListener(this);
        garbage_collector = new Timer("Element Cleaner", true);
        garbage_collector.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                try {
                    item_pool.buryDead();
                    // item_cache.buryDead();
                    template_cache.buryDead();
                    template_pool.buryDead();
                } catch (NullPointerException e) {
                    System.out.println("NULL pointer here");
                }
            }
        }, 1000, period);
        logger.finer("start sky done.");
        is_started = true;
    }

    /**
     * stop the tuplespace service.
     */
    public void stop() {

        logger.info("STOP service!");
        networker.stopRequestListener();
        networker.stopResponseListener();
        garbage_collector.cancel();
        is_started = false;
    }

    public String getStatisticInfo() {

        return "Item发送量：" + stat_item_sended + "\nItem接手量："
                + stat_item_received + "\nTemplate发送量：" + stat_template_sended
                + "\nTemplate接收量：" + stat_template_received;
    }

    public List<String> getItemPoolInfo() {
        return item_pool.toListString();
    }

    // public List<String> getEnvItemCachitnfo() {
    // return item_cache.toListString();
    // }

    // public List<String> getEnvGroupCachitnfo() {
    // return template_pool.toListString();
    // }

    public List<String> getTemplatePoolInfo() {
        return template_pool.toListString();
    }

    public void clearData() {
        // clearCache();
        item_pool.clear();
        template_pool.clear();
        template_cache.clear();
        stat_item_received = 0;
        stat_item_sended = 0;
        stat_template_sended = 0;
        stat_template_received = 0;
    }

    void report_status() {
        System.out.println("=============Current item_pool status===========");
        System.out.println(item_pool.toListString());
        System.out
                .println("=============Current template_pool status===========");
        System.out.println(template_pool.toListString());
        System.out
                .println("=============Current template_cache status===========");
        System.out.println(template_cache.toListString());
        System.out.println("+++++++++++++Current status END+++++++++++");
    }

}
