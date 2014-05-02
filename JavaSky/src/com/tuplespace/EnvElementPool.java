package com.tuplespace;

public abstract class EnvElementPool {
	public abstract void add(EnvElement e);
	public abstract void remove(EnvElement e);
	public abstract void buryDead();
}
