package com.tuplespace;

import com.tuplespace.util.ObjectProxy;


/**
 * 元组空间接口
 * 关于Write、Subscribe、Acquire这几个操作都是异步的。
 * 在ObjectAdapter中有Owner的地址,用Multicast公布结果.
 * 
 * @author Jason
 *
 */
public interface IEnvEntry {
	public static final int DEFAULT_TUPLE_LEASE_TIME = 5;
	public static final int DEFAULT_RESPONSE_TIME = 1;
	public static final int DEFAULT_TEMPLATE_WAIT_TIME = 5;

	/**
	 * 设置最久的响应时间，即在response_time内响应的认为是同时响应。
	 * @param time
	 */
	public void setResponseTime(int time);
	
	public void setWaitTime(int time);
	
	public void setLeaseTime(int time);
	/**
	 * 将元组写入元组空间. 
	 * @param ei 要写入元组空间的元组.
	 */
	public void Write(EnvItem e);
	/**
	 * 省去构造EnvItem
	 * 各参数定义见{@link EnvItem}
	 * @param owner
	 * @param tuple
	 * @param type 
	 * @param leasetime
	 */
	public void Write(ObjectProxy owner,String tuple,int type,int leasetime );
	/**
	 * leaseTime设置为默认值:见DEFAULT_TUPLE_LEASE_TIME
	 * @param owner
	 * @param tuple
	 * @param type
	 */
	public void Write(ObjectProxy owner,String tuple,int type);
	
	public void subscribe(EnvGroup eg);
	
	/**
	 * 订阅一个元组（从元组空间读取一个元组）
	 * @param owner	订阅的主人的描述信息
	 * @param template	用来匹配元组空间的模版。
	 * @param waitTime	有效时间 
	 */
	public void subscribe(ObjectProxy owner, String template, int waitTime);
	/**
	 * waittime设置为默认值,见:DEFAULT_TEMPLATE_WAIT_TIME
	 * @param owner
	 * @param template
	 */
	public void subscribe(ObjectProxy owner, String template);
	
	/**
	 * subscribe的many版本
	 * @param owner
	 * @param template
	 * @param waitTime
	 */
	public void subscribeMany(ObjectProxy owner, String template, int waitTime);
	public void subscribeMany(ObjectProxy owner, String template);
	public void subscribeMany(EnvGroup eg);

	
	public void acquire(EnvGroup eg);
	/**
	 * 获取一个元组（从元组空间中拿走）
	 * @param owner
	 * @param template
	 * @param waitTime
	 */
	public void acquire(ObjectProxy owner, String template, int waitTime);
	/**
	 * waitTime使用DEFAULT_TEMPLATE_WAIT_TIME
	 * @param owner
	 * @param template
	 */
	public void acquire(ObjectProxy owner, String template);
	public void acquireMany(ObjectProxy owner, String template, int waitTime);
	public void acquireMany(ObjectProxy owner, String template);
	public void acquireMany(EnvGroup eg);
//	/**
//	 * 增加拒绝访问策略
//	 * @param acp
//	 */
//	public void addDenyAccessPolicy(AccessControlPolicy acp);
//	/**
//	 * 增加元组分配策略
//	 * @param tap
//	 */
//	public void addTupleAllocationPolicy(TupleAllocationPolicy tap);
}
