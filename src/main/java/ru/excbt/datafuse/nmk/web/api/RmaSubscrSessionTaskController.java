package ru.excbt.datafuse.nmk.web.api;

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
import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.SubscrSessionTask;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointSessionDetailType;
import ru.excbt.datafuse.nmk.data.model.support.SessionDetailTypeInfo;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.SessionDetailTypeService;
import ru.excbt.datafuse.nmk.data.service.SubscrSessionTaskService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/api/rma/subscrSessionTask")
public class RmaSubscrSessionTaskController extends AbstractSubscrApiResource {

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
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
	@RequestMapping(value = "/contZPointSessionDetailType/byDeviceObject/{deviceObjectId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointSessionDetailType(@PathVariable("deviceObjectId") Long deviceObjectId) {
		List<ContZPoint> contZPoints = contZPointService.selectContPointsByDeviceObject(deviceObjectId);

		Long[] contZPointIds = contZPoints.stream().map(i -> i.getId()).collect(Collectors.toList())
				.toArray(new Long[] {});

		if (contZPointIds == null || contZPointIds.length == 0) {
			return responseOK(new ArrayList<>());
		}

		//		if (!canAccessContZPoint(contZPointIds)) {
		//			return responseForbidden();
		//		}

		List<ContZPointSessionDetailType> resultList = contZPoints.stream().map(i -> {
			final List<SessionDetailTypeInfo> sessionDetailTypes = sessionDetailTypeService
					.selectByContServiceType(i.getContServiceTypeKeyname());
			return new ContZPointSessionDetailType(i, sessionDetailTypes);
		}).collect(Collectors.toList());

		return responseOK(resultList);
	}

	/**
	 *
	 * @param deviceObjectId
	 * @return
	 */
	@RequestMapping(value = "/sessionDetailTypes/byDeviceObject/{deviceObjectId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSessionDetailType(@PathVariable("deviceObjectId") Long deviceObjectId) {
		List<ContZPoint> contZPoints = contZPointService.selectContPointsByDeviceObject(deviceObjectId);

		Long[] contZPointIds = contZPoints.stream().map(i -> i.getId()).collect(Collectors.toList())
				.toArray(new Long[] {});

		if (contZPointIds == null || contZPointIds.length == 0) {
			return responseOK(new ArrayList<>());
		}

		//		if (!canAccessContZPoint(contZPointIds)) {
		//			return responseForbidden();
		//		}

		List<String> contServiceTypes = contZPoints.stream().map(i -> i.getContServiceTypeKeyname()).distinct()
				.collect(Collectors.toList());

		if (contServiceTypes.isEmpty()) {
			contServiceTypes.add("");
		}

		List<SessionDetailTypeInfo> resultList = sessionDetailTypeService
				.selectByContServiceType(contServiceTypes);

		return responseOK(resultList);
	}

	/**
	 *
	 * @param subscrSessionTaskId
	 * @return
	 */
	@RequestMapping(value = "/{subscrSessionTaskId}/logSessions", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrSessionTaskLogSession(
			@PathVariable("subscrSessionTaskId") Long subscrSessionTaskId) {
		List<LogSession> result = subscrSessionTaskService.selectTaskLogSessions(subscrSessionTaskId);
		return responseOK(ObjectFilters.deletedFilter(result));
	}

}
