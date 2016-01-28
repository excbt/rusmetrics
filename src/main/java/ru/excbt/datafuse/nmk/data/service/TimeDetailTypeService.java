package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.keyname.TimeDetailType;
import ru.excbt.datafuse.nmk.data.repository.keyname.TimeDetailTypeRepository;

@Service
public class TimeDetailTypeService {

	public final static List<String> TIME_DETAIL_1H_24h;

	static {
		List<String> t = new ArrayList<>();
		t.add("1h");
		t.add("24h");
		TIME_DETAIL_1H_24h = Collections.unmodifiableList(t);
	}

	@Autowired
	public TimeDetailTypeRepository timeDetailTypeRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TimeDetailType> findAll() {
		return timeDetailTypeRepository.findAll();
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TimeDetailType> find1h24h() {
		List<TimeDetailType> resultList = timeDetailTypeRepository.findAll();
		return resultList.stream().filter(i -> TIME_DETAIL_1H_24h.contains(i.getKeyname()))
				.collect(Collectors.toList());
	}

}
