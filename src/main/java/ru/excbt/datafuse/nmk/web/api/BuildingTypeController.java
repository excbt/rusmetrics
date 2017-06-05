/**
 *
 */
package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.dto.BuildingTypeCategoryDto;
import ru.excbt.datafuse.nmk.data.model.dto.BuildingTypeDto;
import ru.excbt.datafuse.nmk.data.model.keyname.BuildingType;
import ru.excbt.datafuse.nmk.data.model.keyname.BuildingTypeCategory;
import ru.excbt.datafuse.nmk.data.service.BuildingTypeService;
import ru.excbt.datafuse.nmk.web.api.support.WebApiController;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 *
 */
@Controller
@RequestMapping(value = "/api/subscr/service/buildingType")
public class BuildingTypeController extends WebApiController {

	@Autowired
	private BuildingTypeService buildingTypeService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getBuildingType() {
		List<BuildingType> resultList = buildingTypeService.selectAllBuildingTypes();
		return responseOK(makeModelMapper(resultList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE),
				BuildingTypeDto.class));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/category/list", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getBuildingTypeCategory() {
		List<BuildingTypeCategory> resultList = buildingTypeService.selectAllBuildingTypeCategories();
		return responseOK(makeModelMapper(resultList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE),
				BuildingTypeCategoryDto.class));
	}

}
