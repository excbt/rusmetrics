package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.service.SubscrCabinetService;
import ru.excbt.datafuse.nmk.data.service.SubscrCabinetService.ContObjectCabinetInfo;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

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
public class SubscrCabinetController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetController.class);

	@Autowired
	private SubscrCabinetService subscrCabinetService;

	/**
	 * 
	 * @param xId
	 * @return
	 */
	@RequestMapping(value = "/contObjectCabinetInfo", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectCabinetInfo() {
		List<ContObjectCabinetInfo> resultList = subscrCabinetService
				.selectSubscrContObjectCabinetInfoList(getSubscriberId());
		checkNotNull(resultList);
		return responseOK(resultList);
	}

	/**
	 * 
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putCreateContObjectCabinetInfo(@RequestBody final List<Long> cabinetContObjectList) {

		ApiAction action = new ApiActionEntityAdapter<List<ContObjectCabinetInfo>>() {

			@Override
			public List<ContObjectCabinetInfo> processAndReturnResult() {

				for (Long contObjectId : cabinetContObjectList) {
					try {
						//SubscrCabinetInfo cabinetInfo = 
						subscrCabinetService.createSubscrUserCabinet(getCurrentSubscriber(),
								new Long[] { contObjectId });
					} catch (PersistenceException e) {
					}
				}
				return subscrCabinetService.selectSubscrContObjectCabinetInfoList(getSubscriberId());
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
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
		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
