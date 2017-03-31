package ru.excbt.datafuse.nmk.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by kovtonyk on 07.03.2017.
 */
public final class TestUtils {

    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    //public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public static String arrayToString(long[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0;; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }


    public static String listToString(List<?> a) {
        if (a == null)
            return "null";
        int iMax = a.size() - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0;; i++) {
            b.append(a.get(i));
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }


    public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
        T data = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);


        try {
            data = mapper.readValue(jsonPacket, type);
        } catch (Exception e) {
            log.error("Can't read JSON:");
            log.error(jsonPacket);
            log.error("exception: ", e);
        }
        return data;
    }


    public static String objectToJson(Object obj) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule module = new JavaTimeModule();

        mapper.registerModule(module);

        String jsonBody = null;
        String jsonBodyPretty = null;
        try {
            if (obj instanceof String) {
                jsonBody = (String) obj;
                jsonBodyPretty = (String) obj;

            } else {
                jsonBody = mapper.writeValueAsString(obj);
                jsonBodyPretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }

            //log.info("Request JSON:\n{}", jsonBody);
            log.info("Request Pretty JSON:\n{}", jsonBodyPretty);

        } catch (JsonProcessingException e) {
            log.error("Can't create json:\n{}", e);
            e.printStackTrace();
            fail();
        }

        return jsonBody;
    }

    /**
     *
     * @param obj
     * @return
     */
    public static String objectToJsonStr(Object obj) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        String jsonBody = null;
        try {
            if (obj instanceof String) {
                jsonBody = (String) obj;

            } else {
                jsonBody = mapper.writeValueAsString(obj);
            }

        } catch (JsonProcessingException e) {
            log.error("Can't create json: {}", e);
            e.printStackTrace();
            fail();
        }
        return jsonBody;
    }


}
