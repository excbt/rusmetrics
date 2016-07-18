package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.model.TariffType;
import ru.excbt.datafuse.nmk.data.repository.TariffTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.TariffOptionRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.TariffPlanService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с тарифными планами для абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.04.2015
 *
 */
@RestController
@RequestMapping(value = "/api/subscr/tariff")
public class TariffPlanController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(TariffPlanController.class);

	@Autowired
	private TariffPlanService tariffPlanService;

	@Autowired
	private TariffOptionRepository tariffOptionRepository;

	@Autowired
	private TariffTypeRepository tariffTypeRepository;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private OrganizationService organizationService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/option", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> tariffOptionGet() {
		return ResponseEntity.ok(tariffOptionRepository.selectAll());
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/type", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> tariffTypeGet() {
		return ResponseEntity.ok(tariffTypeRepository.findAll());
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/rso", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> rsoGet() {
		// subscriberService.selectRsoOrganizations(currentSubscriberService.getSubscriberId())
		return ResponseEntity.ok(organizationService.selectRsoOrganizations(getSubscriberParam()));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/default", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDefaultAll() {
		List<?> resultList = tariffPlanService.selectTariffPlanList(getCurrentSubscriberId());
		return responseOK(resultList);
	}

	/**
	 * 
	 * @param rsoOrganizationId
	 * @return
	 */
	@RequestMapping(value = "/default/rso", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDefaultAll(@RequestParam("rsoOrganizationId") long rsoOrganizationId) {
		List<?> resultList = tariffPlanService.selectTariffPlanList(rsoOrganizationId);
		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param tariffPlan
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneTariff(@PathVariable("tariffPlanId") long tariffPlanId,
			@RequestParam(value = "rsoOrganizationId", required = true) Long rsoOrganizationId,
			@RequestParam(value = "tariffTypeId", required = true) Long tariffTypeId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody TariffPlan tariffPlan) {

		if (tariffPlanId <= 0) {
			return ResponseEntity.badRequest().build();
		}

		if (tariffPlan == null) {
			return ResponseEntity.badRequest().build();
		}

		if (tariffPlan.isNew()) {
			return ResponseEntity.badRequest().build();
		}

		if (rsoOrganizationId != null && rsoOrganizationId > 0) {
			Organization rso = organizationService.selectOrganization(rsoOrganizationId);
			if (rso == null) {
				return responseBadRequest(ApiResult.validationError("Invalid rsoOrganizationId"));
			}
			tariffPlan.setRso(rso);
		}

		if (tariffTypeId != null && tariffTypeId > 0) {
			TariffType tt = tariffTypeRepository.findOne(tariffTypeId);
			if (tt == null) {
				return responseBadRequest(ApiResult.validationError("Invalid tariffTypeId"));
			}

			tariffPlan.setTariffType(tt);
		}

		if (contObjectIds != null) {

			for (long contObjectId : contObjectIds) {
				ContObject co = contObjectService.findContObject(contObjectId);
				if (co == null) {
					return responseBadRequest(ApiResult.validationError("Invalid ContObjectId"));
				}
				checkNotNull(tariffPlan.getContObjects());
				tariffPlan.getContObjects().add(co);
			}
		}

		ApiAction action = new AbstractEntityApiAction<TariffPlan>(tariffPlan) {

			@Override
			public void process() {
				//entity.setIsDefault(entity.is_default());
				setResultEntity(tariffPlanService.updateOne(getSubscriberParam(), entity));

			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param tariffPlan
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneTariff(
			@RequestParam(value = "rsoOrganizationId", required = true) Long rsoOrganizationId,
			@RequestParam(value = "tariffTypeId", required = true) Long tariffTypeId,
			@RequestParam(value = "contObjectIds", required = false) Long[] contObjectIds,
			@RequestBody TariffPlan tariffPlan, HttpServletRequest request) {

		checkNotNull(tariffPlan);
		checkArgument(tariffPlan.isNew());
		checkNotNull(rsoOrganizationId > 0);
		checkNotNull(tariffTypeId > 0);

		//		if (tariffPlan.getTariffOptionKey() == null) {
		//			return ResponseEntity.badRequest().body("Invalid TariffOptionKey");
		//		}

		if (rsoOrganizationId != null && rsoOrganizationId > 0) {
			Organization rso = organizationService.selectOrganization(rsoOrganizationId);
			if (rso == null) {
				return responseBadRequest(ApiResult.badRequest("Invalid rsoOrganizationId"));
			}
			tariffPlan.setRso(rso);
		}

		if (tariffTypeId != null && tariffTypeId > 0) {
			TariffType tt = tariffTypeRepository.findOne(tariffTypeId);
			if (tt == null) {
				return responseBadRequest(ApiResult.badRequest("Invalid tariffTypeId"));
			}
			tariffPlan.setTariffType(tt);
		}

		if (contObjectIds != null) {

			for (long contObjectId : contObjectIds) {
				ContObject co = contObjectService.findContObject(contObjectId);
				if (co == null) {
					return responseBadRequest(ApiResult.badRequest("Invalid ContObjectId"));
				}
				checkNotNull(tariffPlan.getContObjects());
				tariffPlan.getContObjects().add(co);
			}
		}

		tariffPlan.setSubscriberId(getSubscriberId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<TariffPlan, Long>(tariffPlan, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public TariffPlan processAndReturnResult() {

				//entity.setIsDefault(entity.is_default());

				return tariffPlanService.createOne(getSubscriberParam(), entity);
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

	/**
	 * 
	 * @param tariffPlan
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneTariff(@PathVariable("tariffPlanId") final long tariffPlanId) {

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				tariffPlanService.deleteOne(tariffPlanId);

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);

	}

	/**
	 * 
	 * @param tariffPlanId
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}/contObject", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTariffPlanContObjects(@PathVariable("tariffPlanId") long tariffPlanId) {

		List<ContObject> contObjectList = tariffPlanService.selectTariffPlanContObjects(tariffPlanId,
				currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(contObjectList);
	}

	/**
	 * 
	 * @param tariffPlanId
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}/contObject/available", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTariffPlanAvailableContObjects(@PathVariable("tariffPlanId") long tariffPlanId) {

		List<ContObject> contObjectList = tariffPlanService.selectTariffPlanAvailableContObjects(tariffPlanId,
				currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(contObjectList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/rsoOrganizations", method = RequestMethod.GET)
	public ResponseEntity<?> getRsoOrganizations() {
		List<Organization> rsOrganizations = organizationService.selectRsoOrganizations(getSubscriberParam());
		List<Organization> resultList = currentSubscriberService.isSystemUser() ? rsOrganizations
				: ObjectFilters.devModeFilter(rsOrganizations);
		return responseOK(resultList);
	}

}
