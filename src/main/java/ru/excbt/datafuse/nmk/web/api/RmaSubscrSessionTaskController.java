package ru.excbt.datafuse.nmk.web.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrSessionTask;
import ru.excbt.datafuse.nmk.data.model.keyname.SessionDetailType;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointSessionDetailType;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.SessionDetailTypeService;
import ru.excbt.datafuse.nmk.data.service.SubscrSessionTaskService;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma/subscrSessionTask")
public class RmaSubscrSessionTaskController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrSessionTaskController.class);

	@Autowired
	private SubscrSessionTaskService subscrSessionTaskService;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private SessionDetailTypeService sessionDetailTypeService;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrSessionTask(@PathVariable("id") Long id) {
		SubscrSessionTask result = subscrSessionTaskService.findSubscrSessionTask(id);
		return responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * 
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> postSubscrSessionTask(
			@RequestBody SubscrSessionTask requestEntity, HttpServletRequest request) {

		subscrSessionTaskService.initTask(getSubscriberParam(), requestEntity);

		if (!subscrSessionTaskService.checkTaskValid(requestEntity)) {
			return responseBadRequest(ApiResult.validationError("SubscrSessionTask enity is not valid"));
		}

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrSessionTask, Long>(requestEntity, request) {

			@Override
			public SubscrSessionTask processAndReturnResult() {

				return subscrSessionTaskService.createSubscrSessionTask(entity);
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/contZPointSessionDetailType/byDeviceObject/{id}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointSessionDetailType(@PathVariable("id") Long deviceObjectId) {
		List<ContZPoint> contZPoints = contZPointService.selectContPointsByDeviceObject(deviceObjectId);

		Long[] contZPointIds = contZPoints.stream().map(i -> i.getId()).collect(Collectors.toList())
				.toArray(new Long[] {});

		if (!canAccessContZPoint(contZPointIds)) {
			return responseForbidden();
		}

		logger.info("Prepare ContZPointSessionDetailType");

		List<ContZPointSessionDetailType> resultList = contZPoints.stream().map(i -> {
			final List<SessionDetailType> sessionDetailTypes = sessionDetailTypeService
					.selectByContServiceType(i.getContServiceTypeKeyname());
			return new ContZPointSessionDetailType(i, sessionDetailTypes);
		}).collect(Collectors.toList());

		return responseOK(resultList);
	}

}
