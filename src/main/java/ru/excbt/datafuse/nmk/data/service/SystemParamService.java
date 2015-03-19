package ru.excbt.datafuse.nmk.data.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.keyname.SystemParam;
import ru.excbt.datafuse.nmk.data.repository.SystemParamRepository;

@Service
@Transactional
public class SystemParamService {

	@Autowired
	private SystemParamRepository systemParamRepository;
	
	@Transactional(readOnly = true)
	public String getParamValue(final String keyname) {
		SystemParam sp = systemParamRepository.findOne(keyname);
		if (sp == null) {
			throw new PersistenceException(String.format("System Param with keyname(%s) not found", keyname));
		}
		return sp.getParamValue(); 
	}
}
