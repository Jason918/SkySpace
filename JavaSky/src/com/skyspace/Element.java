package com.skyspace;

import com.skyspace.util.ObjectProxy;

public abstract class Element {
	/**
	 * 该元素的拥有者
	 */
	protected ObjectProxy owner;
	/**
	 * 该元组所胜的生存时间
	 */
	protected long expire;
	
	/**
	 * 容纳这个元素的集合
	 */
	ElementPool container;

	public Element(ObjectProxy owner, int time) {
		this.owner = owner;
		this.expire = System.currentTimeMillis() + time;
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
