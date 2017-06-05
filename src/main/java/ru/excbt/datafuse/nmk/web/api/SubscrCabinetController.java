package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectCabinetInfo;
import ru.excbt.datafuse.nmk.data.model.support.SubscrUserWrapper;
import ru.excbt.datafuse.nmk.data.service.SubscrCabinetService;
import ru.excbt.datafuse.nmk.data.service.support.PasswordUtils;
import ru.excbt.datafuse.nmk.ldap.service.SubscrLdapException;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.04.2016
 *
 */
@Controller
@RequestMapping(value = "/api/subscr/subscrCabinet")
public class SubscrCabinetController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetController.class);

	@Autowired
	private SubscrCabinetService subscrCabinetService;

    /**
     *
     * @return
     */
	@RequestMapping(value = "/contObjectCabinetInfo", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectCabinetInfo() {
		List<ContObjectCabinetInfo> resultList = subscrCabinetService
				.selectSubscrContObjectCabinetInfoList(getSubscriberId());
		checkNotNull(resultList);
		return ApiResponse.responseOK(resultList);
	}

    /**
     *
     * @param cabinetContObjectList
     * @return
     */
	@RequestMapping(value = "/create", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putCreateContObjectCabinetInfo(@RequestBody final List<Long> cabinetContObjectList) {

		final List<Exception> errExceptions = new ArrayList<>();

		ApiActionObjectProcess actionProcess = () -> {
			for (Long contObjectId : cabinetContObjectList) {
				try {
					//SubscrCabinetInfo cabinetInfo =
					subscrCabinetService.createSubscrUserCabinet(getCurrentSubscriber(), new Long[] { contObjectId });
				} catch (PersistenceException e) {
					errExceptions.add(e);
				} catch (SubscrLdapException e) {
					errExceptions.add(e);
				}
			}
			return subscrCabinetService.selectSubscrContObjectCabinetInfoList(getSubscriberId());
		};

		ResponseEntity<?> result = ApiResponse.responseUpdate(actionProcess, (x) -> {
			return errExceptions.isEmpty() ? null : ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(x);
		});

		return result;
	}

    /**
     *
     * @param childSubscriberList
     * @return
     */
	@RequestMapping(value = "/delete", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putDeleteContObjectCabinetInfo(@RequestBody final List<Long> childSubscriberList) {

		ApiAction action = new ApiActionEntityAdapter<List<ContObjectCabinetInfo>>() {

			@Override
			public List<ContObjectCabinetInfo> processAndReturnResult() {

				for (Long childSubscriberId : childSubscriberList) {
					try {
						//SubscrCabinetInfo cabinetInfo =
						subscrCabinetService.deleteSubscrUserCabinet(childSubscriberId);
					} catch (PersistenceException e) {
					}
				}
				return subscrCabinetService.selectSubscrContObjectCabinetInfoList(getSubscriberId());
			}
		};
		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param subscrUserId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/subscrUser/{subscrUserId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrUser(@PathVariable("subscrUserId") Long subscrUserId,
			@RequestBody SubscrUserWrapper requestEntity) {

		checkNotNull(subscrUserId);
		checkNotNull(requestEntity);
		checkNotNull(requestEntity.getSubscrUser());

		if (!subscrUserId.equals(requestEntity.getSubscrUser().getId())) {
			return ApiResponse.responseBadRequest();
		}

		ApiAction action = new ApiActionEntityAdapter<SubscrUserWrapper>(requestEntity) {

			@Override
			public SubscrUserWrapper processAndReturnResult() {
				return subscrCabinetService.saveCabinelSubscrUser(getSubscriberId(), entity);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param subscrUserId
	 * @return
	 */
	@RequestMapping(value = "/subscrUser/{subscrUserId}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrUser(@PathVariable("subscrUserId") Long subscrUserId) {
		SubscrUser subscrUser = subscrCabinetService.selectCabinelSubscrUser(subscrUserId);
		if (subscrUser == null) {
			return ApiResponse.responseBadRequest();
		}

		SubscrUserWrapper result = new SubscrUserWrapper(subscrUser);

		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param subscrUserIds
	 * @return
	 */
	@RequestMapping(value = "/subscrUser/resetPassword", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrUserResetPassword(@RequestBody final List<Long> subscrUserIds) {

		ApiAction action = new ApiActionEntityAdapter<List<ContObjectCabinetInfo>>() {

			@Override
			public List<ContObjectCabinetInfo> processAndReturnResult() {

				for (Long subscrUserId : subscrUserIds) {
					try {
						subscrCabinetService.saveCabinelSubscrUserPassword(getSubscriberId(), subscrUserId,
								PasswordUtils.generateRandomPassword());
					} catch (PersistenceException e) {
					}
				}

				return subscrCabinetService.selectSubscrContObjectCabinetInfoList(getSubscriberId());
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param subscrUserIds
	 * @return
	 */
	@RequestMapping(value = "/subscrUser/sendPassword", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrUserPasswordNotification(@RequestBody final List<Long> subscrUserIds) {

		ApiAction action = new ApiActionEntityAdapter<List<Long>>() {

			@Override
			public List<Long> processAndReturnResult() {

				List<Long> result = new ArrayList<>();

				for (Long subscrUserId : subscrUserIds) {
					if (subscrCabinetService.sendSubscrUserPasswordEmailNotification(getCurrentSubscUserId(),
							subscrUserId)) {
						result.add(subscrUserId);
					}
				}

				return result;// subscrCabinetService.selectSubscrContObjectCabinetInfoList(getSubscriberId());
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

}
