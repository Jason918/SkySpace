package com.skyspace.test;

import com.skyspace.Sky;
import com.skyspace.SkyEntry;
import com.skyspace.element.Item;
import com.skyspace.element.Template;
import com.skyspace.util.CallBack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Test {

    private static int node_id;

    private static Scanner scanner;

    static void test_acquire() {
        SkyEntry se = new SkyEntry("Node" + node_id);

        if (node_id == 0) {
            Item it = new Item(
                    null,
                    Item.TYPE_ACQUIRABLE,
                    "test_acq,hello,jason",
                    5 * 60000);
            se.write(it);
        } else if (node_id == 1) {
            Template tmpl = new Template(null, "test,?,jason",
                    Template.TYPE_ACQUIRE, 60000);
            se.acquire(tmpl, new CallBack() {

                @Override
                public void handleMany(Template tmpl, ArrayList<Item> itList) {
                    System.out.println("handleMany..." + tmpl + "," + itList);
                }

                @Override
                public void handle(Template tmpl, Item it) {
                    System.out.println("handleMany..." + tmpl + "," + it);
                }
            });
        } else {
            System.err.println("node_id error:" + node_id);
        }
        scanner.next();
        se.report_status();
    }

    static void test_subscribe() {
        SkyEntry se = new SkyEntry("Node" + node_id);

        if (node_id == 1) {
            Item it = new Item(
                    null,
                    Item.TYPE_SUBSCRIBALE,
                    "test_sub,hello,jason",
                    5 * 60000);
            se.write(it);
        } else if (node_id == 0) {
            Template tmpl = new Template(null, "test_sub,?,?",
                    Template.TYPE_SUBSCRIBE, 60000);
            se.subscribe(tmpl, null);

        } else {
            System.err.println("node_id error:" + node_id);
        }
        scanner.next();
        se.report_status();
    }

    static void test_read() {
        SkyEntry se = new SkyEntry("Node" + node_id);
        if (node_id == 0) {
            Item it = new Item(
                    null,
                    Item.TYPE_SUBSCRIBALE,
                    "test,hello,jason",
                    5 * 60000);
            se.write(it);

            Item it2 = new Item(
                    null,
                    Item.TYPE_SUBSCRIBALE,
                    "test,hi,jason",
                    5 * 60000);
            se.write(it2);

        }
        if (node_id == 1) {
            Template tmpl = new Template(null, "test,?,jason",
                    Template.TYPE_SUBSCRIBE | Template.TYPE_MANY, 3000);
            List<Item> items = se.read(tmpl);
            System.out.println("read result:" + items);
        }
    }

    static void test_take() {
        final SkyEntry se = new SkyEntry("Node" + node_id);

        new Thread(new Runnable() {
            @Override public void run() {
                Template tmpl = new Template(null, "test,?,?",
                        Template.TYPE_ACQUIRE, 6000000);
                List<Item> items = se.take(tmpl);
                System.out.println("read result:" + items);
            }
        }).start();
        Item it2 = new Item(
                null,
                Item.TYPE_SUBSCRIBALE|Item.TYPE_ACQUIRABLE,
                "test,hi,jason",
                1000);
        se.write(it2);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        scanner = new Scanner(System.in);
        node_id = scanner.nextInt();

        Sky.logger.setLevel(Level.ALL);

        FileHandler fh;
        try {
            fh = new FileHandler("ENV-Node" + node_id + ".log");
            fh.setFormatter(new SimpleFormatter());
            Sky.logger.addHandler(fh);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n..........test_acquire()...........");
        //test_acquire();

        System.out.println("\n\n..........test_subscribe()...........");
        //test_subscribe();

        System.out.println("\n\n..........test_read()...........");
        //test_read();

        System.out.println("\n\n..........test_take()...........");
        test_take();

        //System.out.println("\n\nTEST FINISHED, enter a new line to finish.");
        //		scanner.nextLine();
        //scanner.close();

    }

}
