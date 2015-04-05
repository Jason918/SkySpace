package com.skyspace.client;

import java.io.File;
import java.io.IOException;
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


    /**
     * 重置资源池
     */
    void resetResPool();

    /**
     *  file must be in xml format.
     * @param file
     */
    void addResFromFile(File file) throws IOException;


    /**
     * add a Res in respool
     * @param name
     * @param model
     * @param update
     */
    void addRes(String name, String model, String update);

    /**
     *
     * @param name
     */
    void deleteRes(String name);


    /**
     * special ticktock.
     * ticktock until one res execute an auto update.
     * @param force Set true if you want to force jump the clock, not ticktock one by one.
     *              Use at your own risk.
     */
    void ticktockToNextUpdate(boolean force);


    int getClock();
}
