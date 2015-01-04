package com.skyspace.client;

import com.skyspace.element.Item;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jianghaiting on 14/12/26.
 */
public class SkyClient {

    public static String SKY_SERVER = "http://127.0.0.1:9000";
    public static String WRITE_URL = "/skyentry/write";
    public static String READ_URL = "/skyentry/read";
    public static String TAKE_URL = "/skyentry/take";

    RestTemplate restTemplate = new RestTemplate();

    public String write(String... tuple) {
        String content = StringUtils.join(tuple, ",");
        String content_id = DigestUtils.sha1Hex(String.valueOf(Math.random()));
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("content", content + "," + content_id);
        data.add("type", -1);
        data.add("expire", 3000);
        restTemplate.postForObject(SKY_SERVER + WRITE_URL, data, String.class);
        return content_id;
    }

    public List<String> read(String... template) {
        return readOrTake(SKY_SERVER + READ_URL, template);
    }

    public List<String> take(String... template) {
        return readOrTake(SKY_SERVER + TAKE_URL, template);
    }

    private List<String> readOrTake(String requestUrl, String[] template) {
        String content = StringUtils.join(template, ",");
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("content", content);
        data.add("isMulti", false);
        data.add("timeout", 500);
        Item[] items = restTemplate.postForObject(requestUrl, data, Item[].class);

        if (items != null && items.length == 1) {
            String content1 = items[0].getContent();
            if (StringUtils.isBlank(content1)) {
                return null;
            } else {
                return Arrays.asList(content1.split(","));
            }
        } else {
            return null;
        }
    }
}
