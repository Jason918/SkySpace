package com.skyspace;

import com.skyspace.element.Item;
import com.skyspace.element.Template;
import com.skyspace.util.CallBack;

import java.util.List;


/**
 * 元组空间接口
 * TODO: 增加更加详细的说明
 * <p/>
 * 关于Write、Subscribe、Acquire这几个操作都是异步的。
 * Read 和 Take操作是同步的。
 * 在ObjectAdapter中有Owner的地址,用Multicast公布结果.
 *
 * @author Jason
 */
public interface ISkyEntry {
    public static final int DEFAULT_TUPLE_LEASE_TIME = 5;
    public static final int DEFAULT_RESPONSE_TIME = 1;
    public static final int DEFAULT_TEMPLATE_WAIT_TIME = 5;

    /**
     * 设置最久的响应时间，即在response_time内响应的认为是同时响应。
     *
     * @param time
     */
    public void setResponseTime(int time);

    public void setTimeOut(int time);

    public void setLeaseTime(int time);

    /**
     * 将元组写入元组空间.
     *
     * @param it 要写入元组空间的元组.
     */
    public void write(Item it);

    /**
     * 异步从元组空间读取元组
     *
     * @param tmpl 订阅的模版
     * @param cb   读到之后的回调函数。
     */
    public void subscribe(Template tmpl, CallBack cb);

    /**
     * 同步从元组空间读取元组
     *
     * @param tmpl
     * @return 读取到的元组集合
     */
    public List<Item> read(Template tmpl);

    /**
     * 异步从元组空间拿走元组
     *
     * @param tmpl
     * @param cb   拿到之后的回调函数
     */
    public void acquire(Template tmpl, CallBack cb);

    /**
     * 同步从元组空间拿走元组
     *
     * @param tmpl
     * @return 得到的元组集合
     */
    public List<Item> take(Template tmpl);

}
