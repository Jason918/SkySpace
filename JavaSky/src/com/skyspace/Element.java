package com.skyspace;

import com.skyspace.util.ObjectProxy;

public abstract class Element {
	/**
	 * 该元素的拥有者
	 */
	protected ObjectProxy owner;
	/**
	 * 该元组失效时间, 单位ms, 从1970年开始计数.
	 */
	protected long expire;
	
	/**
	 * 该元素的内容
	 */
	protected String content;
	
	/**
	 * 该元素的类型
	 */
	protected int type;
	
	/**
	 * 容纳这个元素的集合
	 */
	ElementPool container;

	public Element(ObjectProxy owner, int time, String content, int type) {
		this.owner = owner;
		this.expire = System.currentTimeMillis() + time;
		this.content = content;
		this.type = type;
		
	}
	public Element() {}

	public boolean isAlive() {
		boolean dead = System.currentTimeMillis() > expire;
		if (dead) {
			if (container != null) {
				container.remove(this);
				Sky.logger.info("remove:"+this);
			}
		}
		return !dead;
	}
	public void setContainer(ElementPool pool) {
		container = pool;		
	}
	public abstract String pack();
	public abstract Element unpack(String pack);
}
