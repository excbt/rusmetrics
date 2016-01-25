package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContServiceTypeRepository;

/**
 * Сервис, возвращающий типы обслуживания ContServiceType
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.01.2016
 *
 */
@Service
public class ContServiceTypeService {

	@Autowired
	private ContServiceTypeRepository contServiceTypeRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceType> selectContServiceType() {
		List<ContServiceType> preResult = contServiceTypeRepository.findAll();
		preResult.sort((a, b) -> a.getServiceOrder().compareTo(b.getServiceOrder()));
		return preResult;
	}
}
