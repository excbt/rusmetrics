package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.data.service.SubscrPrefService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrPrefController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPrefController.class);

	@Autowired
	private SubscrPrefService subscrPrefService;

	@Autowired
	private SubscrObjectTreeService subscrObjectTreeService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/subscrPrefValues", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrPrefValue() {

		List<SubscrPrefValue> resultList = subscrPrefService.selectSubscrPrefValue(getSubscriberId());

		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/subscrPrefValues/objectTreeTypes", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrPrefObjectTreeTypes(@RequestParam("subscrPref") String subscrPref) {

		List<String> treeTypes = subscrPrefService.selectSubscrPrefTreeTypes(subscrPref);

		if (treeTypes.isEmpty()) {
			return responseOK();
		}

		List<SubscrObjectTree> treeList = subscrObjectTreeService.selectSubscrObjectTreeShort(getSubscriberParam());

		List<SubscrObjectTree> resultList = treeList.stream().filter(i -> treeTypes.contains(i.getObjectTreeType()))
				.collect(Collectors.toList());

		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/subscrPrefValues", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrPrefValue(@RequestBody List<SubscrPrefValue> requestEntityList) {

		checkNotNull(requestEntityList);

		for (SubscrPrefValue v : requestEntityList) {
			if (v.getSubscriberId() == null || !v.getSubscriberId().equals(getSubscriberId())) {
				return responseBadRequest(ApiResult.validationError("Invalid subscriberId in request"));
			}
		}

		ApiAction action = new ApiActionEntityAdapter<List<SubscrPrefValue>>(requestEntityList) {

			@Override
			public List<SubscrPrefValue> processAndReturnResult() {

				return subscrPrefService.saveSubscrPrefValues(getSubscriberId(), requestEntityList);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
