/**
 *
 */
package ru.excbt.datafuse.nmk.web.api;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 *
 */
@Controller
@RequestMapping(value = "/api/subscr/service/buildingType")
public class BuildingTypeController {

    private final BuildingTypeService buildingTypeService;

    protected final ModelMapper modelMapper;

    public BuildingTypeController(BuildingTypeService buildingTypeService, ModelMapper modelMapper) {
        this.buildingTypeService = buildingTypeService;
        this.modelMapper = modelMapper;
    }

    /**
     *
     * @param srcStream
     * @param destClass
     * @return
     */
    private <S, M> List<M> makeModelMapper(Stream<S> srcStream, Class<M> destClass) {
        checkNotNull(srcStream);
        return srcStream.map((i) -> modelMapper.map(i, destClass)).collect(Collectors.toList());
    }



    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getBuildingType() {
		List<BuildingType> resultList = buildingTypeService.selectAllBuildingTypes();
		return ApiResponse.responseOK(makeModelMapper(resultList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE),
				BuildingTypeDto.class));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/category/list", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getBuildingTypeCategory() {
		List<BuildingTypeCategory> resultList = buildingTypeService.selectAllBuildingTypeCategories();
		return ApiResponse.responseOK(makeModelMapper(resultList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE),
				BuildingTypeCategoryDto.class));
	}

}
