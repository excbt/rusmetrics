package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrVCookie;
import ru.excbt.datafuse.nmk.data.service.SubscrVCookieService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr/vcookie")
public class SubscrVCookieController extends SubscrApiController {

	@Autowired
	private SubscrVCookieService subscrVCookieService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> readSubscrVCookie() {
		List<SubscrVCookie> resultList = subscrVCookieService.selectSubscrVCookie(getSubscriberParam());
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @param requestEntities
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrVCookie(@RequestBody List<SubscrVCookie> requestEntities) {

		checkNotNull(requestEntities);

		for (SubscrVCookie vc : requestEntities) {
			if (!vc.isNew()) {
				return responseBadRequest(ApiResult.validationError("id is null"));
			}

			if (vc.getVcKey() == null) {
				return responseBadRequest(ApiResult.validationError("vcKey is null"));
			}

			if (vc.getVcMode() == null) {
				return responseBadRequest(ApiResult.validationError("vcMode is null"));
			}

			vc.setSubscriberId(getSubscriberId());
			vc.setSubscrUserId(null);

		}

		ApiAction action = new ApiActionEntityAdapter<List<SubscrVCookie>>(requestEntities) {

			@Override
			public List<SubscrVCookie> processAndReturnResult() {

				return subscrVCookieService.saveVCookieSubscr(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
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
	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> readSubscrVCookieUser() {
		List<SubscrVCookie> resultList = subscrVCookieService.selectSubscrVCookieByUser(getSubscriberParam());
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

	/**
	 * 
	 * @param requestEntities
	 * @return
	 */
	@RequestMapping(value = "/user/list", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrVCookieUser(@RequestBody List<SubscrVCookie> requestEntities) {

		checkNotNull(requestEntities);

		for (SubscrVCookie vc : requestEntities) {
			if (!vc.isNew()) {
				return responseBadRequest(ApiResult.validationError("id is null"));
			}

			if (vc.getVcKey() == null) {
				return responseBadRequest(ApiResult.validationError("vcKey is null"));
			}

			if (vc.getVcMode() == null) {
				return responseBadRequest(ApiResult.validationError("vcMode is null"));
			}

			vc.setSubscriberId(getSubscriberId());
			vc.setSubscrUserId(null);

		}

		ApiAction action = new ApiActionEntityAdapter<List<SubscrVCookie>>(requestEntities) {

			@Override
			public List<SubscrVCookie> processAndReturnResult() {

				return subscrVCookieService.saveVCookieSubscr(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
