package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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

import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ReferencePeriodService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr")
public class ReferencePeriodController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(ReferencePeriodController.class);

	@Autowired
	private ReferencePeriodService referencePeriodService;

	// @Autowired
	// private CurrentSubscriberService currentSubscriberService;

	// @Autowired
	// private SubscriberService subscriberService;

	@Autowired
	private ContZPointService contZPointService;

	/**
	 * 
	 * @param contObjectId
	 * @param zpointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getLastReferencePeriod(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId) {

		List<ReferencePeriod> resultList = referencePeriodService
				.selectLastReferencePeriod(currentSubscriberService.getSubscriberId(), contZPointId);

		return ResponseEntity.ok().body(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param zpointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod/{id}",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOne(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("id") long referencePeriodId) {

		ReferencePeriod result = referencePeriodService.findOne(referencePeriodId);

		return ResponseEntity.ok().body(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param zpointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/zpoints/referencePeriod/{id}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneShort(@PathVariable("id") long referencePeriodId) {

		ReferencePeriod result = referencePeriodService.findOne(referencePeriodId);

		return ResponseEntity.ok().body(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	public ResponseEntity<?> checkContObjectZPoint(long contObjectId, long contZPointId) {
		List<Long> contObjectsIds = subscrContObjectService
				.selectSubscriberContObjectIds(currentSubscriberService.getSubscriberId());

		if (!contObjectsIds.contains(contObjectId)) {
			return ResponseEntity.badRequest().body(ApiResult.validationError("Bad contObjectId"));
		}

		List<Long> contZPointIds = contZPointService.selectContZPointIds(contObjectId);

		if (!contZPointIds.contains(contZPointId)) {
			return ResponseEntity.badRequest().body(ApiResult.validationError("Bad contZPointId"));
		}

		return null;
	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod",
			method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody ReferencePeriod referencePeriod,
			HttpServletRequest request) {

		checkNotNull(referencePeriod);
		checkState(referencePeriod.isNew());

		ResponseEntity<?> checkResult = checkContObjectZPoint(contObjectId, contZPointId);
		if (checkResult != null) {
			return checkResult;
		}

		referencePeriod.setSubscriber(currentSubscriberService.getSubscriber());
		referencePeriod.setContZPointId(contZPointId);

		ApiActionLocation action = new AbstractEntityApiActionLocation<ReferencePeriod, Long>(referencePeriod,
				request) {

			@Override
			public void process() {
				setResultEntity(referencePeriodService.createOne(entity));
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
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod/{referencePeriodId}",
			method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @PathVariable("referencePeriodId") Long referencePeriodId,
			@RequestBody ReferencePeriod referencePeriod, HttpServletRequest request) {

		checkNotNull(referencePeriod);
		checkState(!referencePeriod.isNew());
		checkState(referencePeriod.getId().equals(referencePeriodId));

		ResponseEntity<?> checkResult = checkContObjectZPoint(contObjectId, contZPointId);

		if (checkResult != null) {
			return checkResult;
		}

		referencePeriod.setSubscriber(currentSubscriberService.getSubscriber());
		referencePeriod.setContZPointId(contZPointId);

		ApiAction action = new AbstractEntityApiAction<ReferencePeriod>(referencePeriod) {

			@Override
			public void process() {
				setResultEntity(referencePeriodService.updateOne(entity));
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod/{referencePeriodId}",
			method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId,
			@PathVariable("referencePeriodId") final Long referencePeriodId) {
		checkNotNull(contObjectId);
		checkNotNull(contZPointId);
		checkNotNull(referencePeriodId);

		ResponseEntity<?> checkResult = checkContObjectZPoint(contObjectId, contZPointId);

		if (checkResult != null) {
			return checkResult;
		}

		final ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				referencePeriodService.deleteOne(referencePeriodId);
			}

		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}
}
