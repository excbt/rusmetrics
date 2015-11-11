package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;

@Controller
@RequestMapping(value = "/api/rma/priceList")
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
	@RequestMapping(value = "/subscribers", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
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
	@RequestMapping(value = "/{subscriberId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getPriceList(@PathVariable("subscriberId") Long subscriberId) {

		checkNotNull(subscriberId);

		List<SubscrPriceList> subscrPriceLists = subscrPriceListService.findRmaPriceLists(subscriberId);

		return responseOK(subscrPriceLists);
	}
}
