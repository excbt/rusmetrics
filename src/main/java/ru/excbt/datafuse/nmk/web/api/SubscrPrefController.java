package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.data.service.SubscrPrefService;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@RestController
@RequestMapping(value = "/api/subscr")
public class SubscrPrefController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPrefController.class);

	private final SubscrPrefService subscrPrefService;

	private final SubscrObjectTreeService subscrObjectTreeService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrPrefController(SubscrPrefService subscrPrefService, SubscrObjectTreeService subscrObjectTreeService, PortalUserIdsService portalUserIdsService) {
        this.subscrPrefService = subscrPrefService;
        this.subscrObjectTreeService = subscrObjectTreeService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/subscrPrefValues", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrPrefValues() {

		List<SubscrPrefValue> resultList = subscrPrefService.selectSubscrPrefValue(portalUserIdsService.getCurrentIds());

		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 *
	 * @param subscrPrefKeyname
	 * @return
	 */
	@RequestMapping(value = "/subscrPrefValue", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrPrefValue(@RequestParam("subscrPrefKeyname") String subscrPrefKeyname) {

		List<SubscrPrefValue> resultList = subscrPrefService.selectSubscrPrefValue(portalUserIdsService.getCurrentIds());

		Optional<SubscrPrefValue> result = resultList.stream()
				.filter(i -> i.getSubscrPrefKeyname().equals(subscrPrefKeyname)).findFirst();

		if (result.isPresent()) {
			return ApiResponse.responseOK(ObjectFilters.deletedFilter(result.get()));
		}

		return ApiResponse.responseOK();
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/subscrPrefValues/objectTreeTypes", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrPrefObjectTreeTypes(@RequestParam("subscrPrefKeyname") String subscrPrefKeyname) {

		List<String> treeTypes = subscrPrefService.selectSubscrPrefTreeTypes(subscrPrefKeyname);

		if (treeTypes.isEmpty()) {
			return ApiResponse.responseOK();
		}

		List<SubscrObjectTree> treeList = subscrObjectTreeService.selectSubscrObjectTreeShort(portalUserIdsService.getCurrentIds());

		List<SubscrObjectTree> resultList = treeList.stream().filter(i -> treeTypes.contains(i.getObjectTreeType()))
				.collect(Collectors.toList());

		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

    /**
     *
     * @param requestEntityList
     * @return
     */
	@RequestMapping(value = "/subscrPrefValues", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrPrefValue(@RequestBody List<SubscrPrefValue> requestEntityList) {

		checkNotNull(requestEntityList);


		for (SubscrPrefValue v : requestEntityList) {
			if (v.getSubscriberId() == null || !v.getSubscriberId().equals(portalUserIdsService.getCurrentIds().getSubscriberId())) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("Invalid subscriberId in request"));
			}
		}

		ApiAction action = new ApiActionEntityAdapter<List<SubscrPrefValue>>(requestEntityList) {

			@Override
			public List<SubscrPrefValue> processAndReturnResult() {

				return subscrPrefService.saveSubscrPrefValues(portalUserIdsService.getCurrentIds(), requestEntityList);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

}
