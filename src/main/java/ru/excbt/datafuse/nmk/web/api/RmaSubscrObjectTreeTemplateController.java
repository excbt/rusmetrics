package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeTemplateService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaSubscrObjectTreeTemplateController extends SubscrApiController {

	@Autowired
	private SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTreeTemplates", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeTemplate() {

		List<SubscrObjectTreeTemplate> resultList = subscrObjectTreeTemplateService
				.selectRmaSubscriberTemplates(getRmaSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTreeTemplates/{templateId}/items", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeTemplateItem(@PathVariable("templateId") Long templateId) {
		List<SubscrObjectTreeTemplateItem> resultList = subscrObjectTreeTemplateService
				.selectSubscrObjectTreeTemplateItems(templateId);
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
