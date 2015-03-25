package ru.excbt.datafuse.nmk.data.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;

@Service
@Transactional
public class ContObjectService {

	@Autowired
	private ContObjectRepository contObjectRepository;
	
	@Autowired
	private SubscriberRepository subscrOrgRepository;
	
	@Autowired
	private SubscrUserRepository subscrUserRepository;
	
	
	@Transactional (readOnly = true)
	private List<ContObject> getSubscrContObjects(long userId) {
		
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
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public ContObject findOne(long id) {
		return contObjectRepository.findOne(id);
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContObject> findByFullName(String str) {
		return contObjectRepository.findByFullNameLikeIgnoreCase(str);
	}
	
}
