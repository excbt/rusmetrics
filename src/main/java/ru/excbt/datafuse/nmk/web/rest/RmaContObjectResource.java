package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;

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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
	private final ObjectAccessService objectAccessService;
    private final SubscriberService subscriberService;

	@Autowired
    public RmaContObjectResource(ContObjectService contObjectService, ContGroupService contGroupService, OrganizationService organizationService, ContObjectFiasService contObjectFiasService, MeterPeriodSettingService meterPeriodSettingService, PortalUserIdsService portalUserIdsService, SubscriberAccessService subscriberAccessService, ObjectAccessService objectAccessService, SubscriberService subscriberService) {
        super(contObjectService, contGroupService, organizationService, contObjectFiasService, meterPeriodSettingService, objectAccessService, portalUserIdsService);
        this.subscriberAccessService = subscriberAccessService;
        this.objectAccessService = objectAccessService;
        this.subscriberService = subscriberService;
    }

    /**
	 *
	 * @param contObjectDTO
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> createContObject(
			@RequestParam(value = "cmOrganizationId", required = false) Long cmOrganizationId,
			final @RequestBody ContObjectDTO contObjectDTO, HttpServletRequest request) {

        Objects.requireNonNull(contObjectDTO);

		if (contObjectDTO.getId() != null) {
			return ResponseEntity.badRequest().build();
		}

		LocalDate rmaBeginDate = subscriberService.getSubscriberCurrentDateJoda(portalUserIdsService.getCurrentIds().getSubscriberId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<ContObjectMonitorDTO, Long>(request) {

			@Override
			public ContObjectMonitorDTO processAndReturnResult() {
				ContObject result = contObjectService.automationCreate(contObjectDTO,
                        portalUserIdsService.getCurrentIds().getSubscriberId(),
                        LocalDateUtils.asLocalDate(rmaBeginDate.toDate()),
						cmOrganizationId);

				return contObjectService.wrapContObjectMonitorDTO(portalUserIdsService.getCurrentIds(),result,false);
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
    @Timed
    public ResponseEntity<?> deleteContObject(@PathVariable("contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		LocalDate subscrEndDate = subscriberService.getSubscriberCurrentDateJoda(portalUserIdsService.getCurrentIds().getSubscriberId());


		ApiAction action = (ApiActionAdapter) () -> contObjectService.deleteContObject(contObjectId, LocalDateUtils.asLocalDate(subscrEndDate.toDate()));

		return ApiActionTool.processResponceApiActionDelete(action);
	}

    /**
     *
     * @param contObjectIds
     * @return
     */
	@RequestMapping(value = "/contObjects", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> deleteContObjects(@RequestParam("contObjectIds") Long[] contObjectIds) {

		checkNotNull(contObjectIds);

		if (!canAccessContObject(Arrays.asList(contObjectIds))) {
			return ApiResponse.responseForbidden();
		}

		LocalDate subscrEndDate = subscriberService.getSubscriberCurrentDateJoda(portalUserIdsService.getCurrentIds().getSubscriberId());

		ApiAction action = (ApiActionAdapter) () -> contObjectService.deleteManyContObjects(contObjectIds, LocalDateUtils.asLocalDate(subscrEndDate.toDate()));

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getContObjects(@RequestParam(value = "contGroupId", required = false) Long contGroupId,
                                            @RequestParam(value = "meterPeriodSettingIds", required = false) List<Long> meterPeriodSettingIds) {

        ApiAction action = new ApiActionEntityAdapter() {
            @Override
            public List<ContObjectMonitorDTO> processAndReturnResult() {
                List<ContObject> resultList = findContObjectsByAccess(contGroupId, true, false,meterPeriodSettingIds);
                    //selectRmaContObjects(contGroupId, false, meterPeriodSettingIds);;
                List<ContObjectMonitorDTO> contObjectMonitorDTOS = contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(),resultList,false);
                return contObjectMonitorDTOS;
            }
        };

	    return ApiActionTool.processResponceApiActionOk(action);

	}

	/**
	 * TODO updateContObjectIdsAccess pt.3
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/subscrContObjects", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getSubscrContObjects(@PathVariable("subscriberId") Long subscriberId) {

        ApiAction action = new ContObjectMonitorDTOResponse() {
            @Override
            public List<ContObjectMonitorDTO> processAndReturnResult() {
                List<ContObject> resultList = objectAccessService.findContObjectsNoTtl(subscriberId);

                return contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(),resultList,false);
            }
        };

        return ApiActionTool.processResponceApiActionOk(action);
	}

	/**
	 * TODO updateContObjectIdsAccess pt.2
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/availableContObjects", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getAvailableSubscrContObjects(@PathVariable("subscriberId") Long subscriberId) {


        ApiAction action = new ContObjectMonitorDTOResponse() {
            @Override
            public List<ContObjectMonitorDTO> processAndReturnResult() {
                List<ContObject> resultList = objectAccessService.findRmaAvailableContObjects(
                    subscriberId,
                    portalUserIdsService.getCurrentIds().getSubscriberId());

                return contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(),resultList,false);
            }
        };

        return ApiActionTool.processResponceApiActionOk(action);

	}

	/**
	 * TODO updateContObjectIdsAccess pt.1
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/subscrContObjects", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> updateSubscrContObjects(@PathVariable("subscriberId") Long subscriberId,
			@RequestBody List<Long> contObjectIds) {

		checkNotNull(subscriberId);
		checkNotNull(contObjectIds);

		LocalDate subscrBeginDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);

		ApiAction action = new ContObjectDTOResponse() {

			@Override
			public List<ContObjectDTO> processAndReturnResult() {

                subscriberAccessService.updateContObjectIdsAccess(
                    contObjectIds,
                    LocalDateUtils.asLocalDateTime(subscrBeginDate.toDate()),
                    new Subscriber().id(subscriberId));
                List<ContObject> contObjects = objectAccessService.findContObjects(subscriberId);
                List<ContObjectDTO> result = contObjectService.mapToDTO(contObjects);
                objectAccessService.setupRmaHaveSubscrDTO(portalUserIdsService.getCurrentIds(), result);
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
    @Timed
    public ResponseEntity<?> getContObjectSubscribers(@PathVariable("contObjectId") Long contObjectId) {
		List<Long> resultList = objectAccessService.findSubscriberIdsByRma(portalUserIdsService.getCurrentIds().getRmaId(), contObjectId);
		return ApiResponse.responseOK(resultList);
	}


}
