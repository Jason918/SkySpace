package com.skyspace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.skyspace.util.CallBack;
import com.skyspace.util.ObjectProxy;


public class SkyEntry implements ISkyEntry {
	public static int DEFAULT_TIME = 60000;
	private int response_time = DEFAULT_TIME;
	private int lease_time = DEFAULT_TIME;
	private int time_out = DEFAULT_TIME;
	
	private ObjectProxy entry_owner;
	public SkyEntry(ObjectProxy op) {
		if (op == null) {
			op = new ObjectProxy("DefaultProxy");
		} else {			
			entry_owner = op;
		}
		Sky.getInstance().start(1000);
	}

	@Override
	public void acquire(Template tmpl, CallBack cb) {
		check_element_owner(tmpl);
		tmpl.setCallback(cb);
		if (tmpl.isAcquire())
			Sky.getInstance().sendRequest(tmpl);
		else
			Sky.logger.warning("tmpl is not acquire or take!"+tmpl);
	}
	
	private void check_element_owner(Element ele) {
		if (ele.getOwner() == null) {
			ele.setOwner(entry_owner);
		}
	}

	@Override
	public void setLeaseTime(int time) {
		lease_time = time;
	}

	@Override
	public void setResponseTime(int time) {
		response_time = time;
	}

	@Override
	public void setTimeOut(int time) {
		time_out = time;
	}

	@Override
	public void subscribe(Template tmpl, CallBack cb) {
		check_element_owner(tmpl);
		tmpl.setCallback(cb);
		if (!tmpl.isAcquire())
			Sky.getInstance().sendRequest(tmpl);
		else
			Sky.logger.warning("tmpl is not subscribe or read!"+tmpl);
	}
	
	@Override
	public void write(Item it) {
		check_element_owner(it);
		Sky.getInstance().write(it);
	}

	@Override
	public List<Item> read(Template tmpl) {
//		check_element_owner(tmpl);
		final Semaphore sem = new Semaphore(0, false); 
		final List<Item> ret = new ArrayList<Item>();
		CallBack cb = new CallBack(){

			@Override
			public void handle(Template tmpl, Item it) {
				ret.add(it);
				sem.release();
			}

			@Override
			public void handleMany(Template tmpl, ArrayList<Item> itList) {
				ret.addAll(itList);
				sem.release();
			}
		};
		
		subscribe(tmpl, cb);
		
		
		try {
			sem.tryAcquire(tmpl.expire-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		return ret;
	}

	@Override
	public List<Item> take(Template tmpl) {
//		check_element_owner(tmpl);
		final Semaphore sem = new Semaphore(0, false); 
		final List<Item> ret = new ArrayList<Item>();
		CallBack cb = new CallBack(){

			@Override
			public void handle(Template tmpl, Item it) {
				ret.add(it);
				sem.release();
			}

			@Override
			public void handleMany(Template tmpl, ArrayList<Item> itList) {
				ret.addAll(itList);
				sem.release();
			}
		};
		
		acquire(tmpl, cb);
		
		
		try {
			sem.tryAcquire(tmpl.expire-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		return ret;
	}



	
}
