package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemVO;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.data.service.SubscrPriceListService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaPriceListController extends SubscrPriceListController {

	private static final Logger logger = LoggerFactory.getLogger(RmaPriceListController.class);

	private final static Long MASTER_PRICE_LIST_SUBSCRIBER_ID = 0L;

	@Autowired
	private SubscrPriceListService subscrPriceListService;

	@Autowired
	private RmaSubscriberService rmaSubscriberService;

	/**
	 * 
	 * @author kovtonyk
	 *
	 */
	public class PriceListSubscriber {
		private final Long id;
		private final String subscriberName;
		private final boolean isRma;

		private PriceListSubscriber(Subscriber subscriber) {
			this.id = subscriber.getId();
			this.subscriberName = subscriber.getSubscriberName();
			this.isRma = Boolean.TRUE.equals(subscriber.getIsRma());
		}

		private PriceListSubscriber(Long id, String subscriberName) {
			this.id = id;
			this.subscriberName = subscriberName;
			this.isRma = true;
		}

		private PriceListSubscriber() {
			this.id = -1L;
			this.subscriberName = "---------";
			this.isRma = false;
		}

		public Long getId() {
			return id;
		}

		public String getSubscriberName() {
			return subscriberName;
		}

		public boolean isRma() {
			return isRma;
		}

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/priceList/subscribers", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getPriceListSubscribers() {
		List<PriceListSubscriber> resultList = new ArrayList<>();

		if (isSystemUser()) {
			resultList.add(new PriceListSubscriber(MASTER_PRICE_LIST_SUBSCRIBER_ID, "MASTER"));
			resultList.add(new PriceListSubscriber());
			List<Subscriber> rmaList = rmaSubscriberService.selectRmaList();
			rmaList.forEach(i -> {
				resultList.add(new PriceListSubscriber(i));
			});
			resultList.add(new PriceListSubscriber());
		} else {
			resultList.add(new PriceListSubscriber(currentSubscriberService.getSubscriber()));
		}

		List<Subscriber> subscribers = rmaSubscriberService.selectRmaSubscribers(getCurrentSubscriberId());
		subscribers.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).forEach(i -> {
			resultList.add(new PriceListSubscriber(i));
		});
		return responseOK(resultList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/priceList/rma", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getPriceListRma() {
		List<PriceListSubscriber> resultList = new ArrayList<>();

		if (!isSystemUser()) {
			return responseForbidden();
		}

		List<Subscriber> subscribers = rmaSubscriberService.selectRmaList();
		subscribers.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).forEach(i -> {
			resultList.add(new PriceListSubscriber(i));
		});
		return responseOK(resultList);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getPriceList(@PathVariable("subscriberId") Long subscriberId) {

		List<SubscrPriceList> subscrPriceLists = new ArrayList<>();

		if (subscriberId == null || subscriberId.intValue() == -1) {
			return responseOK(subscrPriceLists);
		}

		// For System user
		if (isSystemUser()) {
			if (subscriberId.intValue() == 0) {
				subscrPriceLists = subscrPriceListService.selectRootPriceLists();
			} else {
				Subscriber checkSubscriber = subscriberService.findOne(subscriberId);
				if (checkSubscriber == null) {
					return responseBadRequest();
				}
				if (Boolean.TRUE.equals(checkSubscriber.getIsRma())) {
					subscrPriceLists = subscrPriceListService.selectRmaPriceLists(subscriberId, null);
				} else {
					subscrPriceLists = subscrPriceListService.selectSubscriberPriceLists(getCurrentSubscriberId(),
							subscriberId);
				}
			}
		} else {
			// Main Case
			if (subscriberId.equals(getCurrentSubscriberId())) {
				subscrPriceLists = subscrPriceListService.selectRmaPriceLists(subscriberId, null);
			} else {
				subscrPriceLists = subscrPriceListService.selectSubscriberPriceLists(getCurrentSubscriberId(),
						subscriberId);
			}
		}

		return responseOK(subscrPriceLists);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param priceListId
	 * @param priceList
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList/{priceListId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updatePriceList(@PathVariable("subscriberId") Long subscriberId,
			@PathVariable("priceListId") Long priceListId, @RequestBody SubscrPriceList priceList) {

		checkNotNull(subscriberId);
		checkNotNull(priceListId);
		checkNotNull(priceList);
		checkArgument(!priceList.isNew());
		checkNotNull(priceList.getPriceListLevel());
		checkNotNull(priceList.getIsActive());
		checkNotNull(priceList.getIsDraft());

		if (priceList.getPriceListLevel() == 0 && !isSystemUser()) {
			return responseBadRequest(ApiResult.validationError("Invalid Price List Level"));
		}

		if (BooleanUtils.isTrue(priceList.getIsMaster()) && !isSystemUser()) {
			return responseBadRequest(ApiResult.validationError("Can't process master price list"));
		}

		if (priceList.getIsDraft() == false) {
			return responseBadRequest(ApiResult.validationError("Only draft price list accepted"));
		}

		ApiAction action = new EntityApiActionAdapter<SubscrPriceList>(priceList) {

			@Override
			public SubscrPriceList processAndReturnResult() {
				return subscrPriceListService.updateOne(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param priceListId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList/{priceListId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deletePriceList(@PathVariable("subscriberId") Long subscriberId,
			@PathVariable("priceListId") Long priceListId) {
		checkNotNull(subscriberId);
		checkNotNull(priceListId);

		SubscrPriceList subscrPriceList = subscrPriceListService.findOne(priceListId);

		if (subscrPriceList == null) {
			return responseBadRequest();
		}

		logger.info("version:{}", subscrPriceList.getVersion());

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				if (isSystemUser() && subscrPriceList.getPriceListLevel() != null
						&& subscrPriceList.getPriceListLevel().intValue() == SubscrPriceListService.PRICE_LEVEL_NMC) {
					subscrPriceListService.softDeleteRootPriceList(subscrPriceList);
				}

				if (subscrPriceList.getPriceListLevel() != null
						&& subscrPriceList.getPriceListLevel().intValue() == SubscrPriceListService.PRICE_LEVEL_RMA) {
					subscrPriceListService.softDeleteRmaPriceList(subscrPriceList);
				}

				if (subscrPriceList.getPriceListLevel() != null && subscrPriceList.getPriceListLevel()
						.intValue() == SubscrPriceListService.PRICE_LEVEL_SUBSCRIBER) {
					subscrPriceListService.softDeleteSubscrPriceList(subscrPriceList);
				}

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param srcPriceListId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createDraftPriceList(@PathVariable("subscriberId") Long subscriberId,
			@RequestParam(value = "srcPriceListId", required = true) Long srcPriceListId, HttpServletRequest reques) {

		checkNotNull(subscriberId);
		checkNotNull(srcPriceListId);

		ApiActionLocation action = new EntityApiActionLocationAdapter<SubscrPriceList, Long>(reques) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrPriceList processAndReturnResult() {
				return subscrPriceListService.createAnyDraftPriceList(srcPriceListId);
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param priceListId
	 * @param priceList
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList/{priceListId}/subscr", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createSubscrPriceList(@PathVariable("subscriberId") Long subscriberId,
			@PathVariable("priceListId") Long priceListId, @RequestParam("subscriberIds") Long[] subscriberIds,
			@RequestParam(value = "activeIds", required = false) Long[] activeIds) {

		checkNotNull(subscriberId);
		checkNotNull(priceListId);

		SubscrPriceList subscrPriceList = subscrPriceListService.findOne(priceListId);

		if (subscrPriceList == null) {
			return responseBadRequest();
		}

		if (subscriberIds.length == 0) {
			return responseBadRequest();
		}

		List<Long> subscriberIdList = Arrays.asList(subscriberIds);
		List<Long> activeIdList = activeIds != null ? Arrays.asList(activeIds) : null;

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {

				if (subscrPriceList.getPriceListLevel().intValue() == SubscrPriceListService.PRICE_LEVEL_RMA) {
					subscrPriceListService.createSubscrPriceLists(priceListId, subscriberIdList, activeIdList);
				} else if (subscrPriceList.getPriceListLevel().intValue() == SubscrPriceListService.PRICE_LEVEL_NMC) {
					subscrPriceListService.createRmaPriceLists(priceListId, subscriberIdList, activeIdList);
				}

			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param priceListId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList/{priceListId}/activate", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> activatePriceList(@PathVariable("subscriberId") Long subscriberId,
			@PathVariable("priceListId") Long priceListId) {

		checkNotNull(subscriberId);
		checkNotNull(priceListId);

		SubscrPriceList priceList = subscrPriceListService.findOne(priceListId);
		if (priceList == null) {
			return responseBadRequest(ApiResult.validationError("SubscrPriceList is not found", priceListId));
		}

		LocalDate startDate = currentSubscriberService.getSubscriberCurrentTime_Joda().toLocalDate();

		ApiAction action = new EntityApiActionAdapter<SubscrPriceList>() {

			@Override
			public SubscrPriceList processAndReturnResult() {

				if (isSystemUser() && priceList.getPriceListLevel() == SubscrPriceListService.PRICE_LEVEL_RMA) {
					return subscrPriceListService.activateRmaPriceList(priceListId, startDate);
				} else {
					return subscrPriceListService.activateSubscrPriceList(priceListId, startDate);
				}
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param subscrPriceListId
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList/{subscrPriceListId}/items", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getPriceListItems(@PathVariable("subscriberId") Long subscriberId,
			@PathVariable("subscrPriceListId") Long subscrPriceListId) {

		SubscrPriceList subscrPriceList = subscrPriceListService.findOne(subscrPriceListId);

		if (subscrPriceList == null) {
			return responseBadRequest();
		}

		List<SubscrPriceItem> priceItems = subscrPriceItemService.findPriceItems(subscrPriceListId);

		List<SubscrPriceItemVO> resultList = priceItems.stream().map(i -> new SubscrPriceItemVO(i))
				.collect(Collectors.toList());

		resultList.forEach(i -> {
			i.setCurrency(subscrPriceList.getPriceListCurrency());
		});

		return responseOK(resultList);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param subscrPriceListId
	 * @param subscrPriceItemVOs
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}/priceList/{subscrPriceListId}/items", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updatePriceItems(@PathVariable("subscriberId") Long subscriberId,
			@PathVariable("subscrPriceListId") Long subscrPriceListId,
			@RequestBody List<SubscrPriceItemVO> subscrPriceItemVOs) {

		checkNotNull(subscrPriceItemVOs);
		logger.info("Count of VOs: {}", subscrPriceItemVOs.size());

		SubscrPriceList subscrPriceList = subscrPriceListService.findOne(subscrPriceListId);
		if (subscrPriceList == null) {
			return responseBadRequest(
					ApiResult.validationError("SubscrPriceList (id=%d) is not found", subscrPriceListId));
		}

		LocalDate localDate = getCurrentSubscriberLocalDate();

		ApiAction action = new EntityApiActionAdapter<List<SubscrPriceItemVO>>(subscrPriceItemVOs) {

			@Override
			public List<SubscrPriceItemVO> processAndReturnResult() {
				// For MASTER price list & SystemUser
				//&& subscrPriceList.getPriceListLevel() == SubscrPriceListService.PRICE_LEVEL_NMC
				if (isSystemUser()) {
					List<SubscrPriceItem> resultPriceItems = subscrPriceItemService
							.updateRmaPriceItemValues(subscrPriceList, entity, localDate);
					List<SubscrPriceItemVO> result = subscrPriceItemService.convertSubscrPriceItemVOs(resultPriceItems);
					return result;
				}

				// Main case
				List<SubscrPriceItem> resultPriceItems = subscrPriceItemService
						.updateSubscrPriceItemValues(subscrPriceList, entity, localDate);
				List<SubscrPriceItemVO> result = subscrPriceItemService.convertSubscrPriceItemVOs(resultPriceItems);
				return result;
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
