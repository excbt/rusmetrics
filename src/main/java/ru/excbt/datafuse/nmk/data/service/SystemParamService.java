package ru.excbt.datafuse.nmk.data.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.keyname.SystemParam;
import ru.excbt.datafuse.nmk.data.model.types.ParamType;
import ru.excbt.datafuse.nmk.data.repository.keyname.SystemParamRepository;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import java.util.Optional;

/**
 * Сервис для работы с системными параметрами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
@Service
@Transactional(readOnly = true)
public class SystemParamService {

	private final static String PARAM_NOT_FOUND_MSG = "System Param with keyname(%s) not found";

	@Autowired
	private SystemParamRepository systemParamRepository;

	/**
	 *
	 * @param keyname
	 * @return
	 */

	public String getParamValueAsString(final String keyname) {
		SystemParam sp = systemParamRepository.findById(keyname)
            .orElseThrow(() -> new EntityNotFoundException(SystemParam.class, keyname));
		return sp.getParamValue();
	}

	public Optional<String> findOptParamValueAsString(final String keyname) {
		return systemParamRepository.findById(keyname).map(SystemParam::getParamValue);
	}

	/**
	 *
	 * @param keyname
	 * @return
	 */
	public boolean getParamValueAsBoolean(final String keyname) {
		SystemParam sp = systemParamRepository.findById(keyname)
            .orElseThrow(() -> new EntityNotFoundException(SystemParam.class, keyname));

		if (ParamType.BOOLEAN.name().equals(sp.getParamType())) {
			Boolean value = Boolean.valueOf(sp.getParamValue());
			if (value != null) {
				return value.booleanValue();
			}
		}

		throw new PersistenceException(String.format("System Param with keyname(%s) is not BOOLEAN type", keyname));
	}

}
