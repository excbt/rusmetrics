/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.01.2017
 * 
 */
public interface JsonModel {

	static final Logger log = LoggerFactory.getLogger(JsonModel.class);

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	public default String toJson() throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(this);
	}

	/**
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	public default String toJsonSilent() {
		String jsonBody = null;
		try {
			jsonBody = OBJECT_MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			log.error("Can't create json: {}", e);
			e.printStackTrace();
			jsonBody = null;
		}
		return jsonBody;
	}

}
