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
import ru.excbt.datafuse.nmk.data.model.ReferencePeriod;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.ReferencePeriodService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Контроллер для работы с эталонным периодом
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.06.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class ReferencePeriodController {

	private static final Logger logger = LoggerFactory.getLogger(ReferencePeriodController.class);


	private final ReferencePeriodService referencePeriodService;

    private final ContZPointService contZPointService;

	private final ObjectAccessService objectAccessService;

	private final PortalUserIdsService portalUserIdsService;

    @Autowired
    public ReferencePeriodController(ReferencePeriodService referencePeriodService, ContZPointService contZPointService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService) {
        this.referencePeriodService = referencePeriodService;
        this.contZPointService = contZPointService;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
    }


    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getLastReferencePeriod(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId) {

		ApiActionObjectProcess actionProcess = () -> referencePeriodService
				.selectLastReferencePeriod(portalUserIdsService.getCurrentIds().getSubscriberId(), contZPointId);

		return ApiResponse.responseOK(actionProcess);
	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param referencePeriodId
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod/{id}",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOne(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @PathVariable("id") long referencePeriodId) {

		return ApiResponse.responseOK(() -> referencePeriodService.findOne(referencePeriodId));
	}

    /**
     *
     * @param referencePeriodId
     * @return
     */
	@RequestMapping(value = "/contObjects/zpoints/referencePeriod/{id}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneShort(@PathVariable("id") long referencePeriodId) {

		return ApiResponse.responseOK(() -> referencePeriodService.findOne(referencePeriodId));
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	private ResponseEntity<?> checkContObjectZPoint(long contObjectId, long contZPointId) {
		List<Long> contObjectsIds = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());

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
     * @param contObjectId
     * @param contZPointId
     * @param referencePeriod
     * @param request
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod",
			method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") final Long contZPointId, final @RequestBody ReferencePeriod referencePeriod,
			final HttpServletRequest request) {

		checkNotNull(referencePeriod);
		checkState(referencePeriod.isNew());

		ResponseEntity<?> checkResult = checkContObjectZPoint(contObjectId, contZPointId);
		if (checkResult != null) {
			return checkResult;
		}

		ApiActionProcess<ReferencePeriod> actionProcess = () -> {
			referencePeriod.setSubscriber(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
			referencePeriod.setContZPointId(contZPointId);

			return referencePeriodService.createOne(referencePeriod);
		};

		return ApiResponse.responseCreate(actionProcess, () -> request.getRequestURI());

	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param referencePeriodId
     * @param referencePeriod
     * @param request
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod/{referencePeriodId}",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
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

		ApiActionObjectProcess actionProcess = () -> {
			referencePeriod.setSubscriber(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
			referencePeriod.setContZPointId(contZPointId);

			return referencePeriodService.updateOne(referencePeriod);
		};

		return ApiResponse.responseUpdate(actionProcess);

	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param referencePeriodId
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/referencePeriod/{referencePeriodId}",
			method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
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

		ApiActionVoidProcess actionProcess = () -> referencePeriodService.deleteOne(referencePeriodId);

		return ApiResponse.responseDelete(actionProcess);
	}
}
