/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.keyname.BuildingType;
import ru.excbt.datafuse.nmk.data.model.keyname.BuildingTypeCategory;
import ru.excbt.datafuse.nmk.data.repository.keyname.BuildingTypeCategoryRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.BuildingTypeRepository;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 * 
 */
@Service
public class BuildingTypeService {

	@Autowired
	private BuildingTypeRepository buildingTypeRepository;

	@Autowired
	private BuildingTypeCategoryRepository buildingTypeCategoryRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<BuildingType> selectAllBuildingTypes() {
		return buildingTypeRepository.selectAll();
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<BuildingTypeCategory> selectAllBuildingTypeCategories() {
		return Lists.newArrayList(buildingTypeCategoryRepository.findAll());
	}

}
