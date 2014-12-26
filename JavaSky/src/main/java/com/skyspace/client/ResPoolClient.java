package com.skyspace.client;

import java.util.List;

/**
 * Created by jianghaiting on 14/12/26.
 */
public class ResPoolClient implements IResPoolClient {
    @Override
    public Object getResValue(String name, int clock) {
        return null;
    }

    @Override
    public List<?> getResValue(String name) {
        return null;
    }

    @Override
    public void setResValue(String name, Object value) {

    }

    @Override
    public void ticktock(float clockCount) {

    }
}
