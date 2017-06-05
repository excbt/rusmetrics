package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrVCookie;
import ru.excbt.datafuse.nmk.data.service.SubscrVCookieService;
import ru.excbt.datafuse.nmk.data.service.WidgetMetaService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Controller
@RequestMapping(value = "/api/subscr/vcookie")
public class SubscrVCookieController extends AbstractSubscrApiResource {

	@Inject
	private SubscrVCookieService subscrVCookieService;

	@Inject
	private WidgetMetaService widgetMetaService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> readSubscrVCookie(@RequestParam(name = "vcMode", required = false) String vcMode,
			@RequestParam(name = "vcKey", required = false) String vcKey) {

		if (vcKey != null && vcMode == null) {
			return ApiResponse.responseBadRequest();
		}

		List<SubscrVCookie> vCookieList = subscrVCookieService.selectSubscrVCookie(getSubscriberParam());

		List<SubscrVCookie> resultList = vCookieList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(i -> vcMode == null || vcMode.equals(i.getVcMode()))
				.filter(i -> vcKey == null || vcKey.equals(i.getVcKey())).collect(Collectors.toList());

		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @param requestEntities
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrVCookie(@RequestBody List<SubscrVCookie> requestEntities) {

		checkNotNull(requestEntities);

		for (SubscrVCookie vc : requestEntities) {
			if (!vc.isNew()) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("id is null"));
			}

			if (vc.getVcKey() == null) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("vcKey is null"));
			}

			if (vc.getVcMode() == null) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("vcMode is null"));
			}

			vc.setSubscriberId(getSubscriberId());
			vc.setSubscrUserId(null);

		}

		ApiAction action = new ApiActionEntityAdapter<List<SubscrVCookie>>(requestEntities) {

			@Override
			public List<SubscrVCookie> processAndReturnResult() {

				return subscrVCookieService.saveSubscrVCookie(entity);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param requestEntities
	 * @param request
	 * @return
	 */
	//	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	//	public ResponseEntity<?> createSubscrVCookie(@RequestBody List<SubscrVCookie> requestEntities,
	//			HttpServletRequest request) {
	//
	//		checkNotNull(requestEntities);
	//
	//		for (SubscrVCookie vc : requestEntities) {
	//			if (!vc.isNew()) {
	//				return responseBadRequest(ApiResult.validationError("id is not null"));
	//			}
	//
	//			if (vc.getVcKey() == null) {
	//				return responseBadRequest(ApiResult.validationError("vcKey is null"));
	//			}
	//
	//			if (vc.getVcMode() == null) {
	//				return responseBadRequest(ApiResult.validationError("vcMode is null"));
	//			}
	//
	//			vc.setSubscriberId(getSubscriberId());
	//			vc.setSubscrUserId(null);
	//
	//		}
	//
	//		ApiActionLocation action = new ApiActionEntityLocationAdapter<List<SubscrVCookie>, Long>(requestEntities,
	//				request) {
	//
	//			@Override
	//			protected Long getLocationId() {
	//				return null;
	//			}
	//
	//			@Override
	//			public List<SubscrVCookie> processAndReturnResult() {
	//				return subscrVCookieService.saveVCookieSubscr(entity);
	//			}
	//		};
	//
	//		return WebApiHelper.processResponceApiActionCreate(action);
	//
	//	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> readSubscrVCookieUser(@RequestParam(name = "vcMode", required = false) String vcMode,
			@RequestParam(name = "vcKey", required = false) String vcKey) {

		if (vcKey != null && vcMode == null) {
			return ApiResponse.responseBadRequest();
		}

		List<SubscrVCookie> vCookieList = subscrVCookieService.selectSubscrVCookieByUser(getSubscriberParam());

		List<SubscrVCookie> resultList = vCookieList.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(i -> vcMode == null || vcMode.equals(i.getVcMode()))
				.filter(i -> vcKey == null || vcKey.equals(i.getVcKey())).collect(Collectors.toList());

		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @param requestEntities
	 * @return
	 */
	@RequestMapping(value = "/user/list", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrVCookieUser(@RequestBody List<SubscrVCookie> requestEntities) {

		checkNotNull(requestEntities);

		for (SubscrVCookie vc : requestEntities) {
			if (!vc.isNew()) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("id is null"));
			}

			if (vc.getVcKey() == null) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("vcKey is null"));
			}

			if (vc.getVcMode() == null) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("vcMode is null"));
			}

			SubscriberParam sParam = getSubscriberParam();

			vc.setSubscriberId(sParam.getSubscriberId());
			vc.setSubscrUserId(sParam.getSubscrUserId());

		}

		ApiAction action = new ApiActionEntityAdapter<List<SubscrVCookie>>(requestEntities) {

			@Override
			public List<SubscrVCookie> processAndReturnResult() {

				return subscrVCookieService.saveSubscrVCookieUser(entity);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/widgets/list", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> widgetList() {
		return ApiResponse.responseOK(() -> widgetMetaService.selectAllWidgets());
	}
}
