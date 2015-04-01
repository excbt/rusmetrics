package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ContZPointSettingModeService;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContZPointSettingModeController {

	@Autowired
	private ContZPointSettingModeService contZPointSettingModeService;

	@Autowired
	private ContZPointService contZPointService;

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/settingMode", method = RequestMethod.GET, produces = WebApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId) {

		if (!contZPointService.checkContZPointOwnership(contZPointId,
				contObjectId)) {
			return ResponseEntity
					.badRequest()
					.body(String
							.format("ContZPoint (id=%d) is not own to ContZObject(id=%d)",
									contZPointId, contObjectId));
		}

		List<ContZPointSettingMode> resultList = contZPointSettingModeService
				.findSettingByContZPointId(contZPointId);
		return ResponseEntity.ok(resultList);
	}

}
