package com.skyspace;

import java.util.List;

import com.skyspace.util.CallBack;
import com.skyspace.util.ObjectProxy;


public class SkyEntry implements ISkyEntry {
	public static int DEFAULT_TIME = 60000;
	private int response_time = DEFAULT_TIME;
	private int lease_time = DEFAULT_TIME;
	private int wait_out = DEFAULT_TIME;
	
	public SkyEntry() {
		Sky.getInstance().start(1000);
	}

	@Override
	public void acquire(Template tmpl, CallBack cb) {
		if (tmpl.isAcquire() && !tmpl.isMany())
			Sky.getInstance().sendRequest(tmpl);
		else
			Sky.logger.warning("tmpl is not acquire or is many!"+tmpl);
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
		wait_out = time;
	}

	@Override
	public void subscribe(Template tmpl, CallBack cb) {
		if (!tmpl.isAcquire() && !tmpl.isMany())
			Sky.getInstance().sendRequest(tmpl);
		else
			Sky.logger.warning("tmpl is not subscribe or is many!"+tmpl);
	}
	
	
//	public void subscribe(ObjectProxy owner, String template) {
//		subscribe(new Template(owner,template,Template.TYPE_SUBSCRIBE,wait_out));
//	}
//
//	
//	public void subscribe(ObjectProxy owner, String template, int waitTime) {
//		subscribe(new Template(owner, template, Template.TYPE_SUBSCRIBE, waitTime));
//	}
//
//	@Override
//	public void subscribeMany(Template tmpl) {
//		if (!tmpl.isAcquire() && tmpl.isMany())
//			Sky.getInstance().sendRequest(tmpl);
//		else
//			Sky.logger.warning("tmpl is not subsribe or is not many!"+tmpl);
//	}
//
//	@Override
//	public void subscribeMany(ObjectProxy owner, String template) {
//		subscribeMany(new Template(owner, template, Template.TYPE_SUBSCRIBE|Template.TYPE_MANY, wait_out));		
//	}
//
//	@Override
//	public void subscribeMany(ObjectProxy owner, String template, int waitTime) {
//		subscribeMany(new Template(owner, template, Template.TYPE_SUBSCRIBE|Template.TYPE_MANY, waitTime));		
//	}

	@Override
	public void write(Item e) {
		Sky.getInstance().write(e);
	}

	
	public void write(ObjectProxy owner, String tuple, int type) {
		write(new Item(owner,type,tuple,lease_time));
	}

	
	public void write(ObjectProxy owner, String tuple, int type, int leasetime) {
		write(new Item(owner,type,tuple,leasetime));
	}

	@Override
	public List<Item> read(Template tmpl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> take(Template tmpl) {
		// TODO Auto-generated method stub
		return null;
	}



	
}
