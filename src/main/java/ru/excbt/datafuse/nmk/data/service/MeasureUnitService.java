package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.repository.keyname.MeasureUnitRepository;

@Service
public class MeasureUnitService {

	@Autowired
	private MeasureUnitRepository measureUnitRepository;

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<MeasureUnit> selectMeasureUnits() {

		List<MeasureUnit> resultList = measureUnitRepository.findAll();

		return ObjectFilters.deletedFilter(resultList);

	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<MeasureUnit> selectMeasureUnitsSame(String measureUnit) {

		List<MeasureUnit> resultList = measureUnitRepository.selectMeasureUnitsSame(measureUnit);

		return ObjectFilters.deletedFilter(resultList);

	}

}
