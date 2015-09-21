package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointStatInfo;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContZPointController extends WebApiController {

	@Autowired
	private SubscriberService subscrUserService;

	@Autowired
	private ContZPointService contZPointService;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectZPoints(
			@PathVariable("contObjectId") long contObjectId) {
		List<ContZPoint> zpList = contZPointService
				.findContObjectZPoints(contObjectId);
		return ResponseEntity.ok(zpList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPointsEx", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectZPointsEx(
			@PathVariable("contObjectId") long contObjectId) {
		List<ContZPointEx> zpList = contZPointService
				.findContObjectZPointsEx(contObjectId);
		return ResponseEntity.ok(zpList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPointsStatInfo", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectZPointStatInfo(
			@PathVariable("contObjectId") long contObjectId) {
		List<ContZPointStatInfo> resultList = contZPointService
				.selectContZPointStatInfo(contObjectId);
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
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateZPoint(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId,
			@RequestBody ContZPoint contZPoint) {

		ContZPoint currentContZPoint = contZPointService.findContZPoint(contZPointId);

		if (currentContZPoint == null
				|| currentContZPoint.getContObject().getId() != contObjectId) {
			return ResponseEntity.badRequest().build();
		}

		currentContZPoint.setCustomServiceName(contZPoint
				.getCustomServiceName());

		currentContZPoint.setIsManualLoading(contZPoint.getIsManualLoading());

		ApiAction action = new AbstractEntityApiAction<ContZPoint>(
				currentContZPoint) {
			@Override
			public void process() {
				setResultEntity(contZPointService.updateContZPoint(entity));
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectZPoint(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId) {
		ContZPoint currentContZPoint = contZPointService.findContZPoint(contZPointId);

		if (currentContZPoint.getContObject().getId() != contObjectId) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(currentContZPoint);
	}

}
