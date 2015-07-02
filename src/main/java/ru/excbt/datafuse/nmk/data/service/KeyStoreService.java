package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.keyname.KeyStore;
import ru.excbt.datafuse.nmk.data.repository.keyname.KeyStoreRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional(readOnly = true)
public class KeyStoreService {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private KeyStoreRepository keyStoreRepository;

	public String getKeyStoreValue(String keyname) {
		KeyStore ks = keyStoreRepository.findOne(keyname);
		checkNotNull(ks, "keyname " + keyname + " for keystore not found");
		return ks.getKeyValue();
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public String getKeyStoreProperty(String keyname, String propertyName) {

		String srcJson = getKeyStoreValue(keyname);

		if (propertyName == null) {
			return srcJson;
		}

		String result = null;
		try {
			JsonNode rootNode = OBJECT_MAPPER.readTree(srcJson);
			JsonNode valueNode = rootNode.findValue(propertyName);
			if (valueNode != null) {
				result = valueNode.asText();
			}
		} catch (IOException e) {
			throw new PersistenceException(String.format(
					"Can't parse KeyStore json for keyname:%s", keyname));
		}

		return result;
	}
}
