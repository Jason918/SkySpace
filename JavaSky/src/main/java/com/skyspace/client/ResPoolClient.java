package com.skyspace.client;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jianghaiting on 14/12/26.
 */
public class ResPoolClient implements IResPoolClient {

    private static final String TARGET_NAME = "res_pool";

    private static final String CLIENT_NAME = "res_pool_client";
    SkyClient skyClient = new SkyClient();
    
    

    @Override
    public Object getResValue(String name, int clock) {
        String requestId = skyClient.write(TARGET_NAME, "get_res",
                                           Arrays.asList(name, String.valueOf(clock)));

        return skyClient.read(CLIENT_NAME, "get_res", requestId, "?");
    }

    @Override
    public List<?> getResValue(String name) {
        String requestId = skyClient.write(TARGET_NAME, "get_res_all_history", name);
        return skyClient.read(CLIENT_NAME, "get_res_all_history", requestId, "?");
    }

    @Override
    public void setResValue(String name, Object value) {
        skyClient.write(TARGET_NAME, "set_res_value", Arrays.asList(name, String.valueOf(value)));
    }

    @Override
    public void ticktock(float clockCount) {
        skyClient.write(TARGET_NAME, "ticktock", clockCount);
    }
}
