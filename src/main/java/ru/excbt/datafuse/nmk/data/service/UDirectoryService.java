package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.repository.UDirectoryRepository;

@Service
@Transactional
public class UDirectoryService {

	@Autowired
	private UDirectoryRepository directoryRepository;
	
	@Transactional (readOnly = true)
	public UDirectory findOne(long id) {
		return directoryRepository.findOne(id);
	}
}
