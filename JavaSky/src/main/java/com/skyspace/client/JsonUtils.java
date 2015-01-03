package com.skyspace.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by jianghaiting on 15/1/3.
 */
public final class JSONUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JSONUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JSONUtils() {
    }

    public static String toJSON(Object object) {
        try {
            if (object == null) {
                return StringUtils.EMPTY;
            }
            return OBJECT_MAPPER.writer().writeValueAsString(object);
        } catch (IOException e) {
            LOG.warn("Serialize " + object + " failed", e);
            return null;
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOG.warn("Deserialize " + json + " failed", e);
            return null;
        }
    }

    public static <T> T fromJSON(String json, TypeReference<T> typeReference) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            LOG.warn("Deserialize " + json + " failed", e);
            return null;
        }
    }
}

