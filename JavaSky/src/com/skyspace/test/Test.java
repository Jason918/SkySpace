package com.skyspace.test;

import java.io.IOException;
import java.lang.Character.Subset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.skyspace.SkyEntry;
import com.skyspace.Template;
import com.skyspace.Item;
import com.skyspace.Sky;
import com.skyspace.util.CallBack;
import com.skyspace.util.ObjectProxy;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Sky.logger.setLevel(Level.ALL);
		try {
			FileHandler fh = new FileHandler("ENV.log");
			fh.setFormatter(new SimpleFormatter());
			Sky.logger.addHandler(fh);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		SkyEntry ee = new SkyEntry();
		
		Item ei1 = new Item(
				new ObjectProxy("FOR testing-EI1"),
				Item.TYPE_SUBSCRIBALE,
				"test,hello,jason",
				60000);
		Item ei2 = new Item(
				new ObjectProxy("FOR testing-EI2"),
				Item.TYPE_SUBSCRIBALE|Item.TYPE_ACQUIRABLE,
				"test,hello,jess",
				60000);
		
		
//		ee.Write(ei1);
//		ee.Write(ei2);
//		ee.subscribeMany(eg1);
		//ee.acquire(eg2);
		Scanner scanner = new Scanner(System.in);
		CallBack cb = new CallBack() {
			
			@Override
			public void callbackMany(Template eg, ArrayList<Item> eiList) {
				// TODO Auto-generated method stub
				System.out.println("callbackMany");
			}
			
			@Override
			public void callback(Template eg, Item ei) {
				// TODO Auto-generated method stub
				System.out.println("callbackMany");
			}
		};
		while (true) {
			String cmd = scanner.nextLine();
			if (cmd.equals("write")) {				
				System.out.print("ObjectID:");
				String objectID = scanner.nextLine();
				System.out.print("life time:");
				int time = scanner.nextInt();
				System.out.print("可订阅");
				boolean isAcquirable = scanner.nextBoolean();
				System.out.print("可获取");
				boolean isSubscribe = scanner.nextBoolean();
				System.out.print("单例");
				boolean isSingleton = scanner.nextBoolean();
				System.out.print("tuple");
				String tuple = scanner.next();
				int type = 0;
				if (isAcquirable) type = type | Item.TYPE_ACQUIRABLE;
				if (isSubscribe) type = type | Item.TYPE_SUBSCRIBALE;
				if (isSingleton) type = type | Item.TYPE_SINGLETON;
				ee.write(new ObjectProxy(objectID), tuple, type,time);
			} else if (cmd.equals("exit")){
				break;
			} else if (cmd.equals("s1")){
				Template eg1 = new Template(
						new ObjectProxy("YOU GET EG1"),
						"test,?,?",
						Template.TYPE_SUBSCRIBE|Template.TYPE_MANY,
						6000
						);
				ee.subscribe(eg1,cb);
			} else if (cmd.equals("s2")) {
				Template eg2 = new Template(
						new ObjectProxy("YOU GET EG2"),
						"test,?,jess",
						Template.TYPE_ACQUIRE,
						60000
						);
				ee.acquire(eg2,cb);
			}
		}
		System.out.println("TEST FINISHED");
	}

}
