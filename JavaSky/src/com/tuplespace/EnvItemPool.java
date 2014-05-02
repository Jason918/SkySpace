package com.tuplespace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnvItemPool extends EnvElementPool{
	private CopyOnWriteArrayList<EnvItem> envItemList = new CopyOnWriteArrayList<EnvItem>();
	private CopyOnWriteArrayList<EnvItem> lockList = new CopyOnWriteArrayList<EnvItem>();
		
	public EnvItem getMatch(EnvGroup eg) {
		int index = envItemList.indexOf(eg);
		if (index >= 0)
			return envItemList.get(index);
		else 
			return null;
	}
	public void lock(EnvItem ei) {
		int index = envItemList.indexOf(ei);
		if (index >= 0) {
			lockList.add(envItemList.get(index));
			envItemList.remove(index);
		} else {
			ENV.logger.warning("lock fail,item do not exits:"+ei);
		}
		
	}
	public void unlock(EnvItem ei) {
		int index = lockList.indexOf(ei);
		if (index >= 0) {
			envItemList.add(lockList.get(index));
			lockList.remove(index);
		} else {
			ENV.logger.warning("unlock fail,item do not exits:"+ei);
		}
	}
	public EnvItem get(EnvItem ei) {
		int index = envItemList.indexOf(ei);
		if (index >= 0)
			return envItemList.get(index);
		else 
			return null;
	}
	@Override
	public void add(EnvElement e) {
		if (e instanceof EnvItem == false) {
			ENV.logger.warning("element is not EnvItem");
			return ;
		}
		EnvItem ei = (EnvItem) e;
		if (ei.isSinglton()) {
			int index = envItemList.indexOf(ei);
			if (index >= 0) {
				envItemList.get(index).expire = ei.expire;//update expiredate!!
				return;
			}
		}
		envItemList.add(ei);
		ei.setContainer(this);
	}
	@Override
	public void remove(EnvElement e) {
		if (e instanceof EnvItem == false) {
			ENV.logger.warning("element is not EnvItem");
			return ;
		}
		boolean b = envItemList.remove(e);
		if (!b) {
			b = lockList.remove(e);
			if (!b){
				ENV.logger.warning("remove fail,item do not exits:"+e);
			} else {
				ENV.logger.finest("remove success:"+e);
			}
		}
	}
	@Override
	public void buryDead() {
		for(EnvItem ei: envItemList) {
			ei.isAlive();
		}
		for(EnvItem ei: lockList) {
			ei.isAlive();
		}
	}
	public void clear() {
		envItemList.clear();
		lockList.clear();
	}
	public List<String> toListString() {
		ArrayList<String> list = new ArrayList<String>();
		for(EnvItem ei : envItemList) {
			list.add(ei.toString());
		}
		return list;
	}
	

}
