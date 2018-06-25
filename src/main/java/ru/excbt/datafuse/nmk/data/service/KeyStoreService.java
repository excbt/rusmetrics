package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import ru.excbt.datafuse.nmk.data.model.keyname.KeyStore;
import ru.excbt.datafuse.nmk.data.repository.keyname.KeyStoreRepository;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

/**
 * Сервис для работы с параметрами системы
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.07.2015
 *
 */
@Service
@Transactional( readOnly = true)
public class KeyStoreService {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private KeyStoreRepository keyStoreRepository;

	/**
	 *
	 * @param keyname
	 * @return
	 */
	public String getKeyStoreValue(String keyname) {
		KeyStore ks = keyStoreRepository.findById(keyname)
            .orElseThrow(() -> new EntityNotFoundException(KeyStore.class, keyname));
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
			throw new PersistenceException(String.format("Can't parse KeyStore json for keyname:%s", keyname));
		}

		return result;
	}
}
