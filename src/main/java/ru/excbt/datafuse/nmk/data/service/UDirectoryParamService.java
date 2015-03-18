package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

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
		
		
		UDirectoryParam recordToSave = null;
		
		if (arg.isNew()) {
			recordToSave = new UDirectoryParam();
		} else {
			recordToSave = repository.findOne(arg.getId());
		}
		
		checkNotNull(recordToSave);
		
		checkArgument(recordToSave.getVersion() == arg.getVersion());
		
		recordToSave.setDirectory(arg.getDirectory());
		recordToSave.setParamType(arg.getParamType());
		recordToSave.setParamName(arg.getParamName());
		
		return repository.save(recordToSave);
	}

	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(UDirectoryParam arg) {
		repository.delete(arg);
	}

	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(long id) {
		if (!repository.exists(id)) {
			throw new PersistenceException();
		}
		repository.delete(id);
	}
	
	@Transactional(readOnly = true)
	public UDirectoryParam findOne(long id) {
		UDirectoryParam result = repository.findOne(id);
		if (result != null) {
			checkNotNull(result.getDirectory().getId());
		}
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<UDirectoryParam> selectDirectoryParams (long directoryId) {
		return repository.selectDirectoryParams(directoryId);
	}
	
	
}
