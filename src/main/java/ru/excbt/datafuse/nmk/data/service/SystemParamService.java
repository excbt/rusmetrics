package ru.excbt.datafuse.nmk.data.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.keyname.SystemParam;
import ru.excbt.datafuse.nmk.data.model.types.ParamType;
import ru.excbt.datafuse.nmk.data.repository.keyname.SystemParamRepository;

@Service
public class SystemParamService {

	private final static String PARAM_NOT_FOUND_MSG = "System Param with keyname(%s) not found";

	@Autowired
	private SystemParamRepository systemParamRepository;

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public String getParamValueAsString(final String keyname) {
		SystemParam sp = systemParamRepository.findOne(keyname);
		if (sp == null) {
			throw new PersistenceException(String.format(PARAM_NOT_FOUND_MSG,
					keyname));
		}
		return sp.getParamValue();
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean getParamValueAsBoolean(final String keyname) {
		SystemParam sp = systemParamRepository.findOne(keyname);
		if (sp == null) {
			throw new PersistenceException(String.format(PARAM_NOT_FOUND_MSG,
					keyname));
		}

		if (ParamType.BOOLEAN.name().equals(sp.getParamType())) {
			Boolean value = Boolean.valueOf(sp.getParamValue());
			if (value != null) {
				return value.booleanValue();
			}
		}

		throw new PersistenceException(String.format(
				"System Param with keyname(%s) is not BOOLEAN type", keyname));
	}

}
