package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.vo.ReportParamsetVO;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.ReportTypeService;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с набором параметров отчета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/reportParamset")
public class ReportParamsetController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetController.class);

	public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final ReportParamsetService reportParamsetService;

	private final ReportTemplateService reportTemplateService;

	private final ReportTypeService reportTypeService;

    private final ObjectAccessService objectAccessService;

    public ReportParamsetController(ReportParamsetService reportParamsetService, ReportTemplateService reportTemplateService, ReportTypeService reportTypeService, ObjectAccessService objectAccessService) {
        this.reportParamsetService = reportParamsetService;
        this.reportTemplateService = reportTemplateService;
        this.reportTypeService = reportTypeService;
        this.objectAccessService = objectAccessService;
    }

	/**
	 *
	 * @param reportUrlName
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyList(@PathVariable("reportUrlName") String reportUrlName) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return ApiResponse.responseOK(() -> reportParamsetService.selectReportTypeParamsetList(reportTypeKey,
				ReportConstants.IS_ACTIVE, currentSubscriberService.getSubscriberId()));

	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return ApiResponse.responseOK(() -> reportParamsetService.findReportParamset(reportParamsetId));
	}

	/**
	 *
	 * @param reportUrlName
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyArchList(@PathVariable("reportUrlName") String reportUrlName) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return ApiResponse.responseOK(() -> reportParamsetService.selectReportTypeParamsetList(reportTypeKey,
				ReportConstants.IS_NOT_ACTIVE, currentSubscriberService.getSubscriberId()));
	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}/{reportParamsetId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAnyOneArch(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return ApiResponse.responseOK(() -> reportParamsetService.findReportParamset(reportParamsetId));
	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportTemplateId
	 * @param contObjectIds
	 * @param reportParamset
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@RequestParam(value = "reportTemplateId", required = true) Long reportTemplateId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset, HttpServletRequest request) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		return createInternal(reportTemplateId, reportParamset, contObjectIds, request);
	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @param contObjectIds
	 * @param reportParamset
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestParam(value = "clearContObjectIds", required = false) Boolean clearContObjectIds,
			@RequestBody ReportParamset reportParamset) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		final Long[] fixContObjectIds = (contObjectIds == null && Boolean.TRUE.equals(clearContObjectIds))
				? new Long[] {} : contObjectIds;

		return updateInternal(reportParamsetId, reportParamset, fixContObjectIds);
	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/{reportUrlName}/{reportParamsetId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteAnyOne(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ApiActionVoidProcess actionProcess = () -> reportParamsetService.deleteReportParamset(reportParamsetId);

		return ApiResponse.responseDelete(actionProcess);
	}

	/**
	 *
	 * @param reportUrlName
	 * @param reportParamsetId
	 * @return
	 */
	@RequestMapping(value = "/archive/{reportUrlName}/{reportParamsetId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteAnyOneArch(@PathVariable("reportUrlName") String reportUrlName,
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		ReportTypeKey reportTypeKey = ReportTypeKey.findByUrlName(reportUrlName);
		if (reportTypeKey == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("Report of type %s is not supported", reportUrlName));
		}

		ApiActionVoidProcess actionProcess = () -> reportParamsetService.deleteReportParamset(reportParamsetId);

		return ApiResponse.responseDelete(actionProcess);
	}

    /**
     *
     * @param reportParamsetId
     * @param reportParamset
     * @param contObjectIds
     * @return
     */
	private ResponseEntity<?> updateInternal(final Long reportParamsetId, final ReportParamset reportParamset,
			final Long[] contObjectIds) {

		checkNotNull(reportParamsetId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.getId());
		checkArgument(reportParamset.getId().equals(reportParamsetId));

		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());

		ApiAction action = new AbstractEntityApiAction<ReportParamset>(reportParamset) {
			@Override
			public void process() {
				setResultEntity(reportParamsetService.updateOne(entity, contObjectIds));
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

    /**
     *
     * @param reportTemplateId
     * @param reportParamset
     * @param contObjectIds
     * @param request
     * @return
     */
	private ResponseEntity<?> createInternal(final Long reportTemplateId, final ReportParamset reportParamset,
			final Long[] contObjectIds, HttpServletRequest request) {

		checkNotNull(reportTemplateId);
		checkNotNull(reportParamset);
		checkNotNull(reportParamset.isNew());

		ReportTemplate reportTemplate = reportTemplateService.findOne(reportTemplateId);

		if (reportTemplate == null) {
			return ResponseEntity.badRequest().build();
		}

		reportParamset.setReportTemplate(reportTemplate);
		reportParamset.setSubscriber(currentSubscriberService.getSubscriber());
		reportParamset.set_active(true);

		try {
			String paramJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(reportParamset);
			logger.trace("ReportParamset JSON: {}", paramJson);

		} catch (JsonProcessingException e) {
		}

		ApiActionProcess<ReportParamset> actionProcess = () -> reportParamsetService
				.createReportParamset(reportParamset, contObjectIds);

		return ApiResponse.responseCreate(actionProcess, () -> request.getRequestURI());

	}

    /**
     *
     * @param srcId
     * @param contObjectIds
     * @param reportParamset
     * @param request
     * @return
     */
	@RequestMapping(value = "/createByTemplate/{srcId}", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createByTemplate(@PathVariable(value = "srcId") Long srcId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody ReportParamset reportParamset, HttpServletRequest request) {

		checkNotNull(srcId);
		checkNotNull(reportParamset);
		checkArgument(reportParamset.isNew());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<ReportParamset, Long>(reportParamset, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public ReportParamset processAndReturnResult() {
				return reportParamsetService.createByTemplate(srcId, reportParamset, contObjectIds,
						currentSubscriberService.getSubscriber());
			}

			@Override
			public URI getLocation() {
				checkNotNull(getResultEntity());

				String keyname = getResultEntity().getReportTemplate().getReportTypeKeyname();
				ReportTypeKey reportType = ReportTypeKey.valueOf(keyname);

				return URI.create(
						"/api/reportParamset" + ReportConstants.getReportTypeURL(reportType) + "/" + getLocationId());
			}

		};

		return ApiActionTool.processResponceApiActionCreate(action);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectUnits(@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportParamsetId);
		return ApiResponse.responseOK(() -> reportParamsetService.selectParamsetContObjects(reportParamsetId));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject/available", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableContObjectUnits(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId) {

		checkNotNull(reportParamsetId);

		return ApiResponse.responseOK(() -> reportParamsetService.selectParamsetAvailableContObjectUnits(reportParamsetId,
				currentSubscriberService.getSubscriberId()));
	}

	/**
	 *
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject", method = RequestMethod.POST,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> addReportParamsetContObject(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectId", required = true) Long contObjectId) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectId);

		if (!objectAccessService.checkContObjectId(getSubscriberId(),contObjectId)
		//    !subscrContObjectService.checkContObjectSubscription(currentSubscriberService.getSubscriberId(),
		//		contObjectId)
        ) {
			return ResponseEntity.badRequest().build();
		}

		ApiActionObjectProcess actionProcess = () -> reportParamsetService.addUnitToParamset(reportParamsetId,
				contObjectId);

		return ApiResponse.responseUpdate(actionProcess);

	}

	/**
	 *
	 * @param reportParamsetId
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/{reportParamsetId}/contObject/{contObjectId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteReportParamsetContObject(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@PathVariable(value = "contObjectId") Long contObjectId) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectId);

		ApiActionVoidProcess actionProcess = () -> reportParamsetService.deleteUnitFromParamset(reportParamsetId,
				contObjectId);

		return ApiResponse.responseDelete(actionProcess);

	}

    /**
     *
     * @param reportParamsetId
     * @return
     */
	@RequestMapping(value = "/archive/move", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> moveToArchive(
			@RequestParam(value = "reportParamsetId", required = true) final Long reportParamsetId) {

		checkNotNull(reportParamsetId);

		ApiAction action = new AbstractEntityApiAction<ReportParamset>() {
			@Override
			public void process() {
				setResultEntity(reportParamsetService.moveToArchive(reportParamsetId));
			}
		};

		ResponseEntity<?> responeResult = ApiActionTool.processResponceApiActionOk(action);

		if (action.getResult() == null) {
			responeResult = ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
					.body(ApiResult.build(ApiResultCode.ERR_UNCKNOWN));
		}

		return responeResult;
	}

    /**
     *
     * @param reportParamsetId
     * @param contObjectIds
     * @return
     */
	@RequestMapping(value = "/{reportParamsetId}/contObjects", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateReportParamsetContObjects(
			@PathVariable(value = "reportParamsetId") Long reportParamsetId,
			@RequestParam(value = "contObjectIds", required = true) Long[] contObjectIds) {

		checkNotNull(reportParamsetId);
		checkNotNull(contObjectIds);

		for (Long id : contObjectIds) {
			if (!objectAccessService.checkContObjectId(getSubscriberId(), id)
                //subscrContObjectService.checkContObjectSubscription(currentSubscriberService.getSubscriberId(), id)
                ) {
				return ResponseEntity.badRequest().build();
			}
		}

		ApiActionVoidProcess actionProcess = () -> reportParamsetService.updateUnitToParamset(reportParamsetId,
				contObjectIds);

		return ApiResponse.responseUpdate(actionProcess);

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/menu/contextLaunch", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamsetContextLaunch() {

		ApiActionObjectProcess actionProcess = () -> {

			List<ReportParamset> xList = reportParamsetService.selectReportParamsetContextLaunch(getSubscriberParam());

			List<ReportType> reportTypes = reportTypeService
					.findAllReportTypes(currentSubscriberService.isSystemUser());
			reportTypes = filterObjectAccess(reportTypes);

			final Set<String> reportTypeKeynames = reportTypes.stream()
					.filter(ObjectFilters.NO_DISABLED_OBJECT_PREDICATE).map(i -> i.getKeyname())
					.collect(Collectors.toSet());

			List<ReportParamsetVO> result = reportParamsetService.wrapReportParamsetVO(xList.stream()
					.filter(i -> reportTypeKeynames.contains(i.getReportTemplate().getReportTypeKeyname()))
					.collect(Collectors.toList()));

			result.sort(ReportParamsetVO.COMPARATOR);

			return ObjectFilters.deletedFilter(result);
		};

		return ApiResponse.responseOK(actionProcess);
	}

	/**
	 *
	 * @param paramDirectoryKeyname
	 * @return
	 */
	@RequestMapping(value = "/directoryParamItems/{paramDirectoryKeyname}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportParamDirectoryItems(
			@PathVariable("paramDirectoryKeyname") String paramDirectoryKeyname) {

		return ApiResponse.responseOK(() -> reportParamsetService.selectReportMetaParamItems(paramDirectoryKeyname), (l) -> {
			return l.isEmpty() ? ApiResponse.responseBadRequest() : null;
		});
	}

}
