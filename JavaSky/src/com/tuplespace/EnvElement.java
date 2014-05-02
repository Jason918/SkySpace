package com.tuplespace;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tuplespace.util.ObjectProxy;

public abstract class EnvElement {
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
	EnvElementPool container;

	public EnvElement(ObjectProxy owner, int time) {
		this.owner = owner;
		this.expire = System.currentTimeMillis() + time;
	}
	public EnvElement() {}

	public boolean isAlive() {
		boolean dead = System.currentTimeMillis() > expire;
		if (dead) {
			if (container != null) {
				container.remove(this);
				ENV.logger.info("remove:"+this);
			}
		}
		return !dead;
	}
	public void setContainer(EnvElementPool pool) {
		container = pool;		
	}
	public abstract String pack();
	public abstract EnvElement unpack(String pack);
}
