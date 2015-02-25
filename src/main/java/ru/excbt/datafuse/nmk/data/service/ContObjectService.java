package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;

@Service
@Transactional
public class ContObjectService {

	@Autowired
	private ContObjectRepository contObjectRepository;
	
	@Transactional (readOnly = true)
	public List<ContObject> getUserContObjects() {
		return contObjectRepository.selectByUserName(725L);
	}
	
}
