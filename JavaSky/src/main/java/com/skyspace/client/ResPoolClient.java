package com.skyspace.client;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jianghaiting on 14/12/26.
 */
public class ResPoolClient implements IResPoolClient {

    private static final Logger LOG = LoggerFactory.getLogger(ResPoolClient.class);

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
            LOG.debug("getResValues[name={},clock={}] = {}", name, clock, result);
            return null;
        }
        T ret = decodeResult(result.get(2), clazz);
        LOG.debug("getResValue[name={},clock={}]={}", name, clock, ret);
        return ret;
    }

    @Override
    public List<?> getResValue(String name) {
        return getResValue(name, Object[].class);
    }


    @Override
    public <T> List<T> getResValue(String name, Class<T[]> clazz) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        String requestId = skyClient.write(TARGET_NAME, "get_res_values", encodeParams(params));
        List<String> result = skyClient.take(CLIENT_NAME, requestId, "?");
        if (CollectionUtils.isEmpty(result) || result.size() < 3) {
            LOG.debug("getResValues[name={}] = {}", name, result);
            return null;
        }
        List<T> ret = Arrays.asList(decodeResult(result.get(2), clazz));
        LOG.debug("getResValues[name={}] = {}", name, ret);
        return ret;
    }

    @Override
    public void setResValue(String name, Object value) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("value", value);
        String requestId = skyClient.write(TARGET_NAME, "set_res_value", encodeParams(params));
        skyClient.take(CLIENT_NAME, requestId, "?");
    }

    @Override
    public void ticktock(double clockCount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("time", clockCount);
        String requestId = skyClient.write(TARGET_NAME, "ticktock", encodeParams(params));
        skyClient.take(CLIENT_NAME, requestId, "?");
    }

    @Override
    public void resetResPool() {
        Map<String, Object> params = new HashMap<String, Object>();
        String requestId = skyClient.write(TARGET_NAME, "reset_res_pool", encodeParams(params));
        skyClient.take(CLIENT_NAME, requestId, "?");
    }

    @Override
    public void addResFromFile(File file) throws IOException {
        String context = new String(Files.readAllBytes(Paths.get(file.toURI())));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("context", context);
        String requestId = skyClient.write(TARGET_NAME, "add_res_from_xml_context", encodeParams(params));
        skyClient.take(CLIENT_NAME, requestId, "?");
    }

    @Override
    public void addRes(String name, String model, String update) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("model", model);
        params.put("update", update);
        String requestId = skyClient.write(TARGET_NAME, "add_res", encodeParams(params));
        skyClient.take(CLIENT_NAME, requestId, "?");
    }

    @Override
    public void deleteRes(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        String requestId = skyClient.write(TARGET_NAME, "delete_res", encodeParams(params));
        skyClient.take(CLIENT_NAME, requestId, "?");
    }

    @Override
    public void ticktockToNextUpdate(boolean force) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("force", force);
        String requestId = skyClient.write(TARGET_NAME, "ticktock_to_next_update", encodeParams(params));
        skyClient.take(CLIENT_NAME, requestId, "?");
    }

    @Override
    public int getClock() {
        Map<String, Object> params = new HashMap<String, Object>();
        String requestId = skyClient.write(TARGET_NAME, "get_clock", encodeParams(params));

        List<String> result = skyClient.take(CLIENT_NAME, requestId, "?");
        if (CollectionUtils.isEmpty(result) || result.size() < 3) {
            return -1;
        }
        Integer ret = decodeResult(result.get(2), Integer.class);
        LOG.debug("getClock={}", ret);
        return ret;
    }


}
