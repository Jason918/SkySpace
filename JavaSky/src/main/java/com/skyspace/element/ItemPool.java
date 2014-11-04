package com.skyspace.element;

import com.skyspace.Sky;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemPool extends ElementPool {
    private CopyOnWriteArrayList<Item> envItemList = new CopyOnWriteArrayList<Item>();
    private CopyOnWriteArrayList<Item> lockList = new CopyOnWriteArrayList<Item>();

    public Item getMatch(Template tmpl) {
        int index = envItemList.indexOf(tmpl);
        if (index >= 0)
            return envItemList.get(index);
        else
            return null;
    }

    public List<Item> getAllMatch(Template tmpl) {
        List<Item> result = new ArrayList<Item>();

        for (Item it : envItemList) {
            if (tmpl.match(it)) {
                result.add(it);
            }
        }
        return result;
    }

    public void lock(Item it) {
        int index = envItemList.indexOf(it);
        if (index >= 0) {
            lockList.add(envItemList.get(index));
            envItemList.remove(index);
        } else {
            Sky.logger.warning("lock fail,item do not exits:" + it);
        }

    }

    public void unlock(Item it) {
        int index = lockList.indexOf(it);
        if (index >= 0) {
            envItemList.add(lockList.get(index));
            lockList.remove(index);
        } else {
            Sky.logger.warning("unlock fail,item do not exits:" + it);
        }
    }

    public Item get(Item it) {
        int index = envItemList.indexOf(it);
        if (index >= 0)
            return envItemList.get(index);
        else
            return null;
    }

    @Override
    public void add(Element e) {
        if (e instanceof Item == false) {
            Sky.logger.warning("element is not Item");
            return;
        }
        Item it = (Item) e;
        if (it.isSinglton()) {
            int index = envItemList.indexOf(it);
            if (index >= 0) {
                envItemList.get(index).expire = it.expire;//update expiredate!!
                return;
            }
        }
        envItemList.add(it);
        it.setContainer(this);
    }

    @Override
    public void remove(Element e) {
        if (e instanceof Item == false) {
            Sky.logger.warning("element is not Item");
            return;
        }
        boolean b = envItemList.remove(e);
        if (!b) {
            b = lockList.remove(e);
            if (!b) {
                Sky.logger.warning("remove fail,item do not exits:" + e);
            } else {
                Sky.logger.finest("remove success:" + e);
            }
        }
    }

    @Override
    public void buryDead() {
        for (Item it : envItemList) {
            it.isAlive();
        }
        for (Item it : lockList) {
            it.isAlive();
        }
    }

    public void clear() {
        envItemList.clear();
        lockList.clear();
    }

    public List<String> toListString() {
        ArrayList<String> list = new ArrayList<String>();
        for (Item it : envItemList) {
            list.add(it.toString());
        }
        return list;
    }


}
