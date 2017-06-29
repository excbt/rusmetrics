package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;

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
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

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
public class RmaContObjectResource extends SubscrContObjectResource {

	private static final Logger log = LoggerFactory.getLogger(RmaContObjectResource.class);

	private final SubscriberAccessService subscriberAccessService;

	@Autowired
    public RmaContObjectResource(ContObjectService contObjectService, ContGroupService contGroupService, OrganizationService organizationService, ContObjectFiasService contObjectFiasService, SubscriberAccessService subscriberAccessService) {
        super(contObjectService, contGroupService, organizationService, contObjectFiasService);
        this.subscriberAccessService = subscriberAccessService;
    }

    /**
	 *
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContObject(
			@RequestParam(value = "cmOrganizationId", required = false) Long cmOrganizationId,
			final @RequestBody ContObject contObject, HttpServletRequest request) {

		checkNotNull(contObject);

		if (!contObject.isNew()) {
			return ResponseEntity.badRequest().build();
		}

		LocalDate rmaBeginDate = subscriberService.getSubscriberCurrentDateJoda(getCurrentSubscriberId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<ContObjectMonitorDTO, Long>(request) {

			@Override
			public ContObjectMonitorDTO processAndReturnResult() {
				ContObject result = contObjectService.automationCreate(contObject, getCurrentSubscriberId(),
                        LocalDateUtils.asLocalDate(rmaBeginDate.toDate()),
						cmOrganizationId);

				return contObjectService.wrapContObjectMonitorDTO(result,false);
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);

	}

    /**
     *
     * @param contObjectId
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObject(@PathVariable("contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		LocalDate subscrEndDate = subscriberService.getSubscriberCurrentDateJoda(getCurrentSubscriberId());


		ApiAction action = (ApiActionAdapter) () -> contObjectService.deleteContObject(contObjectId, LocalDateUtils.asLocalDate(subscrEndDate.toDate()));

		return ApiActionTool.processResponceApiActionDelete(action);
	}

    /**
     *
     * @param contObjectIds
     * @return
     */
	@RequestMapping(value = "/contObjects", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObjects(@RequestParam("contObjectIds") Long[] contObjectIds) {

		checkNotNull(contObjectIds);

		if (!canAccessContObject(contObjectIds)) {
			return ApiResponse.responseForbidden();
		}

		LocalDate subscrEndDate = subscriberService.getSubscriberCurrentDateJoda(getCurrentSubscriberId());

		ApiAction action = (ApiActionAdapter) () -> contObjectService.deleteManyContObjects(contObjectIds, LocalDateUtils.asLocalDate(subscrEndDate.toDate()));

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjects(@RequestParam(value = "contGroupId", required = false) Long contGroupId,
                                            @RequestParam(value = "meterPeriodSettingIds", required = false) List<Long> meterPeriodSettingIds) {

        ApiAction action = new ApiActionEntityAdapter<Object>() {
            @Override
            public Object processAndReturnResult() {
                List<ContObject> resultList = selectRmaContObjects(contGroupId, false, meterPeriodSettingIds);;

                return contObjectService.wrapContObjectsMonitorDTO(resultList,false);
            }
        };

	    return ApiActionTool.processResponceApiActionOk(action);

	}

	/**
	 * TODO updateContObjectAccess pt.3
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/subscrContObjects", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrContObjects(@PathVariable("subscriberId") Long subscriberId) {

        ApiAction action = new ApiActionEntityAdapter<Object>() {
            @Override
            public Object processAndReturnResult() {
                List<ContObject> resultList = subscrContObjectService.selectSubscriberContObjects(subscriberId);

                return contObjectService.wrapContObjectsMonitorDTO(resultList,false);
            }
        };

        return ApiActionTool.processResponceApiActionOk(action);
	}

	/**
	 * TODO updateContObjectAccess pt.2
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/availableContObjects", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableSubscrContObjects(@PathVariable("subscriberId") Long subscriberId) {


        ApiAction action = new ApiActionEntityAdapter<Object>() {
            @Override
            public Object processAndReturnResult() {
                List<ContObject> resultList = subscrContObjectService.selectAvailableContObjects(subscriberId,
                    getCurrentSubscriberId());

                return contObjectService.wrapContObjectsMonitorDTO(resultList,false);
            }
        };

        return ApiActionTool.processResponceApiActionOk(action);

	}

	/**
	 * TODO updateContObjectAccess pt.1
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/subscrContObjects", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrContObjects(@PathVariable("subscriberId") Long subscriberId,
			@RequestBody List<Long> contObjectIds) {

		checkNotNull(subscriberId);
		checkNotNull(contObjectIds);

		LocalDate subscrBeginDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);

		ApiAction action = new ApiActionEntityAdapter<List<ContObject>>() {

			@Override
			public List<ContObject> processAndReturnResult() {

//				List<ContObject> result =
                subscriberAccessService.updateContObjectAccess(new Subscriber().id(subscriberId), contObjectIds, LocalDateUtils.asLocalDateTime(subscrBeginDate.toDate()));
//                    subscrContObjectService.updateSubscrContObjects(subscriberId, contObjectIds,
//						subscrBeginDate);

                List<ContObject> result = subscrContObjectService.selectSubscriberContObjects(subscriberId);

				subscrContObjectService.rmaInitHaveSubscr(getSubscriberParam(), result);

				return result;
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/subscribers", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectSubscribers(@PathVariable("contObjectId") Long contObjectId) {
		List<Long> resultList = subscrContObjectService.selectContObjectSubscriberIdsByRma(getRmaSubscriberId(),
				contObjectId);
		return ApiResponse.responseOK(resultList);
	}


}
