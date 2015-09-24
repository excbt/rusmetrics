package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePrice;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceItemService;
import ru.excbt.datafuse.nmk.data.service.SubscrServicePackService;
import ru.excbt.datafuse.nmk.data.service.SubscrServicePriceService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr/manage/service")
public class SubscServiceManageController extends SubscrApiController {

	@Autowired
	private SubscrServicePackService subscrServicePackService;

	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	@Autowired
	private SubscrServicePriceService subscrServicePriceService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/packs", method = RequestMethod.GET)
	public ResponseEntity<?> getServicePacks() {
		List<SubscrServicePack> result = subscrServicePackService.selectServicePackList();
		return responseOK(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public ResponseEntity<?> getServiceItems() {
		List<SubscrServiceItem> result = subscrServiceItemService.selectServiceItemList();
		return responseOK(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/prices", method = RequestMethod.GET)
	public ResponseEntity<?> getServicePrices() {
		List<SubscrServicePrice> result = subscrServicePriceService.selectPriceByDate(LocalDate.now());
		return responseOK(result);
	}

}
