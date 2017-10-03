package ru.excbt.datafuse.nmk.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.SubscriberController;
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
public class RmaSubscriberResource extends SubscriberController {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscriberResource.class);

	private final OrganizationService organizationService;

	private final RmaSubscriberService rmaSubscriberService;

    public RmaSubscriberResource(ObjectAccessService objectAccessService, OrganizationService organizationService, RmaSubscriberService rmaSubscriberService) {
        super(objectAccessService);
        this.organizationService = organizationService;
        this.rmaSubscriberService = rmaSubscriberService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.GET)
	public ResponseEntity<?> getRmaSubscribers() {
		if (!currentSubscriberService.isRma()) {
			return ApiResponse.responseForbidden();
		}

		return ApiResponse.responseOK(() -> rmaSubscriberService.selectRmaSubscribersDTO(getCurrentSubscriberId()));
	}

	/**
	 *
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.GET)
	public ResponseEntity<?> getRmaSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId) {


        PortalUserIds portalUserIds = getSubscriberParam().asPortalUserIds();

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
	 * @param rSubscriber
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscribers", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createSubscriber(@RequestBody Subscriber rSubscriber, HttpServletRequest request) {

		checkNotNull(rSubscriber);
		checkNotNull(rSubscriber.getOrganizationId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<Subscriber, Long>(rSubscriber, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public Subscriber processAndReturnResult() {
				return rmaSubscriberService.createRmaSubscriber(entity, getCurrentSubscriberId());
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param rSubscriberId
     * @param rSubscriber
     * @return
     */
	@RequestMapping(value = "/subscribers/{rSubscriberId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestBody Subscriber rSubscriber) {

		checkNotNull(rSubscriberId);
		checkNotNull(rSubscriber);
		checkNotNull(rSubscriber.getOrganizationId());

		ApiAction action = new ApiActionEntityAdapter<Subscriber>(rSubscriber) {

			@Override
			public Subscriber processAndReturnResult() {
				return rmaSubscriberService.updateRmaSubscriber(rSubscriber, getCurrentSubscriberId());
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
	public ResponseEntity<?> deleteSubscriber(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		checkNotNull(rSubscriberId);

		ApiAction action = (ApiActionAdapter) () -> {
            if (Boolean.TRUE.equals(isPermanent)) {
                rmaSubscriberService.deleteRmaSubscriberPermanent(rSubscriberId, getCurrentSubscriberId());
            } else {
                rmaSubscriberService.deleteRmaSubscriber(rSubscriberId, getCurrentSubscriberId());
            }
        };
		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/subscribers/organizations", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOrganizations() {
		List<OrganizationDTO> organizations = organizationService.findOrganizationsOfRma(getSubscriberParam().asPortalUserIds());
		return ApiResponse.responseOK(organizations);
	}

}
