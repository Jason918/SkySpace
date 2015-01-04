package com.skyspace.restfulsky;

import com.skyspace.element.Item;
import com.skyspace.element.Template;

public class LocalSkyEntryTest {

    @org.junit.Test
    public void test() throws InterruptedException {
        final LocalSkyEntry entry = new LocalSkyEntry();
//        entry.write(new Item(null, Item.TYPE_ACQUIRABLE, "a,b,c", 1000));
//        entry.write(new Item(null, Item.TYPE_ACQUIRABLE, "a,b,d", 1000));
//        entry.write(new Item(null, Item.TYPE_ACQUIRABLE, "a,b,e", 1000));
//        entry.write(new Item(null, Item.TYPE_ACQUIRABLE, "a,b,f", 1000));
//        System.out.println(entry.take(new Template(null, "a,b,?", Template.TYPE_ACQUIRE, 1000)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("result->"+entry.take(new Template(null, "a,b,d,?", Template.TYPE_ACQUIRE, 6000)));
            }
        }).start();

        Thread.sleep(5000);
        entry.write(new Item(null, Item.TYPE_ACQUIRABLE, "a,b,d,x", 1000));
        Thread.sleep(5000);
    }

}
