package ru.excbt.datafuse.nmk.web.api;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectWrapper;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;

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

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

/**
 * Контроллер для работы с объектами учета для РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
@Controller
@RequestMapping(value = "/api/rma")
public class RmaSubscrContObjectController extends SubscrContObjectController {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrContObjectController.class);

	/**
	 *
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContObject(
			@RequestParam(value = "cmOrganizationId", required = false) Long cmOrganizationId,
			final @RequestBody ContObject contObject, HttpServletRequest request) {

		checkNotNull(contObject);

		if (!contObject.isNew()) {
			return ResponseEntity.badRequest().build();
		}

		LocalDate rmaBeginDate = subscriberService.getSubscriberCurrentDateJoda(getCurrentSubscriberId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<ContObjectWrapper, Long>(request) {

			@Override
			public ContObjectWrapper processAndReturnResult() {
				ContObject result = contObjectService.createContObject(contObject, getCurrentSubscriberId(),
						rmaBeginDate,
						cmOrganizationId);

				return contObjectService.wrapContObjectsStats(result);
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getContObject().getId();
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

    /**
     *
     * @param contObjectId
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObject(@PathVariable("contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		LocalDate subscrEndDate = subscriberService.getSubscriberCurrentDateJoda(getCurrentSubscriberId());

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				contObjectService.deleteContObject(contObjectId, subscrEndDate);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

    /**
     *
     * @param contObjectIds
     * @return
     */
	@RequestMapping(value = "/contObjects", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObjects(@RequestParam("contObjectIds") Long[] contObjectIds) {

		checkNotNull(contObjectIds);

		if (!canAccessContObject(contObjectIds)) {
			return responseForbidden();
		}

		LocalDate subscrEndDate = subscriberService.getSubscriberCurrentDateJoda(getCurrentSubscriberId());

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				contObjectService.deleteManyContObjects(contObjectIds, subscrEndDate);
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
	public ResponseEntity<?> getContObjects(@RequestParam(value = "contGroupId", required = false) Long contGroupId,
                                            @RequestParam(value = "meterPeriodSettingIds", required = false) List<Long> meterPeriodSettingIds) {
		List<ContObject> resultList = selectRmaContObjects(contGroupId, false, meterPeriodSettingIds);
		return responseOK(contObjectService.wrapContObjectsStats(resultList));
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

		return responseOK(contObjectService.wrapContObjectsStats(resultList));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/availableContObjects", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableSubscrContObjects(@PathVariable("subscriberId") Long subscriberId) {

		List<ContObject> resultList = subscrContObjectService.selectAvailableContObjects(subscriberId,
				getCurrentSubscriberId());

		return responseOK(contObjectService.wrapContObjectsStats(resultList));
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

		ApiAction action = new ApiActionEntityAdapter<List<ContObject>>() {

			@Override
			public List<ContObject> processAndReturnResult() {

				List<ContObject> result = subscrContObjectService.updateSubscrContObjects(subscriberId, contObjectIds,
						subscrBeginDate);

				subscrContObjectService.rmaInitHaveSubscr(getSubscriberParam(), result);

				return result;
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/subscribers", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectSubscribers(@PathVariable("contObjectId") Long contObjectId) {
		List<Long> resultList = subscrContObjectService.selectContObjectSubscriberIdsByRma(getRmaSubscriberId(),
				contObjectId);
		return responseOK(resultList);
	}


}
