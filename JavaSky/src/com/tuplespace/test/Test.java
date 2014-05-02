package com.tuplespace.test;

import java.io.IOException;
import java.lang.Character.Subset;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.tuplespace.ENV;
import com.tuplespace.EnvEntry;
import com.tuplespace.EnvGroup;
import com.tuplespace.EnvItem;
import com.tuplespace.util.ObjectProxy;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ENV.logger.setLevel(Level.ALL);
		try {
			FileHandler fh = new FileHandler("ENV.log");
			fh.setFormatter(new SimpleFormatter());
			ENV.logger.addHandler(fh);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		EnvEntry ee = new EnvEntry();
		
		EnvItem ei1 = new EnvItem(
				new ObjectProxy("FOR testing-EI1"),
				EnvItem.TYPE_SUBSCRIBALE,
				"test,hello,jason",
				60000);
		EnvItem ei2 = new EnvItem(
				new ObjectProxy("FOR testing-EI2"),
				EnvItem.TYPE_SUBSCRIBALE|EnvItem.TYPE_ACQUIRABLE,
				"test,hello,jess",
				60000);
		
		
//		ee.Write(ei1);
//		ee.Write(ei2);
//		ee.subscribeMany(eg1);
		//ee.acquire(eg2);
		Scanner scanner = new Scanner(System.in);
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
				if (isAcquirable) type = type | EnvItem.TYPE_ACQUIRABLE;
				if (isSubscribe) type = type | EnvItem.TYPE_SUBSCRIBALE;
				if (isSingleton) type = type | EnvItem.TYPE_SINGLETON;
				ee.Write(new ObjectProxy(objectID), tuple, type,time);
			} else if (cmd.equals("exit")){
				break;
			} else if (cmd.equals("s1")){
				EnvGroup eg1 = new EnvGroup(
						new ObjectProxy("YOU GET EG1"),
						"test,?,?",
						EnvGroup.TYPE_SUBSCRIBE|EnvGroup.TYPE_MANY,
						6000
						);
				ee.subscribeMany(eg1);
			} else if (cmd.equals("s2")) {
				EnvGroup eg2 = new EnvGroup(
						new ObjectProxy("YOU GET EG2"),
						"test,?,jess",
						EnvGroup.TYPE_ACQUIRE,
						60000
						);
				ee.acquire(eg2);
			}
		}
		System.out.println("TEST FINISHED");
	}

}
