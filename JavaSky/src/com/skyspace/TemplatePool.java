package com.skyspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TemplatePool extends ElementPool{
	private CopyOnWriteArrayList<Template> envGroupList = new CopyOnWriteArrayList<Template>();
	private CopyOnWriteArrayList<MatchItem> matchList;
	/**
	 * 获取能够的匹配ei的EnvGroup，将返回的数据从envGroupList中删除（保留具有many属性的）
	 * @param ei
	 * @return 匹配的集合。如果没有结果就返回空的集合（非null）
	 */
	public Collection<Template> getMatch(Item ei) {
		CopyOnWriteArrayList<Template> egs = new CopyOnWriteArrayList<Template>();
		for(Template eg:envGroupList) {
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
		Template eg;
		ArrayList<Item> eis;
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
		MatchItem(Template eg2) {
			eg = eg2;
		}
		void add(Item ei) {
			if (eis == null) eis = new ArrayList<Item>();
			eis.add(ei);
		}
	}
	@Override
	public void add(Element e) {
		if (e instanceof Template == false) {
			Sky.logger.warning("element is not Item");
			return ;
		}
		envGroupList.add((Template)e);
		e.setContainer(this);
	}
	
	/**
	 * put an ei into a type-many eg;
	 * @param ei
	 * @param eg
	 */
	public void addMatch(Item ei,Template eg) {
		if (matchList == null)
			matchList = new CopyOnWriteArrayList<TemplatePool.MatchItem>();
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
	public void remove(Element e) {
		if (e instanceof Template == false) {
			Sky.logger.warning("element is not Item");
			return ;
		}
		Template eg = (Template) e;
		envGroupList.remove(eg);
		if (eg.isMany() && matchList != null) {
			int index = matchList.indexOf(eg);
			if (index >= 0) {
				MatchItem mi = matchList.get(index);
				Template.default_callback.handleMany(mi.eg, mi.eis);
				matchList.remove(index);
			}
		}
		
	}

	public Template get(Template eg) {
		int index = envGroupList.indexOf(eg);
		if (index >= 0)
			return envGroupList.get(index);
		else
			return null;
	}

	@Override
	public void buryDead() {
		for(Template eg:envGroupList) {
			eg.isAlive();
		}
	}

	public void clear() {
		envGroupList.clear();
		if (matchList != null) matchList.clear();
	}

	public List<String> toListString() {
		ArrayList<String> list = new ArrayList<String>();
		for(Template eg : envGroupList) {
			list.add(eg.toString());
		}
		return list;
	}
}
