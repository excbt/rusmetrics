package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

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
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

/**
 * Контроллер для работы с точками учета для РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
@Controller
@RequestMapping(value = "/api/rma")
public class RmaContZPointController extends SubscrContZPointController {

	private static final Logger logger = LoggerFactory.getLogger(RmaContZPointController.class);

    public RmaContZPointController(ContZPointService contZPointService, ContServiceDataHWaterService contServiceDataHWaterService, ContServiceDataElService contServiceDataElService, ContZPointMetadataService contZPointMetadataService, MeasureUnitService measureUnitService, OrganizationService organizationService, ObjectAccessService objectAccessService) {
        super(contZPointService, contServiceDataHWaterService, contServiceDataElService, contZPointMetadataService, measureUnitService, organizationService, objectAccessService);
    }


    /**
	 *
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody ContZPoint contZPoint) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);
		checkNotNull(contZPoint);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ApiActionObjectProcess actionProcess = () -> {
			return contZPointService.updateOne(contZPoint);
		};
		return ApiResponse.responseUpdate(actionProcess);

		//		ApiAction action = new ApiActionEntityAdapter<ContZPoint>(contZPoint) {
		//			@Override
		//			public ContZPoint processAndReturnResult() {
		//				return contZPointService.updateOne(entity);
		//			}
		//		};
		//
		//		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPoint
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.POST,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@RequestBody ContZPoint contZPoint, HttpServletRequest request) {

		checkNotNull(contObjectId);
		checkNotNull(contZPoint);

		ApiActionProcess<ContZPoint> actionProcess = () -> contZPointService.createOne(getSubscriberParam().toPortalUserIds(), contObjectId, contZPoint);

		return ApiResponse.responseCreate(actionProcess, () -> request.getRequestURI());

		//		ApiActionLocation action = new ApiActionEntityLocationAdapter<ContZPoint, Long>(contZPoint, request) {
		//
		//			@Override
		//			public ContZPoint processAndReturnResult() {
		//				return contZPointService.createOne(contObjectId, entity);
		//			}
		//
		//			@Override
		//			protected Long getLocationId() {
		//				return getResultEntity().getId();
		//			}
		//
		//		};
		//
		//		return WebApiHelper.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		ApiActionVoidProcess actionProcess = () -> {
			contZPointService.deleteOne(getSubscriberParam().toPortalUserIds(), contZPointId);
		};
		return ApiResponse.responseDelete(actionProcess);

		//		ApiAction action = new ApiActionAdapter() {
		//
		//			@Override
		//			public void process() {
		//				contZPointService.deleteOne(contZPointId);
		//			}
		//		};

		//		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 *
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putContZPointMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody List<ContZPointMetadata> requestEntity) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return ApiResponse.responseForbidden();
		}

		if (!canAccessContZPoint(contZPointId)) {
			return ApiResponse.responseForbidden();
		}

		ApiActionObjectProcess actionProcess = () -> {
			return contZPointMetadataService.saveContZPointMetadata(requestEntity, contZPointId);
		};
		return ApiResponse.responseUpdate(actionProcess);

		//		ApiAction action = new ApiActionEntityAdapter<List<ContZPointMetadata>>(requestEntity) {
		//
		//			@Override
		//			public List<ContZPointMetadata> processAndReturnResult() {
		//				return contZPointMetadataService.saveContZPointMetadata(requestEntity, contZPointId);
		//			}
		//		};
		//
		//		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
