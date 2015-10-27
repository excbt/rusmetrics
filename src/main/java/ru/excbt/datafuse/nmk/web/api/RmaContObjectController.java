package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;

// TODO make RMA actions
@Controller
@RequestMapping(value = "/api/rma")
public class RmaContObjectController extends SubscrContObjectController {

	private static final Logger logger = LoggerFactory.getLogger(RmaContObjectController.class);

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContObject(
			@RequestParam(value = "cmOrganizationId", required = false) Long cmOrganizationId,
			@RequestBody ContObject contObject, HttpServletRequest request) {

		checkNotNull(contObject);

		if (!contObject.isNew()) {
			return ResponseEntity.badRequest().build();
		}

		LocalDate rmaBeginDate = subscriberService.getSubscriberCurrentDateJoda(getSubscriberId());

		ApiActionLocation action = new EntityApiActionLocationAdapter<ContObject, Long>(contObject, request) {

			@Override
			public ContObject processAndReturnResult() {
				return contObjectService.createOne(entity, getSubscriberId(), rmaBeginDate, cmOrganizationId);
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
	 * @param contObjectId
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObject(@PathVariable("contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		LocalDate subscrEndDate = subscriberService.getSubscriberCurrentDateJoda(getSubscriberId());

		ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				contObjectService.deleteOne(contObjectId, subscrEndDate);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjects() {
		List<ContObject> contObjectList = subscrContObjectService
				.selectRmaSubscriberContObjects(currentSubscriberService.getSubscriberId());

		List<ContObject> resultList = contObjectList.stream().filter(i -> !Boolean.FALSE.equals(i.get_haveSubscr()))
				.collect(Collectors.toList());

		return ResponseEntity.ok().body(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/manage", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getRmaContObjects() {
		List<ContObject> contObjectList = subscrContObjectService
				.selectRmaSubscriberContObjects(currentSubscriberService.getSubscriberId());

		List<ContObject> resultList = contObjectList;

		return ResponseEntity.ok().body(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/subscrContObjects", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrContObjects(@PathVariable("subscriberId") Long subscriberId) {

		checkNotNull(subscriberId);

		List<ContObject> resultList = subscrContObjectService.selectSubscriberContObjects(subscriberId);

		return ResponseEntity.ok().body(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/availableContObjects", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableSubscrContObjects(@PathVariable("subscriberId") Long subscriberId) {

		List<ContObject> resultList = subscrContObjectService.selectAvailableContObjects(subscriberId,
				getSubscriberId());

		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/subscrContObjects", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrContObjects(@PathVariable("subscriberId") Long subscriberId,
			@RequestBody List<Long> contObjectIds) {

		checkNotNull(subscriberId);
		checkNotNull(contObjectIds);

		LocalDate subscrBeginDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);

		ApiAction action = new EntityApiActionAdapter<List<ContObject>>() {

			@Override
			public List<ContObject> processAndReturnResult() {
				return subscrContObjectService.updateSubscrContObjects(subscriberId, contObjectIds, subscrBeginDate);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
