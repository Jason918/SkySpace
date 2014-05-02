package com.tuplespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnvGroupPool extends EnvElementPool{
	private CopyOnWriteArrayList<EnvGroup> envGroupList = new CopyOnWriteArrayList<EnvGroup>();
	private CopyOnWriteArrayList<MatchItem> matchList;
	/**
	 * 获取能够的匹配ei的EnvGroup，将返回的数据从envGroupList中删除（保留具有many属性的）
	 * @param ei
	 * @return 匹配的集合。如果没有结果就返回空的集合（非null）
	 */
	public Collection<EnvGroup> getMatch(EnvItem ei) {
		CopyOnWriteArrayList<EnvGroup> egs = new CopyOnWriteArrayList<EnvGroup>();
		for(EnvGroup eg:envGroupList) {
			if (eg.match(ei)) {
				if (!eg.isMany())
					envGroupList.remove(eg);
				egs.add(eg);
			}
		}
		return egs;
	}
	
	/**
	 * Class for storing a list of EnvItems which can match eg.
	 * ei can decide to match which one.
	 * @author jason
	 *
	 */
	class MatchItem {
		EnvGroup eg;
		ArrayList<EnvItem> eis;
		@Override
		public String toString() {
			return "MatchItem;" + eg + "result:" + eis;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MatchItem) {
				return eg.equals(((MatchItem)obj).eg);
			} else 
				return false;
		}
		MatchItem(EnvGroup eg2) {
			eg = eg2;
		}
		void add(EnvItem ei) {
			if (eis == null) eis = new ArrayList<EnvItem>();
			eis.add(ei);
		}
	}
	@Override
	public void add(EnvElement e) {
		if (e instanceof EnvGroup == false) {
			ENV.logger.warning("element is not EnvItem");
			return ;
		}
		envGroupList.add((EnvGroup)e);
		e.setContainer(this);
	}
	
	/**
	 * put an ei into a type-many eg;
	 * @param ei
	 * @param eg
	 */
	public void addMatch(EnvItem ei,EnvGroup eg) {
		if (matchList == null)
			matchList = new CopyOnWriteArrayList<EnvGroupPool.MatchItem>();
		MatchItem mi = new MatchItem(eg);
		int index = matchList.indexOf(mi);
		if (index >= 0) {
			matchList.get(index).add(ei);
		} else {
			mi.add(ei);
			matchList.add(mi);
		}
		
	}
	@Override
	public void remove(EnvElement e) {
		if (e instanceof EnvGroup == false) {
			ENV.logger.warning("element is not EnvItem");
			return ;
		}
		EnvGroup eg = (EnvGroup) e;
		envGroupList.remove(eg);
		if (eg.isMany() && matchList != null) {
			int index = matchList.indexOf(eg);
			if (index >= 0) {
				MatchItem mi = matchList.get(index);
				EnvGroup.default_callback.callbackMany(mi.eg, mi.eis);
				matchList.remove(index);
			}
		}
		
	}

	public EnvGroup get(EnvGroup eg) {
		int index = envGroupList.indexOf(eg);
		if (index >= 0)
			return envGroupList.get(index);
		else
			return null;
	}

	@Override
	public void buryDead() {
		for(EnvGroup eg:envGroupList) {
			eg.isAlive();
		}
	}

	public void clear() {
		envGroupList.clear();
		if (matchList != null) matchList.clear();
	}

	public List<String> toListString() {
		ArrayList<String> list = new ArrayList<String>();
		for(EnvGroup eg : envGroupList) {
			list.add(eg.toString());
		}
		return list;
	}
}
