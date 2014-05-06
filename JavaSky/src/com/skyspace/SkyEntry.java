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
	
	public SkyEntry() {
		Sky.getInstance().start(1000);
	}

	@Override
	public void acquire(Template tmpl, CallBack cb) {
		tmpl.setCallback(cb);
		if (tmpl.isAcquire())
			Sky.getInstance().sendRequest(tmpl);
		else
			Sky.logger.warning("tmpl is not acquire or take!"+tmpl);
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
		tmpl.setCallback(cb);
		if (!tmpl.isAcquire())
			Sky.getInstance().sendRequest(tmpl);
		else
			Sky.logger.warning("tmpl is not subscribe or read!"+tmpl);
	}
	
	@Override
	public void write(Item e) {
		Sky.getInstance().write(e);
	}

	@Override
	public List<Item> read(Template tmpl) {
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
