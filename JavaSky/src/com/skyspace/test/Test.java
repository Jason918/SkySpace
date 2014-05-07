package com.skyspace.test;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.skyspace.Item;
import com.skyspace.Sky;
import com.skyspace.SkyEntry;
import com.skyspace.Template;

public class Test {

	private static int node_id;
	static void test_acquire() {
		SkyEntry se = new SkyEntry("Node"+node_id);
		
		if (node_id == 0) {			
			Item it = new Item(
					null,
					Item.TYPE_ACQUIRABLE,
					"test,hello,jason",
					5*60000);
			se.write(it);
		} else if (node_id == 1) {
			Template tmpl = new Template(null, "test,?,jason", Template.TYPE_ACQUIRE, 60000);
			se.acquire(tmpl, null);
		} else {
			System.err.println("node_id error:"+node_id);
		}

	}
	static void test_subscribe() {
		
	}
	static void test_read() {
		
	}
	static void test_take() {
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		node_id = scanner.nextInt();
		
		
		Sky.logger.setLevel(Level.ALL);
		try {
			FileHandler fh = new FileHandler("ENV-Node"+node_id+".log");
			fh.setFormatter(new SimpleFormatter());
			Sky.logger.addHandler(fh);
			
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}



		
		System.out.println("\n\n..........test_acquire()...........");
		test_acquire();
		
		System.out.println("\n\n..........test_subscribe()...........");
		test_subscribe();
		
		System.out.println("\n\n..........test_read()...........");
		test_read();
		
		System.out.println("\n\n..........test_take()...........");
		test_take();
		
		
		System.out.println("\n\nTEST FINISHED, enter a new line to finish.");
//		scanner.nextLine();
		scanner.close();
		
	}

}
