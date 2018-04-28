package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ContZPointSettingModeService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с настройками точки учета для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 01.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContZPointSettingModeController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContZPointSettingModeController.class);

	private final ContZPointSettingModeService contZPointSettingModeService;

	private final ContZPointService contZPointService;

    public SubscrContZPointSettingModeController(ContZPointSettingModeService contZPointSettingModeService, ContZPointService contZPointService) {
        this.contZPointSettingModeService = contZPointSettingModeService;
        this.contZPointService = contZPointService;
    }

    /**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/settingMode",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		logger.debug("Fire All");

		// if (!contZPointService.checkContZPointOwnership(contZPointId,
		// contObjectId)) {
		// return ResponseEntity
		// .badRequest()
		// .body(String
		// .format("ContZPoint (id=%d) is not own to ContZObject(id=%d)",
		// contZPointId, contObjectId));
		// }

		logger.debug("Fire Result List");
		List<ContZPointSettingMode> resultList = contZPointSettingModeService.findSettingByContZPointId(contZPointId);
		return ResponseEntity.ok(resultList);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @param id
	 * @param settingMode
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/settingMode/{id}",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("id") long id,
			@RequestBody ContZPointSettingMode settingMode) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkArgument(id > 0);
		checkNotNull(settingMode);
		checkNotNull(settingMode.getId());

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint.getContObject().getId().longValue() != contObjectId) {
			return ResponseEntity.badRequest().body(
					String.format("ContZPoint (id=%d) is not own to ContZObject(id=%d)", contZPointId, contObjectId));
		}

		ContZPointSettingMode currentSetting = contZPointSettingModeService.findOne(id);

		if (currentSetting == null) {
			return ResponseEntity.badRequest().build();
		}

		if (currentSetting.getContZPoint().getId().longValue() != contZPoint.getId().longValue()) {
			return ResponseEntity.badRequest().build();
		}

		if (currentSetting.getId().longValue() != settingMode.getId().longValue()) {
			return ResponseEntity.badRequest().build();
		}

		settingMode.setContZPoint(contZPoint);

		//AuditableTools.copyAuditableProps(currentSetting, settingMode);


		ApiAction action = new AbstractEntityApiAction<ContZPointSettingMode>(settingMode) {

			@Override
			public void process() {
				setResultEntity(contZPointSettingModeService.save(entity));
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

}
