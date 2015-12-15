package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElProfile;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElTech;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataElService;
import ru.excbt.datafuse.nmk.web.api.support.RequestDataSelectorPaged;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataElController extends SubscrContServiceDataWebApiController {

	@Autowired
	private ContServiceDataElService contServiceDataElService;

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @param dataDateSort
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElCons/{timeDetailType}/{contZPointId}/paged",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElConsPaged(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr,
			@RequestParam(value = "dataDateSort", required = false, defaultValue = "desc") String dataDateSort,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		RequestDataSelectorPaged<ContServiceDataElCons> pageSelector = new RequestDataSelectorPaged<ContServiceDataElCons>() {

			@Override
			public Page<ContServiceDataElCons> selectDataPaged(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectConsByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay(), pageRequest);
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceDataPaged(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataDateSort, pageable, pageSelector);

		return resultResponse;

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @param dataDateSort
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElProfile/{timeDetailType}/{contZPointId}/paged",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElProfilePaged(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr,
			@RequestParam(value = "dataDateSort", required = false, defaultValue = "desc") String dataDateSort,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		RequestDataSelectorPaged<ContServiceDataElProfile> pageSelector = new RequestDataSelectorPaged<ContServiceDataElProfile>() {

			@Override
			public Page<ContServiceDataElProfile> selectDataPaged(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectProfileByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay(), pageRequest);
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceDataPaged(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataDateSort, pageable, pageSelector);

		return resultResponse;

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @param dataDateSort
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElTech/{timeDetailType}/{contZPointId}/paged",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElTechPaged(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr,
			@RequestParam(value = "dataDateSort", required = false, defaultValue = "desc") String dataDateSort,
			@PageableDefault(size = DEFAULT_PAGE_SIZE, page = 0) Pageable pageable) {

		RequestDataSelectorPaged<ContServiceDataElTech> pageSelector = new RequestDataSelectorPaged<ContServiceDataElTech>() {

			@Override
			public Page<ContServiceDataElTech> selectDataPaged(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectTechByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay(), pageRequest);
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceDataPaged(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataDateSort, pageable, pageSelector);

		return resultResponse;

	}

}
