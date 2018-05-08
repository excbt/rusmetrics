package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.SubscriberManageService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы абонентами для РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.10.2015
 *
 */
@Controller
@RequestMapping("/api/rma")
public class RmaSubscriberResource {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscriberResource.class);

	private final SubscriberService subscriberService;

	private final OrganizationService organizationService;

	private final SubscriberManageService subscriberManageService;

    private final PortalUserIdsService portalUserIdsService;

    private final SubscriberMapper subscriberMapper;

    public RmaSubscriberResource(SubscriberService subscriberService, SubscriberManageService subscriberManageService, OrganizationService organizationService, PortalUserIdsService portalUserIdsService, SubscriberMapper subscriberMapper) {
        this.subscriberService = subscriberService;
        this.organizationService = organizationService;
        this.subscriberManageService = subscriberManageService;
        this.portalUserIdsService = portalUserIdsService;
        this.subscriberMapper = subscriberMapper;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<?> getRmaSubscribers() {
		if (!portalUserIdsService.getCurrentIds().isRma()) {
			return ApiResponse.responseForbidden();
		}
		return ResponseEntity.ok(subscriberService.findByRmaSubscriber(portalUserIdsService.getCurrentIds()));
	}

	/**
	 *
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<?> getRmaSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId) {


        PortalUserIds portalUserIds = portalUserIdsService.getCurrentIds();

        //SubscrUserInfo userInfo = getSubscriberParam();

		if (!portalUserIds.isRma()) {
			logger.warn("Current User is not RMA");
			return ApiResponse.responseForbidden();
		}

		Optional<SubscriberDTO> subscriberDTOOptional = subscriberService.findSubscriberDTO(rSubscriberId);
		if (subscriberDTOOptional.isPresent()) {
            if (subscriberDTOOptional.get().getRmaSubscriberId() == null
                || !subscriberDTOOptional.get().getRmaSubscriberId().equals(portalUserIds.getSubscriberId())) {
                return ApiResponse.responseForbidden();
            }
        }

        return ApiResponse.responseContent(subscriberDTOOptional);

	}

	/**
	 *
	 * @param subscriberVM
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> createSubscriber(@RequestBody SubscriberVM subscriberVM, HttpServletRequest request) {

		checkNotNull(subscriberVM);
		checkNotNull(subscriberVM.getOrganizationId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscriberVM, Long>(subscriberVM, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscriberVM processAndReturnResult() {
				return subscriberManageService.createRmaSubscriber(entity, portalUserIdsService.getCurrentIds())
                    .map(subscriberMapper::toVM).orElse(null);
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param rSubscriberId
     * @param subscriberVM
     * @return
     */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> updateSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestBody SubscriberVM subscriberVM) {

		checkNotNull(rSubscriberId);
		checkNotNull(subscriberVM);
		checkNotNull(subscriberVM.getOrganizationId());

		ApiAction action = new ApiActionEntityAdapter<SubscriberVM>(subscriberVM) {

			@Override
			public SubscriberVM processAndReturnResult() {
				return subscriberManageService.updateRmaSubscriber(subscriberVM, portalUserIdsService.getCurrentIds())
                    .map(subscriberMapper::toVM)
                    .orElse(null);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> deleteSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		checkNotNull(rSubscriberId);

		ApiAction action = (ApiActionAdapter) () -> {
            if (Boolean.TRUE.equals(isPermanent)) {
                subscriberManageService.deleteRmaSubscriberPermanent(rSubscriberId, portalUserIdsService.getCurrentIds().getSubscriberId());
            } else {
                subscriberManageService.deleteRmaSubscriber(rSubscriberId, portalUserIdsService.getCurrentIds().getSubscriberId());
            }
        };
		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/subscribers/organizations", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getOrganizations() {
		List<OrganizationDTO> organizations = organizationService.findOrganizationsOfRma(portalUserIdsService.getCurrentIds());
		return ApiResponse.responseOK(organizations);
	}

}
