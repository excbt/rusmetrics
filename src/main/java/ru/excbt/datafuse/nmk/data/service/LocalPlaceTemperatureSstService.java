package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.repository.LocalPlaceTemperatureSstRepository;

@Service
public class LocalPlaceTemperatureSstService {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceTemperatureSstService.class);

	@Autowired
	private LocalPlaceTemperatureSstRepository localPlaceTemperatureSstRepository;

	/**
	 * 
	 * @param localPlaceId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LocalPlaceTemperatureSst> selectByLocalPlace(Long localPlaceId) {
		return localPlaceTemperatureSstRepository.selectByLocalPlace(localPlaceId);
	}

}
