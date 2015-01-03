package com.skyspace.client;

import java.util.List;

/**
 * Created by jianghaiting on 14/12/26.
 */
public interface IResPoolClient {

    /**
     * 获取资源的值。
     * @param name 获取资源的名称。
     * @param clock 获取值对应的时刻
     * @return 资源的值。
     */
    Object getResValue(String name, int clock);

    <T> T getResValue(String name, int clock, Class<T> clazz);

    /**
     * 获取资源的所有时刻的值。
     * @param name 资源的名字。
     * @return 资源的值的列表，列表的长度等于当前clock。
     */
    List<?> getResValue(String name);

    <T> List<T> getResValue(String name, Class<T[]> clazz);

    /**
     * 设置指定资源的值。
     * @param name 资源的名字。
     * @param value 资源的新值。
     */
    void setResValue(String name, Object value);

    /**
     * 让平台时钟前进clockCount个。
     * @param clockCount 时钟前进的量， 精确到0.5。
     */
    void ticktock(double clockCount);
}
