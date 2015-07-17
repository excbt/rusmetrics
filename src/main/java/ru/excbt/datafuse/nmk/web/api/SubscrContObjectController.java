package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

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

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.model.types.ContObjectCurrentSettingTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContObjectController extends WebApiController {

	// private final static int TEST_SUBSCRIBER_ID = 728;

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContObjectController.class);

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectsList() {
		logger.debug("Fire listAll");

		List<ContObject> resultList = subscriberService
				.selectSubscriberContObjects(currentSubscriberService
						.getSubscriberId());

		return ResponseEntity.ok().body(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObject(
			@PathVariable("contObjectId") Long contObjectId,
			@RequestBody ContObject contObject) {

		checkNotNull(contObjectId);
		checkNotNull(contObject);

		if (contObject.isNew()) {
			return ResponseEntity.badRequest().build();
		}

		ApiAction action = new AbstractEntityApiAction<ContObject>(contObject) {
			@Override
			public void process() {
				setResultEntity(contObjectService.updateOneContObject(entity));

			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/settingModeType", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectSettingModeType() {

		List<ContObjectSettingModeType> resultList = contObjectService
				.selectContObjectSettingModeType();
		return ResponseEntity.ok().body(resultList);
	}

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	@RequestMapping(value = "/contObjects/settingModeType", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObjectSettingModeType(
			@RequestParam(value = "contObjectIds", required = true) final Long[] contObjectIds,
			@RequestParam(value = "currentSettingMode", required = true) final String currentSettingMode) {

		checkArgument(contObjectIds.length > 0);
		checkNotNull(currentSettingMode);
		checkArgument(ContObjectCurrentSettingTypeKey
				.isValid(currentSettingMode));

		ApiAction action = new AbstractEntityApiAction<List<Long>>() {

			@Override
			public void process() {

				List<Long> result = contObjectService
						.updateContObjectCurrentSettingModeType(contObjectIds,
								currentSettingMode,
								currentSubscriberService.getSubscriberId());

				setResultEntity(result);
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
