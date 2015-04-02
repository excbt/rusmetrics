package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ContZPointSettingModeService;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContZPointSettingModeController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContZPointSettingModeController.class);

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
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/settingMode", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
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

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param id
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/settingMode/{id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(
			@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId,
			@PathVariable("id") long id,
			@RequestBody ContZPointSettingMode entity) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkArgument(id > 0);
		checkNotNull(entity);
		checkNotNull(entity.getId());

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint.getContObject().getId().longValue() != contObjectId) {
			return ResponseEntity
					.badRequest()
					.body(String
							.format("ContZPoint (id=%d) is not own to ContZObject(id=%d)",
									contZPointId, contObjectId));
		}

		ContZPointSettingMode currentSetting = contZPointSettingModeService
				.findOne(id);

		if (currentSetting == null) {
			return ResponseEntity.badRequest().build();
		}

		if (currentSetting.getContZPoint().getId().longValue() != contZPoint
				.getId().longValue()) {
			return ResponseEntity.badRequest().build();
		}

		if (currentSetting.getId().longValue() != entity.getId().longValue()) {
			return ResponseEntity.badRequest().build();
		}

		entity.setContZPoint(contZPoint);
		prepareAuditableProps(currentSetting, entity);

		try {
			contZPointSettingModeService.save(entity);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error(
					"Error during save entity ContZPointSettingMode (id={}): {}",
					id, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().build();
	}

}
