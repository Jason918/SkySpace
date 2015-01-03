package com.skyspace.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skyspace.Sky;
import com.skyspace.util.ObjectProxy;

public abstract class Element {
    /**
     * 该元素的拥有者
     */
    protected ObjectProxy owner;
    /**
     * 该元组失效时间, 单位ms, 从1970年开始计数.
     */
    protected long expire;
    /**
     * 该元素的内容
     */
    protected String content;
    /**
     * 该元素的类型
     */
    protected int type;
    /**
     * 容纳这个元素的集合
     */
    ElementPool container;

    public Element(ObjectProxy owner, int time, String content, int type) {
        this.owner = owner;
        this.expire = System.currentTimeMillis() + time;
        this.content = content;
        this.type = type;

    }

    public Element() {
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ElementPool getContainer() {
        return container;
    }

    public void setContainer(ElementPool pool) {
        container = pool;
    }

    @JsonIgnore
    public boolean isAlive() {
        boolean dead = System.currentTimeMillis() > expire;
        if (dead) {
            if (container != null) {
                container.remove(this);
                Sky.logger.info("remove:" + this);
            }
        }
        return !dead;
    }

    public abstract String pack();

    public abstract Element unpack(String pack);

    public ObjectProxy getOwner() {
        return owner;
    }

    public void setOwner(ObjectProxy owner) {
        this.owner = owner;
    }

    @JsonIgnore
    public long getTimeout() {
        return Math.max(this.expire - System.currentTimeMillis(),0);
    }

}
