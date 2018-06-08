package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeTemplateService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrObjectTreeTemplateController  {

	private final SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrObjectTreeTemplateController(SubscrObjectTreeTemplateService subscrObjectTreeTemplateService, PortalUserIdsService portalUserIdsService) {
        this.subscrObjectTreeTemplateService = subscrObjectTreeTemplateService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTreeTemplates", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeTemplate() {

		List<SubscrObjectTreeTemplate> resultList = subscrObjectTreeTemplateService
				.selectRmaSubscriberTemplates(portalUserIdsService.getCurrentIds());
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 *
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTreeTemplates/{templateId}/items", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeTemplateItem(@PathVariable("templateId") Long templateId) {
		List<SubscrObjectTreeTemplateItem> resultList = subscrObjectTreeTemplateService
				.selectSubscrObjectTreeTemplateItems(templateId);
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
