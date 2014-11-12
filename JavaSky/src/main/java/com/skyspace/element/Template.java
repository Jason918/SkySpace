package com.skyspace.element;

import com.skyspace.Sky;
import com.skyspace.json.JSONException;
import com.skyspace.json.JSONObject;
import com.skyspace.util.CallBack;
import com.skyspace.util.ObjectProxy;

import java.util.ArrayList;
import java.util.Date;

/**
 * 用来表示模版, 即一类元组.
 *
 * @author Jason
 */
public class Template extends Element {

    public static final int PRIORITY_HIGH = 0x1000;
    public static final int PRIORITY_LOW = 0x0010;
    public static final int PRIORITY_NORMAL = 0x0100;

    public static final int TYPE_ACQUIRE = 0x01;
    public static final int TYPE_MANY = 0x10;
    public static final int TYPE_SUBSCRIBE = 0x00;
    public static CallBack default_callback = new CallBack() {
        @Override
        public void handleMany(Template tmpl, ArrayList<Item> itList) {
            System.out.println("+++++++++++\nDEFAULT_CALLBACK_MANY：\n" + tmpl
                    + "\n" + itList + "\n+++++++++++++++++++++\n");
        }

        @Override
        public void handle(Template tmpl, Item it) {
            System.out.println("+++++++++++\nDEFAULT_CALLBACK：\n" + tmpl + "\n"
                    + it + "\n+++++++++++++++++++++\n");
        }

        @Override
        public String toString() {
            return "default call back";

        }

        ;
    };
    protected int priority;
    protected CallBack callback;

    public Template(JSONObject jo) {
        setMemberByJSON(jo);
    }

    public Template(ObjectProxy owner, String template, int type, int time) {
        super(owner, time, template, type);
        this.priority = PRIORITY_NORMAL;
    }

    /**
     * 将打包好的字符串生成EnvGroup的对象 JSON格式
     *
     * @param pack
     */
    public Template(String pack) {
        try {
            JSONObject jo = new JSONObject(pack);
            setMemberByJSON(jo);
        } catch (JSONException e) {
            Sky.logger.warning("String pack format illtmplal:" + pack);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Template) {
            Template tmpl = (Template) obj;
            return type == tmpl.type && owner.equals(tmpl.owner)
                    && content.equals(tmpl.content)
                    && priority == tmpl.priority;
        } else if (obj instanceof Item) {
            Item it = (Item) obj;
            return match(it);
        } else if (obj instanceof TemplatePool.MatchItem) {
            TemplatePool.MatchItem mi = (TemplatePool.MatchItem) obj;
            return equals(mi.tmpl);
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
     *
     * @param it 匹配的目标对象
     * @return 这个EnvGroup是否能匹配it
     */
    public boolean match(Item it) {
        Sky.logger.entering("Template", "match", it);
        // check alive:
        if (!isAlive() || !it.isAlive()) {

            return false;
        }
        // permision check
        if (isAcquire()) {
            if (!it.isAcquirable())
                return false;
        } else {
            if (!it.isSubscribale())
                return false;
        }
        Sky.logger.finer("permission pass,start real match");
        String[] tuples = it.content.split(",");
        String[] templates = content.split(",");
        if (tuples.length == templates.length) {
            for (int i = 0; i < tuples.length; i++) {
                if (!templates[i].equals("?")
                        && !templates[i].equals(tuples[i])) {
                    return false;
                }
            }
        } else {
            return false;
        }
        Sky.logger.exiting("Template", "match");
        return true;
    }

    /**
     * 将元组打包,方面传输
     *
     * @return
     */
    public String pack() {
        JSONObject jo = new JSONObject();
        jo.put("owner", owner.toString());
        jo.put("type", type);
        jo.put("template", content);
        jo.put("expire", expire);
        jo.put("priority", priority);
        return jo.toString();
    }

    public boolean prior(Template ac) {
        return priority > ac.priority;
    }

    private void setMemberByJSON(JSONObject jo) {
        try {
            owner = ObjectProxy.buildByString(jo.getString("owner"));
            type = jo.getInt("type");
            content = jo.getString("template");
            expire = jo.getLong("expire");
            priority = jo.getInt("priority");
        } catch (JSONException e) {
            Sky.logger.warning("JSON member error:" + jo);
        }
    }

    @Override
    public String toString() {
        return "Template:\n-[owner]:" + owner
                + "\n-[type]:" + Integer.toHexString(type)
                + "\n-[template]:" + content
                + "\n-[expire]:" + new Date(expire)
                + "\n-[priority]:" + priority
                + "\n-callback:" + callback;
    }

    @Override
    public Element unpack(String pack) {
        return new Template(pack);
    }

    public CallBack getCallback() {
        if (callback == null) {
            return default_callback;
        } else {
            return callback;
        }
    }

    public void setCallback(CallBack cb) {
        callback = cb;
    }

}
