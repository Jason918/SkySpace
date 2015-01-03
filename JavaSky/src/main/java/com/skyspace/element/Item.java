package com.skyspace.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skyspace.Sky;
import com.skyspace.json.JSONException;
import com.skyspace.json.JSONObject;
import com.skyspace.util.ObjectProxy;

import java.util.Date;

/**
 * 用来表示元组,包括元组信息,元组的owner,元组的类型,过期时间,
 *
 * @author Jason
 */
public class Item extends Element {
    public static final int TYPE_SINGLETON = 0x001;
    public static final int TYPE_SUBSCRIBALE = 0x010;
    public static final int TYPE_ACQUIRABLE = 0x100;


    static final int PERIOD_LENGTH = 40;

    /**
     * TUPLE_TYPE_SINGLETON (单例) 或者TUPLE_TYPE_MULTITON(多例)
     * 如果是可订阅,则加上TUPLE_TYPE_SUBSCRIBABLE,如果可获取则加上TUPLE_TYPE_ACQUIRABLE;
     * 组合的方式将各个变量的算数或值,如TUPLE_TYPE_SINGLETON|TUPLE_TYPE_SUBSCRIBABLE表示这个元组是单例的且可以被订阅不可以被获取.
     */
    //protected int type;

    /**
     * 元组内容
     */
//	protected String tuple;
    public Item(ObjectProxy owner, int type, String tuple, int time) {
        super(owner, time, tuple, type);
    }

    /**
     * 将打包好的字符串生成Item的对象
     * JSON格式
     *
     * @param pack
     */
    public Item(String pack) {
        try {
            JSONObject jo = new JSONObject(pack);
            setMemberByJSON(jo);
        } catch (JSONException e) {
            Sky.logger.warning("String pack format illegal:" + pack);
        }
    }

    public Item(JSONObject jo) {
        setMemberByJSON(jo);
    }

    @JsonIgnore
    public boolean isSubscribale() {
        return (type & TYPE_SUBSCRIBALE) != 0;
    }

    @JsonIgnore
    public boolean isAcquirable() {
        return (type & TYPE_ACQUIRABLE) != 0;
    }

    @JsonIgnore
    public boolean isSinglton() {
        return (type & TYPE_SINGLETON) != 0;
    }

    /**
     * 将元组打包,方面传输
     * JSON格式
     *
     * @return
     */
    public String pack() {
        JSONObject jo = new JSONObject();
        jo.put("owner", owner.toString());
        jo.put("type", type);
        jo.put("tuple", content);
        jo.put("expire", expire);
        return jo.toString();
    }

    public Item() {
    }

    @Override
    public String toString() {

        return "Item\n-[owner]:" + owner
                + "\n-[type]:0x" + Integer.toHexString(type)
                + ",isSubscribale:" + isSubscribale()
                + ",isAcquirable:" + isAcquirable()
                + ",isSinglton:" + isSinglton()
                + "\n-[tuple]:" + content
                + "\n-[expire]:" + new Date(expire) + "\n";
    }

    private void setMemberByJSON(JSONObject jo) {
        try {
            owner = ObjectProxy.buildByString(jo.getString("owner"));
            type = jo.getInt("type");
            content = jo.getString("tuple"); //TODO change type of tuple
            expire = jo.getLong("expire");
        } catch (JSONException e) {
            Sky.logger.warning("JSON member error:" + jo);
        }
    }

    @Override
    public Element unpack(String pack) {//TODO check format
        return new Item(pack);
    }
}
