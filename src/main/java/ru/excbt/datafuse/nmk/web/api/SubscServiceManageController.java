package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemVO;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с управляющими организациями для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
@RestController
@RequestMapping(value = "/api/subscr")
public class SubscServiceManageController  {

	private static final Logger logger = LoggerFactory.getLogger(SubscServiceManageController.class);

	private final SubscrServicePackService subscrServicePackService;

	private final SubscrServiceItemService subscrServiceItemService;

	private final SubscrPriceListService subscrPriceListService;

	private final SubscriberTimeService subscriberTimeService;

	private final SubscrServiceAccessService subscrServiceAccessService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscServiceManageController(SubscrServicePackService subscrServicePackService, SubscrServiceItemService subscrServiceItemService, SubscrPriceListService subscrPriceListService, SubscriberTimeService subscriberTimeService, SubscrServiceAccessService subscrServiceAccessService, PortalUserIdsService portalUserIdsService) {
        this.subscrServicePackService = subscrServicePackService;
        this.subscrServiceItemService = subscrServiceItemService;
        this.subscrPriceListService = subscrPriceListService;
        this.subscriberTimeService = subscriberTimeService;
        this.subscrServiceAccessService = subscrServiceAccessService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/manage/service/servicePackList", method = RequestMethod.GET)
	public ResponseEntity<?> getServicePacks() {
		List<SubscrServicePack> packList = subscrServicePackService.selectServicePackList(portalUserIdsService.getCurrentIds());
		List<SubscrServicePack> result = ObjectFilters.activeFilter(packList);
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/manage/service/serviceItemList", method = RequestMethod.GET)
	public ResponseEntity<?> getServiceItems() {
		List<SubscrServiceItem> itemList = subscrServiceItemService.selectServiceItemList();
		List<SubscrServiceItem> result = ObjectFilters.activeFilter(itemList);
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/manage/service/servicePriceList", method = RequestMethod.GET)
	public ResponseEntity<?> getServicePrices() {
		List<SubscrPriceItemVO> priceItems = new ArrayList<>();
		logger.info("currentSubscriber: {}", portalUserIdsService.getCurrentIds().getSubscriberId());
		if (portalUserIdsService.getCurrentIds().isRma()) {
			priceItems = subscrPriceListService.selectActiveRmaPriceListItemVOs(portalUserIdsService.getCurrentIds().getSubscriberId());
		} else {
			priceItems = subscrPriceListService.selectActiveSubscrPriceListItemVOs(portalUserIdsService.getCurrentIds().getSubscriberId());
		}
		return ApiResponse.responseOK(priceItems);
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/manage/service/access", method = RequestMethod.GET)
	public ResponseEntity<?> getSubscriberServiceAccess(@PathVariable("subscriberId") Long subscriberId) {
		return ApiResponse.responseOK(subscriberServiceAccessList(subscriberId));
	}

    /**
     *
     * @return
     */
	@RequestMapping(value = "/manage/service/access", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentServiceAccess() {
		return ApiResponse.responseOK(subscriberServiceAccessList(portalUserIdsService.getCurrentIds().getSubscriberId()));
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	private List<SubscrServiceAccess> subscriberServiceAccessList(Long subscriberId) {
		java.time.LocalDate accessDate = LocalDateUtils.asLocalDate(subscriberTimeService.getSubscriberCurrentTime(subscriberId));
		List<SubscrServiceAccess> result = subscrServiceAccessService.selectSubscriberServiceAccess(subscriberId,
				accessDate);

		result.forEach((i) -> {
			i.setId(null);
			i.setSubscriber(null);
			i.setSubscriberId(null);
			i.setAccessStartDate(null);
		});
		return result;

	}

	/**
	 *
	 * @param subscriberId
	 * @param subscriberAccessList
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/manage/service/access", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscriberServiceAccess(@PathVariable("subscriberId") Long subscriberId,
			@RequestBody final List<SubscrServiceAccess> subscriberAccessList) {

		ApiAction action = new AbstractEntityApiAction<List<SubscrServiceAccess>>() {

			@Override
			public void process() {
				setResultEntity(
                    subscrServiceAccessService.processAccessList(subscriberId, java.time.LocalDate.now(), subscriberAccessList));

			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param subscriberAccessList
	 * @return
	 */
	@RequestMapping(value = "/manage/service/access", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateCurrentServiceAccess(
			@RequestBody final List<SubscrServiceAccess> subscriberAccessList) {

		ApiAction action = new AbstractEntityApiAction<List<SubscrServiceAccess>>() {

			@Override
			public void process() {
				setResultEntity(subscrServiceAccessService.processAccessList(portalUserIdsService.getCurrentIds().getSubscriberId(), java.time.LocalDate.now(),
						subscriberAccessList));

			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/manage/service/permissions", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentServicePermissions() {
        java.time.LocalDate currentDate = LocalDateUtils.asLocalDate(subscriberTimeService.getSubscriberCurrentTime(portalUserIdsService.getCurrentIds().getSubscriberId()));

		List<SubscrServicePermission> permissions = subscrServiceAccessService
				.selectSubscriberPermissions(portalUserIdsService.getCurrentIds().getSubscriberId(), currentDate);
		List<SubscrServicePermission> result = permissions.stream().filter((i) -> Boolean.TRUE.equals(i.getIsFront()))
				.sorted(Comparator.comparing(AbstractKeynameEntity::getKeyname)).collect(Collectors.toList());
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/manage/service/permissions", method = RequestMethod.GET)
	public ResponseEntity<?> getSubscriberServicePermissions(@PathVariable("subscriberId") Long subscriberId) {
		List<SubscrServicePermission> permissions = subscrServiceAccessService.selectSubscriberPermissions(subscriberId,
				subscriberTimeService.getSubscriberCurrentTime(portalUserIdsService.getCurrentIds()));
		List<SubscrServicePermission> result = permissions.stream().filter((i) -> Boolean.TRUE.equals(i.getIsFront()))
				.sorted(Comparator.comparing(AbstractKeynameEntity::getKeyname)).collect(Collectors.toList());
		return ApiResponse.responseOK(result);
	}

}
