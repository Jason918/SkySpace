package com.tuplespace;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.security.auth.callback.Callback;

import com.tuplespace.EnvGroupPool.MatchItem;
import com.tuplespace.json.JSONException;
import com.tuplespace.json.JSONObject;
import com.tuplespace.util.CallBack;
import com.tuplespace.util.ObjectProxy;

/**
 * 用来表示模版, 即一类元组.
 * 
 * @author Jason
 *
 */
public class EnvGroup extends EnvElement{
	
	public static final int PRIORITY_HIGH 	= 0x1000;
	public static final int PRIORITY_LOW 	= 0x0010;
	public static final int PRIORITY_NORMAL = 0x0100;
	
	public static final int TYPE_ACQUIRE	= 0x01;
	public static final int TYPE_MANY		= 0x10;
	public static final int TYPE_SUBSCRIBE	= 0x00;
	
	public int priority;
	protected String template;	
	protected int type;
	
	public static CallBack default_callback = new CallBack() {
		@Override
		public void callbackMany(EnvGroup eg, ArrayList<EnvItem> eiList) {
			System.out.println("+++++++++++\nDEFAULT_CALLBACK_MANY：\n"
					+eg + "\n" + eiList + "\n+++++++++++++++++++++\n");
		}
		@Override
		public void callback(EnvGroup eg, EnvItem ei) {
			System.out.println("+++++++++++\nDEFAULT_CALLBACK：\n"
					+eg + "\n" + ei + "\n+++++++++++++++++++++\n");
		}
	};
	
	public EnvGroup(JSONObject jo) {
		setMemberByJSON(jo);
	}
	public EnvGroup(ObjectProxy owner,String template,int type,int time) {
		super(owner,time);
		this.type = type;
		this.template = template;
		this.priority = PRIORITY_NORMAL;
	}

	/**
	 * 将打包好的字符串生成EnvGroup的对象
	 * JSON格式
	 * @param pack
	 */
	public EnvGroup(String pack) {
		try {
			JSONObject jo = new JSONObject(pack);
			setMemberByJSON(jo);
		} catch (JSONException e) {
			ENV.logger.warning("String pack format illegal:"+pack);
		}
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnvGroup) {
			EnvGroup eg = (EnvGroup)obj;
			return type == eg.type && owner.equals(eg.owner) && template.equals(eg.template) && priority == eg.priority; 
		} else if (obj instanceof EnvItem) {
			EnvItem ei = (EnvItem) obj;
			return match(ei);
		} else if (obj instanceof MatchItem){
			MatchItem mi = (MatchItem) obj;
			return equals(mi.eg);
		} else 
			return false;
	}
	
	public boolean isAcquire() {
		return (type & TYPE_ACQUIRE) != 0;
	}
	
	public boolean isMany() {
		return (type & TYPE_MANY) != 0;
	}
	/**
	 * 判断一个EnvItem是否能匹配EnvGroup
	 * @param ei 匹配的目标对象
	 * @return 这个EnvGroup是否能匹配ei
	 */
	public boolean match(EnvItem ei) {
		ENV.logger.entering("EnvGroup", "match", ei);
		//check alive:
		if (!isAlive() || !ei.isAlive()) {
			
			return false;
		}
		//permision check
		if (isAcquire()){
			if (!ei.isAcquirable())
				return false;
		} else {
			if (!ei.isSubscribale())
				return false;
		}
		ENV.logger.finer("permission pass,start real match");
		String[] tuples = ei.tuple.split(",");
		String[] templates = template.split(",");
		if (tuples.length == templates.length){
			for(int i = 0 ; i < tuples.length; i++) {
				if (! templates[i].equals("?") && !templates[i].equals(tuples[i])){
					return false;
				}
			}
		}
		ENV.logger.exiting("EnvGroup", "match");
		return true;
	}
	/**
	 * 将元组打包,方面传输
	 * @return
	 */
	public String pack(){
		JSONObject jo = new JSONObject();
		jo.put("owner", owner.toString());
		jo.put("type", type);
		jo.put("template", template);
		jo.put("expire", expire);
		jo.put("priority", priority);
		return jo.toString();
	}
	
	public boolean prior(EnvGroup ac) {
		return priority > ac.priority;
	}
	
	private void setMemberByJSON(JSONObject jo) {
		try {
			owner = new ObjectProxy(jo.getString("owner"));
			type = jo.getInt("type");
			template = jo.getString("template");
			expire = jo.getLong("expire");
			priority = jo.getInt("priority");
		} catch (JSONException e) {
			ENV.logger.warning("JSON member error:"+jo);
		}
	}
	
	@Override
	public String toString() {
		return "EnvGroup:\n-[owner]:"+owner
			+"\n-[type]:"+Integer.toHexString(type)
			+"\n-[template]:"+template
			+"\n-[expire]:"+new Date(expire)
			+"\n-[priority]:"+priority;
	}
	
	@Override
	public EnvElement unpack(String pack) {
		return new EnvGroup(pack);
	}
	
	
}
