package com.tuplespace;

import com.tuplespace.util.ObjectProxy;


public class EnvEntry implements IEnvEntry {
	public EnvEntry() {
		ENV.getInstance().start(1000);
	}
	public static int DEFAULT_TIME = 60000;
	
	private int response_time = DEFAULT_TIME;
	private int lease_time = DEFAULT_TIME;
	private int wait_time = DEFAULT_TIME;

	@Override
	public void acquire(EnvGroup eg) {
		if (eg.isAcquire() && !eg.isMany())
			ENV.getInstance().sendRequest(eg);
		else
			ENV.logger.warning("eg is not acquire or is many!"+eg);
	}

	@Override
	public void acquire(ObjectProxy owner, String template) {
		acquire(new EnvGroup(owner, template, EnvGroup.TYPE_ACQUIRE, wait_time));
	}

	@Override
	public void acquire(ObjectProxy owner, String template, int waitTime) {
		acquire(new EnvGroup(owner, template, EnvGroup.TYPE_ACQUIRE, waitTime));
	}

	@Override
	public void acquireMany(EnvGroup eg) {
		if (eg.isAcquire() && eg.isMany())
			ENV.getInstance().sendRequest(eg);
		else
			ENV.logger.warning("eg is not acquire or is not many!"+eg);
	}
	
	@Override
	public void acquireMany(ObjectProxy owner, String template) {
		acquire(new EnvGroup(owner, template, EnvGroup.TYPE_ACQUIRE|EnvGroup.TYPE_MANY, wait_time));		
	}

	@Override
	public void acquireMany(ObjectProxy owner, String template, int waitTime) {
		acquire(new EnvGroup(owner, template, EnvGroup.TYPE_ACQUIRE|EnvGroup.TYPE_MANY, waitTime));		
	}

//	@Override
//	public void addDenyAccessPolicy(AccessControlPolicy acp) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void addTupleAllocationPolicy(TupleAllocationPolicy tap) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void setResponseTime(int time) {
		response_time = time;
	}

	@Override
	public void setWaitTime(int time) {
		wait_time = time;
	}

	@Override
	public void setLeaseTime(int time) {
		lease_time = time;
	}

	@Override
	public void subscribe(EnvGroup eg) {
		if (!eg.isAcquire() && !eg.isMany())
			ENV.getInstance().sendRequest(eg);
		else
			ENV.logger.warning("eg is not subscribe or is many!"+eg);
	}
	
	@Override
	public void subscribe(ObjectProxy owner, String template) {
		subscribe(new EnvGroup(owner,template,EnvGroup.TYPE_SUBSCRIBE,wait_time));
	}

	@Override
	public void subscribe(ObjectProxy owner, String template, int waitTime) {
		subscribe(new EnvGroup(owner, template, EnvGroup.TYPE_SUBSCRIBE, waitTime));
	}

	@Override
	public void subscribeMany(EnvGroup eg) {
		if (!eg.isAcquire() && eg.isMany())
			ENV.getInstance().sendRequest(eg);
		else
			ENV.logger.warning("eg is not subsribe or is not many!"+eg);
	}

	@Override
	public void subscribeMany(ObjectProxy owner, String template) {
		subscribeMany(new EnvGroup(owner, template, EnvGroup.TYPE_SUBSCRIBE|EnvGroup.TYPE_MANY, wait_time));		
	}

	@Override
	public void subscribeMany(ObjectProxy owner, String template, int waitTime) {
		subscribeMany(new EnvGroup(owner, template, EnvGroup.TYPE_SUBSCRIBE|EnvGroup.TYPE_MANY, waitTime));		
	}

	@Override
	public void Write(EnvItem e) {
		ENV.getInstance().write(e);
	}

	@Override
	public void Write(ObjectProxy owner, String tuple, int type) {
		Write(new EnvItem(owner,type,tuple,lease_time));
	}

	@Override
	public void Write(ObjectProxy owner, String tuple, int type, int leasetime) {
		Write(new EnvItem(owner,type,tuple,leasetime));
	}



	
}
