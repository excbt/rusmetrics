package ru.excbt.datafuse.nmk.data.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kovtonyk on 03.04.2017.
 */
public class JsonMapperUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonMapperUtils.class);


    public static String objectToJson(Object obj) {
        return objectToJson(obj, false);
    }


    public static String objectToJson(Object obj, boolean pretty) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule module = new JavaTimeModule();

        mapper.registerModule(module);

        String jsonBody;
        String jsonBodyPretty;
        try {
            if (obj instanceof String) {
                jsonBody = (String) obj;
                jsonBodyPretty = (String) obj;
            } else {
                jsonBody = mapper.writeValueAsString(obj);
                jsonBodyPretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            log.error("Can't create json:\n{}", e);
            return null;
        }

        return pretty ? jsonBodyPretty : jsonBody;
    }



    public static <T> T jsonToObject(final String json, final TypeReference<T> type) {
        T data = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);


        try {
            data = mapper.readValue(json, type);
        } catch (Exception e) {
            log.error("Can't read JSON:\n" + json);
            log.error("exception: ", e);
        }
        return data;
    }



}
