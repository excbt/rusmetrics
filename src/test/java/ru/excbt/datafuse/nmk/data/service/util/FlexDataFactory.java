package ru.excbt.datafuse.nmk.data.service.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FlexDataFactory {

    private FlexDataFactory() {
    }

    public static JSONObject createFlexData1 () {
        JSONObject object = new JSONObject();
        try {
            object.put("firstName", "John")
                .put("lastName", "Smith")
                .put("age", 25)
                .put("address", new JSONObject()
                    .put("streetAddress", "21 2nd Street")
                    .put("city", "New York")
                    .put("state", "NY")
                    .put("postalCode", "10021"))
                .put("phoneNumber",  new JSONArray()
                    .put(new JSONObject()
                        .put("type", "home")
                        .put("number", "212 555-1234"))
                    .put(new JSONObject()
                        .put("type", "fax")
                        .put("number", "646 555-4567")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
