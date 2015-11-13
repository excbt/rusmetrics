package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

		private PriceListSubscriber(Subscriber subscriber) {
			this.id = subscriber.getId();
			this.subscriberName = subscriber.getSubscriberName();
		}

		public Long getId() {
			return id;
		}

		public String getSubscriberName() {
			return subscriberName;
		}

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/priceList/subscribers", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getPriceListSubscribers() {
		List<PriceListSubscriber> resultList = new ArrayList<>();

		resultList.add(new PriceListSubscriber(currentSubscriberService.getSubscriber()));

		List<Subscriber> subscribers = rmaSubscriberService.selectRmaSubscribers(getCurrentSubscriberId());
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

		checkNotNull(subscriberId);

		List<SubscrPriceList> subscrPriceLists = new ArrayList<>();
		if (subscriberId.equals(getCurrentSubscriberId())) {
			subscrPriceLists = subscrPriceListService.findRmaPriceLists(subscriberId);
		} else {
			subscrPriceLists = subscrPriceListService.findSubscriberPriceLists(getCurrentSubscriberId(), subscriberId);
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
		checkNotNull(subscriberId);
		checkNotNull(priceList);
		checkArgument(!priceList.isNew());
		checkNotNull(priceList.getPriceListLevel());
		checkNotNull(priceList.getIsActive());
		checkNotNull(priceList.getIsDraft());

		if (priceList.getPriceListLevel() == 0) {
			return responseBadRequest(ApiResult.validationError("Invalid Price List Level"));
		}

		if (BooleanUtils.isTrue(priceList.getIsMaster())) {
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
				return subscrPriceListService.makeDraftRmaPriceList(srcPriceListId);
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
			@RequestParam("activeIds") Long[] activeIds) {

		checkNotNull(subscriberId);
		checkNotNull(priceListId);

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				subscrPriceListService.makeSubscrPriceLists(priceListId, Arrays.asList(subscriberIds),
						Arrays.asList(activeIds));
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
