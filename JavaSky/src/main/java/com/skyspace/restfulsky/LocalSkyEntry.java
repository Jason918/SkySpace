package com.skyspace.restfulsky;

import com.skyspace.ISkyEntry;
import com.skyspace.element.Item;
import com.skyspace.element.Template;
import com.skyspace.util.CallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by jianghaiting on 15/1/3.
 */
public class LocalSkyEntry implements ISkyEntry {
    private static final Logger LOG = LoggerFactory.getLogger(LocalSkyEntry.class);

    Set<Item> tupleSet = new ConcurrentSkipListSet<Item>();

    volatile int writeCount = 0;

    @Override
    public void setResponseTime(int time) {

    }

    @Override
    public void setTimeOut(int time) {

    }

    @Override
    public void setLeaseTime(int time) {

    }

    @Override
    public void write(Item it) {
        boolean ret = tupleSet.add(it);
        LOG.info("write[{}],tuple count:{}, it={}", ret, tupleSet.size(), it);
        if (!ret) {
            LOG.error("wrtie failed, tuple={}", it);
        } else {
            incWriteCount();
        }
    }

    private synchronized void incWriteCount() {
        writeCount++;
    }

    @Override
    public void subscribe(Template tmpl, CallBack cb) {

    }

    @Override
    public List<Item> read(Template tmpl) {
        return null;
    }

    @Override
    public void acquire(Template tmpl, CallBack cb) {

    }

    @Override
    public List<Item> take(Template tmpl) {
        LOG.info("take, tmpl={}", tmpl);
        while (tmpl.getExpire() > System.currentTimeMillis()) {
            int oldWriteCount = writeCount;
            //HashSet<Item> setCopy = new HashSet<Item>(tupleSet);
            for (Item item : tupleSet) {
//                LOG.debug("checking...tuple={}", item);
                if (item.getExpire() < System.currentTimeMillis()) {
                    boolean ret = tupleSet.remove(item);
                    LOG.info("remove expired tuple:{}, RESULT={}, LEFT={}", item, ret, tupleSet.size());
                } else {
                    if (tmpl.match(item)) {
                        if (tupleSet.remove(item)) {
                            LOG.info("found match, take it. it={}", item);
                            return Arrays.asList(item);
                        } else {
                            LOG.info("found match, failed remove. it = {}", item);
                        }
                    }
                }
            }
            while (oldWriteCount == writeCount && tmpl.getExpire() > System.currentTimeMillis()) {
//                LOG.debug("no match for now");
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            LOG.debug("retrying...left time={}ms", tmpl.getExpire() - System.currentTimeMillis());
        }
        return new ArrayList<Item>(0);
    }
}
