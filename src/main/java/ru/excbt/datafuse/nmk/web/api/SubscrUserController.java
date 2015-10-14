package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping("/api/subscr")
public class SubscrUserController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrUserController.class);

	@Autowired
	protected SubscrUserService subscrUserService;

	/**
	 * 
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentSubscrUsers() {
		List<SubscrUser> subscrUsers = subscrUserService.findBySubscriberId(getSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(subscrUsers));
	}

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers/{subscrUserId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentSubscrUser(@PathVariable("subscrUserId") Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserService.findOne(subscrUserId);
		if (subscrUser == null || subscrUser.getSubscriberId() == null
				|| !subscrUser.getSubscriberId().equals(subscrUserId)) {
			return responseBadRequest();
		}

		List<SubscrUser> subscrUsers = subscrUserService.findBySubscriberId(getSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(subscrUsers));
	}

	/**
	 * 
	 * @param subscrUser
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers", method = RequestMethod.POST)
	public ResponseEntity<?> createCurrentSubscrUsers(@RequestBody SubscrUser subscrUser,
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
			HttpServletRequest request) {
		checkNotNull(subscrUser);

		subscrUser.setSubscriberId(getSubscriberId());
		subscrUser.getSubscrRoles().clear();

		ApiActionLocation action = new EntityApiActionLocationAdapter<SubscrUser, Long>(subscrUser, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrUser processAndReturnResult() {
				return subscrUserService.createOne(entity);
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param subscrUserId
	 * @param subscrUser
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers/{subscrUserId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateCurrentSubscrUsers(@PathVariable("subscrUserId") Long subscrUserId,
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
			@RequestBody SubscrUser subscrUser) {
		checkNotNull(subscrUserId);
		checkNotNull(subscrUser);
		checkNotNull(subscrUser.getSubscriberId());

		subscrUser.getSubscrRoles().clear();

		if (!subscrUser.getSubscriberId().equals(getSubscriberId())) {
			return responseBadRequest();
		}

		ApiAction action = new EntityApiActionAdapter<SubscrUser>(subscrUser) {

			@Override
			public SubscrUser processAndReturnResult() {
				return subscrUserService.updateOne(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param subscrUserId
	 * @param isPermanent
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers/{subscrUserId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCurrentSubscrUsers(@PathVariable("subscrUserId") Long subscrUserId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {
		checkNotNull(subscrUserId);

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				if (Boolean.TRUE.equals(isPermanent)) {
					subscrUserService.deleteOnePermanent(subscrUserId);
				} else {
					subscrUserService.deleteOne(subscrUserId);
				}

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

}
