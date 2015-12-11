package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.excbt.datafuse.nmk.data.service.SubscrPriceItemService;
import ru.excbt.datafuse.nmk.data.service.SubscrPriceListService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrPriceListController extends SubscrApiController {

	@Autowired
	protected SubscrPriceListService subscrPriceListService;

	@Autowired
	protected SubscrPriceItemService subscrPriceItemService;

}
