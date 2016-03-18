package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.repository.LocalPlaceRepository;

@Service
public class LocalPlaceService {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceService.class);

	@Autowired
	private LocalPlaceRepository localPlaceRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LocalPlace> selectLocalPlaces() {
		return localPlaceRepository.selectLocalPlaces();
	}

}
