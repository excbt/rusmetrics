package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.keyname.TimeDetailType;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.keyname.TimeDetailTypeRepository;

/**
 * Сервис для работы с типами детализации
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.01.2015
 *
 */
@Service
public class TimeDetailTypeService {

	public final static List<String> TIME_DETAIL_1H_24h;
	public final static List<String> TIME_DETAIL_24H_24HAbs;

	static {
		{
			List<String> t = new ArrayList<>();
			t.add(TimeDetailKey.TYPE_1H.getKeyname());
			t.add(TimeDetailKey.TYPE_24H.getKeyname());
			TIME_DETAIL_1H_24h = Collections.unmodifiableList(t);
		}
		{
			List<String> t = new ArrayList<>();
			t.add(TimeDetailKey.TYPE_24H.getKeyname());
			t.add(TimeDetailKey.TYPE_24H.getAbsPair());
			TIME_DETAIL_24H_24HAbs = Collections.unmodifiableList(t);
		}
	}

	@Autowired
	public TimeDetailTypeRepository timeDetailTypeRepository;

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<TimeDetailType> findAll() {
		return timeDetailTypeRepository.findAll();
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<TimeDetailType> select24h24hAbs() {
		List<TimeDetailType> resultList = timeDetailTypeRepository.findAll();
		return resultList.stream().filter(i -> TIME_DETAIL_24H_24HAbs.contains(i.getKeyname()))
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<TimeDetailType> select1h24h() {
		List<TimeDetailType> resultList = timeDetailTypeRepository.findAll();
		return resultList.stream().filter(i -> TIME_DETAIL_1H_24h.contains(i.getKeyname()))
				.collect(Collectors.toList());
	}

}
