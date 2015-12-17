package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

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
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataSummary;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataElService;
import ru.excbt.datafuse.nmk.web.api.support.RequestAnyDataSelector;
import ru.excbt.datafuse.nmk.web.api.support.RequestListDataSelector;
import ru.excbt.datafuse.nmk.web.api.support.RequestPageDataSelector;

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

		RequestPageDataSelector<ContServiceDataElCons> dataSelector = new RequestPageDataSelector<ContServiceDataElCons>() {

			@Override
			public Page<ContServiceDataElCons> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				checkNotNull(pageRequest);
				return contServiceDataElService.selectConsByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay(), pageRequest);
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceDataPaged(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataDateSort, pageable, dataSelector);

		return resultResponse;

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElCons/{timeDetailType}/{contZPointId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElCons(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		RequestListDataSelector<ContServiceDataElCons> dataSelector = new RequestListDataSelector<ContServiceDataElCons>() {

			@Override
			public List<ContServiceDataElCons> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectConsByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay());
			}

		};

		ResponseEntity<?> resultResponse = getResponseServiceData(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataSelector);

		return resultResponse;

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElCons/{timeDetailType}/{contZPointId}/summary",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElConsSummary(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		RequestAnyDataSelector<ContServiceDataSummary<ContServiceDataElCons>> dataSelector = new RequestAnyDataSelector<ContServiceDataSummary<ContServiceDataElCons>>() {

			@Override
			public ContServiceDataSummary<ContServiceDataElCons> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectConsSummary(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay());
			}

		};

		ResponseEntity<?> resultResponse = getResponseServiceData(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataSelector);

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

		RequestPageDataSelector<ContServiceDataElProfile> dataSelector = new RequestPageDataSelector<ContServiceDataElProfile>() {

			@Override
			public Page<ContServiceDataElProfile> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				checkNotNull(pageRequest);
				return contServiceDataElService.selectProfileByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay(), pageRequest);
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceDataPaged(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataDateSort, pageable, dataSelector);

		return resultResponse;

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElProfile/{timeDetailType}/{contZPointId}",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElProfile(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		RequestListDataSelector<ContServiceDataElProfile> dataSelector = new RequestListDataSelector<ContServiceDataElProfile>() {

			@Override
			public List<ContServiceDataElProfile> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectProfileByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay());
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceData(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataSelector);

		return resultResponse;

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElProfile/{timeDetailType}/{contZPointId}/summary",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElProfileSummary(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		RequestAnyDataSelector<ContServiceDataSummary<ContServiceDataElProfile>> dataSelector = new RequestAnyDataSelector<ContServiceDataSummary<ContServiceDataElProfile>>() {

			@Override
			public ContServiceDataSummary<ContServiceDataElProfile> selectData(Long contZPointId,
					TimeDetailKey timeDetail, LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectProfileSummary(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay());
			}

		};

		ResponseEntity<?> resultResponse = getResponseServiceData(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataSelector);

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

		RequestPageDataSelector<ContServiceDataElTech> pageSelector = new RequestPageDataSelector<ContServiceDataElTech>() {

			@Override
			public Page<ContServiceDataElTech> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				checkNotNull(pageRequest);
				return contServiceDataElService.selectTechByContZPoint(contZPointId, timeDetail,
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
	 * @return
	 */
	@RequestMapping(value = "/{contObjectId}/serviceElTech/{timeDetailType}/{contZPointId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElTech(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		RequestListDataSelector<ContServiceDataElTech> dataSelector = new RequestListDataSelector<ContServiceDataElTech>() {

			@Override
			public List<ContServiceDataElTech> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectTechByContZPoint(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay());
			}
		};

		ResponseEntity<?> resultResponse = getResponseServiceData(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataSelector);

		return resultResponse;

	}

	@RequestMapping(value = "/{contObjectId}/serviceElTech/{timeDetailType}/{contZPointId}/summary",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDataElTechSummary(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("timeDetailType") String timeDetailType,
			@RequestParam("beginDate") String fromDateStr, @RequestParam("endDate") String toDateStr) {

		RequestAnyDataSelector<ContServiceDataSummary<ContServiceDataElTech>> dataSelector = new RequestAnyDataSelector<ContServiceDataSummary<ContServiceDataElTech>>() {

			@Override
			public ContServiceDataSummary<ContServiceDataElTech> selectData(Long contZPointId, TimeDetailKey timeDetail,
					LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
				return contServiceDataElService.selectTechSummary(contZPointId, timeDetail,
						localDatePeriod.buildEndOfDay());
			}

		};

		ResponseEntity<?> resultResponse = getResponseServiceData(contObjectId, contZPointId, timeDetailType,
				fromDateStr, toDateStr, dataSelector);

		return resultResponse;
	}
}
