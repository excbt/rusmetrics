package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryParamRepository;

@Service
@Transactional
public class UDirectoryParamService implements SecuredServiceRoles {

	@Autowired
	private UDirectoryParamRepository repository;
	
	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public UDirectoryParam save(UDirectoryParam arg) {
		checkNotNull(arg);
		return repository.save(arg);
	}

	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(UDirectoryParam arg) {
		repository.delete(arg);
	}
	
	@Transactional(readOnly = true)
	public UDirectoryParam findOne(long id) {
		return repository.findOne(id);
	}
	
	@Transactional(readOnly = true)
	public List<UDirectoryParam> selectDirectoryParams (long directoryId) {
		return repository.selectDirectoryParams(directoryId);
	}
}
