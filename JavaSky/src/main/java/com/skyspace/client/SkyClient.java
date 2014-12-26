package com.skyspace.client;

import com.skyspace.element.Item;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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

    public String write(Object... contentItems) {
        String content = encodeContent(contentItems);
        String hash = DigestUtils.sha1Hex(content);
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("content", content);
        data.add("type", -1);
        data.add("expire", 3000);
        restTemplate.postForObject(SKY_SERVER + WRITE_URL, data, String.class);
        return hash;
    }

    public List<Object> read(Object... contentItems) {
        String content = encodeContent(contentItems);
//        String hash = DigestUtils.sha1Hex(content);
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("content", content);
        data.add("isMulti", false);
        data.add("timeout", 500);
        Item[] items = restTemplate.postForObject(SKY_SERVER + READ_URL, data, Item[].class);

        if (items != null && items.length == 1) {
            return decodeContent(items[0].getContent());
        } else {
            return null;
        }
    }

    public List<Object> take(Object... contentItems) {
        String content = encodeContent(contentItems);
//        String hash = DigestUtils.sha1Hex(content);
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("content", content);
        data.add("isMulti", false);
        data.add("timeout", 500);
        Item[] items = restTemplate.postForObject(SKY_SERVER + WRITE_URL, data, Item[].class);

        if (items != null && items.length == 1) {
            return decodeContent(items[0].getContent());
        } else {
            return null;
        }
    }

    private List<Object> decodeContent(String content) {
        String[] contents = content.split(",");
        List<Object> objs = new ArrayList<Object>();
        for (String c : contents) {
            String decodedContent = new String(Base64.decodeBase64(c));
            objs.add(decodedContent);
        }
        return objs;
    }

    private String encodeContent(Object[] contentItems) {
        List<String> encodedContentString = new ArrayList<String>();
        for (Object item : contentItems) {
            String str = item.toString();
            if (StringUtils.equals(str, "?")) {
                encodedContentString.add(str);
            } else {
                encodedContentString.add(Base64.encodeBase64String(str.getBytes()));
            }
        }
        return StringUtils.join(encodedContentString, ",");
    }


}
