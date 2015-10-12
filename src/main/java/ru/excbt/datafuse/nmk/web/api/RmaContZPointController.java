package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaContZPointController extends SubscrContZPointController {

	private static final Logger logger = LoggerFactory.getLogger(RmaContZPointController.class);

	/**
	 * 
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody ContZPoint contZPoint) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);
		checkNotNull(contZPoint);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		return responseBadRequest();
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPoint
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@RequestBody ContZPoint contZPoint, HttpServletRequest request) {

		checkNotNull(contObjectId);
		checkNotNull(contZPoint);

		ApiActionLocation action = new AbstractEntityApiActionLocation<ContZPoint, Long>(contZPoint, request) {

			@Override
			public void process() {
				ContZPoint resultEntity = contZPointService.createOne(contObjectId, entity);
				setResultEntity(resultEntity);
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param contZPoint
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				contZPointService.deleteOne(contZPointId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

}
