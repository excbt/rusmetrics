package ru.excbt.datafuse.nmk.data.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrOrgRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;

@Service
@Transactional
public class ContObjectService {

	@Autowired
	private ContObjectRepository contObjectRepository;
	
	@Autowired
	private SubscrOrgRepository subscrOrgRepository;
	
	@Autowired
	private SubscrUserRepository subscrUserRepository;
	
	
	@Transactional (readOnly = true)
	public List<ContObject> getSubscrContObjects(long userId) {
		
		List<ContObject> result = null;
		
		SubscrUser su = subscrUserRepository.findOne(userId);
		
		if (su == null) {
			result = Collections.emptyList();
			return result;
		}
		
		//su.
		
		return result;
		
		//return contObjectRepository.selectByUserName(725L);
	}
	
}
