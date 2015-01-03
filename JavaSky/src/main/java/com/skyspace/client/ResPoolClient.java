package com.skyspace.client;


import org.apache.commons.codec.binary.Base64;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jianghaiting on 14/12/26.
 */
public class ResPoolClient implements IResPoolClient {

    private static final String TARGET_NAME = "res_pool";

    private static final String CLIENT_NAME = "res_pool_client";
    SkyClient skyClient = new SkyClient();

    private <T> T decodeResult(String result, Class<T> clazz) {
        String decode64 = new String(Base64.decodeBase64(result));
        return JSONUtils.fromJSON(decode64, clazz);
    }

    private String encodeParams(Map<String, Object> params) {
        String jsonString = JSONUtils.toJSON(params);
        return Base64.encodeBase64String(jsonString.getBytes());
    }

    @Override
    public Object getResValue(String name, int clock) {
        return getResValue(name, clock, Object.class);
    }

    @Override
    public <T> T getResValue(String name, int clock, Class<T> clazz) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("clock", clock);

        String requestId = skyClient.write(TARGET_NAME, "get_res_value", encodeParams(params));

        List<String> result = skyClient.take(CLIENT_NAME, requestId, "?");
        if (CollectionUtils.isEmpty(result) || result.size() < 3) {
            return null;
        }
        return decodeResult(result.get(2), clazz);
    }

    @Override
    public List<?> getResValue(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        String requestId = skyClient.write(TARGET_NAME, "get_res_values", encodeParams(params));
        List<String> result = skyClient.take(CLIENT_NAME, requestId, "?");
        if (CollectionUtils.isEmpty(result) || result.size() < 3) {
            return null;
        }
        return Arrays.asList(decodeResult(result.get(2), Object[].class));
    }


    @Override
    public <T> List<T> getResValue(String name, Class<T[]> clazz) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        String requestId = skyClient.write(TARGET_NAME, "get_res_values", encodeParams(params));
        List<String> result = skyClient.take(CLIENT_NAME, requestId, "?");
        if (CollectionUtils.isEmpty(result) || result.size() < 3) {
            return null;
        }
        return Arrays.asList(decodeResult(result.get(2), clazz));
    }

    @Override
    public void setResValue(String name, Object value) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("value", value);
        skyClient.write(TARGET_NAME, "set_res_value", encodeParams(params));
    }

    @Override
    public void ticktock(double clockCount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("time", clockCount);
        skyClient.write(TARGET_NAME, "ticktock", encodeParams(params));
    }
}
