package com.skyspace.client;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by jianghaiting on 14/12/26.
 */
public class ResPoolClientMocker implements IResPoolClient {
    int clock = 0;

    @Override
    public Object getResValue(String name, int clock) {
        if (StringUtils.equals(name, "temp")) {
            return (int) (Math.random() * 30);
        } else if (StringUtils.equals(name, "hum")) {
            return Math.random() * 100;
        }
        return null;
    }

    @Override
    public <T> T getResValue(String name, int clock, Class<T> clazz) {
        return null;
    }

    @Override
    public List<Object> getResValue(String name) {
        if (StringUtils.equals(name, "temp")) {
//            List<Object> ret = new ArrayList<Integer>();
//            for (int i = 0; i < clock; i++) {
//                ret.add((int) (Math.random() * 100));
//            }
//            return ret;
        }
        return null;
    }

    @Override
    public <T> List<T> getResValue(String name, Class<T[]> clazz) {
        return null;
    }

    @Override
    public void setResValue(String name, Object value) {

    }

    @Override
    public void ticktock(double clockCount) {

    }



}
