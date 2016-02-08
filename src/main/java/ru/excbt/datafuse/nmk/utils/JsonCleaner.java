package ru.excbt.datafuse.nmk.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Работа с JSON
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.05.2015
 *
 */
public class JsonCleaner {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final String NULL_STRING = "null";

	private JsonCleaner() {

	}

	/**
	 * 
	 * @param inputJson
	 * @return
	 * @throws IOException
	 */
	public static String cleanJsonStringValues(String inputJson, String[] cleanString) throws IOException {

		if (inputJson == null) {
			return null;
		}

		if (cleanString == null || cleanString.length == 0) {
			return inputJson;
		}

		JsonNode node = OBJECT_MAPPER.readTree(inputJson);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory factory = new JsonFactory();
		JsonGenerator generator = factory.createGenerator(baos);

		Iterator<String> fieldNames = node.fieldNames();
		generator.writeStartObject();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			String value = node.get(fieldName).asText();

			boolean pass = true;
			for (String s : cleanString) {
				if (value.equals(s)) {
					pass = false;
				}
			}
			if (pass) {
				generator.writeFieldName(fieldName);
				generator.writeString(value);
			}
		}
		generator.writeEndObject();
		generator.close();
		return new String(baos.toByteArray(), UTF8);

	}

	/**
	 * 
	 * @param inputJson
	 * @param cleanArg
	 * @return
	 * @throws IOException
	 */
	public static String cleanJsonStringValue(String inputJson, String cleanArg) throws IOException {
		return cleanJsonStringValues(inputJson, new String[] { cleanArg });
	}

	/**
	 * 
	 * @param inputJson
	 * @return
	 * @throws IOException
	 */
	public static String cleanJsonNullValue(String inputJson) throws IOException {
		return cleanJsonStringValue(inputJson, NULL_STRING);
	}
}
