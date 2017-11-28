package ru.excbt.datafuse.nmk.web.rest.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

public final class JsonResultViewer {


    private Consumer<String> printer;

    public JsonResultViewer(Consumer<String> printer) {
        this.printer = printer;
    }

    /**
     *
     * @param resultActions
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public static final String arrayBeatifyResult(ResultActions resultActions) throws UnsupportedEncodingException, JSONException {
        String resultJson = resultActions.andReturn().getResponse().getContentAsString();

        JSONArray resultJsonArray = new JSONArray(resultJson);
        return resultJsonArray.toString(4);
    }

    /**
     *
     * @param resultActions
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public static final String objectBeatifyResult(ResultActions resultActions) throws UnsupportedEncodingException, JSONException {
        String resultJson = resultActions.andReturn().getResponse().getContentAsString();

        JSONObject resultJsonObject = new JSONObject(resultJson);
        return resultJsonObject.toString(4);
    }

    /**
     *
     * @param result
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public static final String objectBeatifyResult(MvcResult result) throws UnsupportedEncodingException, JSONException {
        String resultJson = result.getResponse().getContentAsString();

        JSONObject resultJsonObject = new JSONObject(resultJson);
        return resultJsonObject.toString(4);
    }

    /**
     * @param result
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public static final String arrayBeatifyResult(MvcResult result) throws UnsupportedEncodingException, JSONException {
        String resultJson = result.getResponse().getContentAsString();

        JSONArray resultJsonObject = new JSONArray(resultJson);
        return resultJsonObject.toString(4);
    }


    /**
     *
     */
    public final ResultHandler objectBeatifyResultHandler = result -> {
        String resultJson = result.getResponse().getContentAsString();
        JSONObject resultJsonObject = new JSONObject(resultJson);
        if (printer != null)  printer.accept(resultJsonObject.toString(4));
    };
}
