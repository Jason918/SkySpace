package com.skyspace;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemPool extends ElementPool{
	private CopyOnWriteArrayList<Item> envItemList = new CopyOnWriteArrayList<Item>();
	private CopyOnWriteArrayList<Item> lockList = new CopyOnWriteArrayList<Item>();
		
	public Item getMatch(Template eg) {
		int index = envItemList.indexOf(eg);
		if (index >= 0)
			return envItemList.get(index);
		else 
			return null;
	}
	public void lock(Item ei) {
		int index = envItemList.indexOf(ei);
		if (index >= 0) {
			lockList.add(envItemList.get(index));
			envItemList.remove(index);
		} else {
			Sky.logger.warning("lock fail,item do not exits:"+ei);
		}
		
	}
	public void unlock(Item ei) {
		int index = lockList.indexOf(ei);
		if (index >= 0) {
			envItemList.add(lockList.get(index));
			lockList.remove(index);
		} else {
			Sky.logger.warning("unlock fail,item do not exits:"+ei);
		}
	}
	public Item get(Item ei) {
		int index = envItemList.indexOf(ei);
		if (index >= 0)
			return envItemList.get(index);
		else 
			return null;
	}
	@Override
	public void add(Element e) {
		if (e instanceof Item == false) {
			Sky.logger.warning("element is not Item");
			return ;
		}
		Item ei = (Item) e;
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
	public void remove(Element e) {
		if (e instanceof Item == false) {
			Sky.logger.warning("element is not Item");
			return ;
		}
		boolean b = envItemList.remove(e);
		if (!b) {
			b = lockList.remove(e);
			if (!b){
				Sky.logger.warning("remove fail,item do not exits:"+e);
			} else {
				Sky.logger.finest("remove success:"+e);
			}
		}
	}
	@Override
	public void buryDead() {
		for(Item ei: envItemList) {
			ei.isAlive();
		}
		for(Item ei: lockList) {
			ei.isAlive();
		}
	}
	public void clear() {
		envItemList.clear();
		lockList.clear();
	}
	public List<String> toListString() {
		ArrayList<String> list = new ArrayList<String>();
		for(Item ei : envItemList) {
			list.add(ei.toString());
		}
		return list;
	}
	

}
