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
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.support.DeviceMetadataInfo;
import ru.excbt.datafuse.nmk.data.model.support.EntityColumn;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;

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

		ApiAction action = new ApiActionEntityAdapter<ContZPoint>(contZPoint) {
			@Override
			public ContZPoint processAndReturnResult() {
				return contZPointService.updateOne(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
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

		ApiActionLocation action = new ApiActionEntityLocationAdapter<ContZPoint, Long>(contZPoint, request) {

			@Override
			public ContZPoint processAndReturnResult() {
				return contZPointService.createOne(contObjectId, entity);
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

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				contZPointService.deleteOne(contZPointId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects/rsoOrganizations", method = RequestMethod.GET)
	public ResponseEntity<?> getRsoOrganizations(
			@RequestParam(value = "organizationId", required = false) Long organizationId) {
		List<Organization> rsOrganizations = organizationService.selectRsoOrganizations(getSubscriberParam());
		List<Organization> resultList = currentSubscriberService.isSystemUser() ? rsOrganizations
				: ObjectFilters.devModeFilter(rsOrganizations);

		organizationService.checkAndEnhanceOrganizations(resultList, organizationId);

		return responseOK(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			responseForbidden();
		}

		List<ContZPointMetadata> result = contZPointMetadataService.selectContZPointMetadata(contZPointId);

		if (result == null || result.isEmpty()) {
			result = contZPointMetadataService.selectNewMetadata(contZPointId, true);
		}

		return responseOK(result);

	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata/srcProp",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadataSrcProp(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			responseForbidden();
		}

		List<ContZPointMetadata> metadataList = contZPointMetadataService.selectNewMetadata(contZPointId, false);

		List<DeviceMetadataInfo> result = contZPointMetadataService.buildSrcPropsDeviceMapping(metadataList);

		return responseOK(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata/destProp",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadataDestProp(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<ContZPointMetadata> metadataList = contZPointMetadataService.selectNewMetadata(contZPointId, false);

		List<EntityColumn> result = contZPointMetadataService.buildDestProps(metadataList);

		return responseOK(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata/destDb",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadataDestDB(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<EntityColumn> result = contZPointMetadataService.selectContZPointDestDB(contZPointId);

		return responseOK(result);
	}

	/**
	 * 
	 * =======
	 * >>>>>>> sprint_s14
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putContZPointMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody List<ContZPointMetadata> requestEntity) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		if (!canAccessContZPoint(contZPointId)) {
			return responseForbidden();
		}

		ApiAction action = new ApiActionEntityAdapter<List<ContZPointMetadata>>(requestEntity) {

			@Override
			public List<ContZPointMetadata> processAndReturnResult() {
				return contZPointMetadataService.saveContZPointMetadata(requestEntity, contZPointId);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
