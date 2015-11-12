package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaPriceListController extends SubscrPriceListController {

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

		List<Subscriber> subscribers = subscriberService.selectRmaSubscribers(getCurrentSubscriberId());
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

		if (subscriberId.equals(getCurrentSubscriberId())) {

		}

		List<SubscrPriceList> subscrPriceLists = subscrPriceListService.findRmaPriceLists(subscriberId);

		return responseOK(subscrPriceLists);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param priceListId
	 * @param priceList
	 * @return
	 */
	@RequestMapping(value = "/{subscriberId}//priceList/{priceListId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updatePriceList(@PathVariable("subscriberId") Long subscriberId,
			@PathVariable("priceListId") Long priceListId, @RequestBody SubscrPriceList priceList) {

		checkNotNull(subscriberId);
		checkNotNull(subscriberId);
		checkNotNull(priceList);
		checkArgument(!priceList.isNew());
		checkNotNull(priceList.getPriceListLevel());
		checkNotNull(priceList.getIsMaster());
		checkNotNull(priceList.getIsActive());
		checkNotNull(priceList.getIsDraft());

		if (priceList.getPriceListLevel() == 0) {
			return responseBadRequest(ApiResult.validationError("Invalid Price List Level"));
		}

		if (priceList.getIsMaster()) {
			return responseBadRequest(ApiResult.validationError("Can't process master price list"));
		}

		if (priceList.getIsDraft() == false) {
			return responseBadRequest(ApiResult.validationError("Only draft price list accepted"));
		}

		ApiAction action = new EntityApiActionAdapter<SubscrPriceList>(priceList) {

			@Override
			public SubscrPriceList processAndReturnResult() {
				return null;
			}
		};

		return responseBadRequest();
	}

}
