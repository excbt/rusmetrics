package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.model.TariffType;
import ru.excbt.datafuse.nmk.data.repository.TariffTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.TariffOptionRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.TariffPlanService;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
public class TariffPlanController  {

	private static final Logger logger = LoggerFactory.getLogger(TariffPlanController.class);

	private final TariffPlanService tariffPlanService;

	private final TariffOptionRepository tariffOptionRepository;

	private final TariffTypeRepository tariffTypeRepository;

	private final ContObjectService contObjectService;

	private final OrganizationService organizationService;

	private final PortalUserIdsService portalUserIdsService;

    public TariffPlanController(TariffPlanService tariffPlanService, TariffOptionRepository tariffOptionRepository, TariffTypeRepository tariffTypeRepository, ContObjectService contObjectService, OrganizationService organizationService, PortalUserIdsService portalUserIdsService) {
        this.tariffPlanService = tariffPlanService;
        this.tariffOptionRepository = tariffOptionRepository;
        this.tariffTypeRepository = tariffTypeRepository;
        this.contObjectService = contObjectService;
        this.organizationService = organizationService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/option", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> tariffOptionGet() {
		return ResponseEntity.ok(tariffOptionRepository.selectAll());
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/type", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> tariffTypeGet() {
		return ResponseEntity.ok(tariffTypeRepository.findAll());
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/rso", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> rsoGet() {
		// subscriberService.selectRsoOrganizations(currentSubscriberService.getSubscriberId())
		return ResponseEntity.ok(organizationService.selectRsoOrganizations(portalUserIdsService.getCurrentIds()));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/default", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDefaultAll() {
		List<?> resultList = tariffPlanService.selectTariffPlanList(portalUserIdsService.getCurrentIds().getSubscriberId());
		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @param rsoOrganizationId
	 * @return
	 */
	@RequestMapping(value = "/default/rso", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDefaultAll(@RequestParam("rsoOrganizationId") long rsoOrganizationId) {
		List<?> resultList = tariffPlanService.selectTariffPlanList(rsoOrganizationId);
		return ResponseEntity.ok(resultList);
	}

	/**
	 *
	 * @param tariffPlan
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
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

            Optional<Organization> organizationOptional = organizationService.findOneOrganization(rsoOrganizationId);
            //Organization rso = organizationService.selectOrganization(rsoOrganizationId);
            if (!organizationOptional.isPresent()) {
                return ApiResponse.responseBadRequest(ApiResult.badRequest("Invalid rsoOrganizationId"));
            }
			tariffPlan.setRso(organizationOptional.orElse(null));
		}

		if (tariffTypeId != null && tariffTypeId > 0) {
			TariffType tt = tariffTypeRepository.findOne(tariffTypeId);
			if (tt == null) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("Invalid tariffTypeId"));
			}

			tariffPlan.setTariffType(tt);
		}

		if (contObjectIds != null) {

			for (long contObjectId : contObjectIds) {
				Optional<ContObject> co = contObjectService.findContObjectOptional(contObjectId);
				if (!co.isPresent()) {
					return ApiResponse.responseBadRequest(ApiResult.validationError("Invalid ContObjectId"));
				}
				checkNotNull(tariffPlan.getContObjects());
				tariffPlan.getContObjects().add(co.get());
			}
		}

		ApiAction action = new AbstractEntityApiAction<TariffPlan>(tariffPlan) {

			@Override
			public void process() {
				//entity.setIsDefault(entity.is_default());
				setResultEntity(tariffPlanService.updateOne(portalUserIdsService.getCurrentIds(), entity));

			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

	/**
	 *
	 * @param tariffPlan
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
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
		    Optional<Organization> organizationOptional = organizationService.findOneOrganization(rsoOrganizationId);
			//Organization rso = organizationService.selectOrganization(rsoOrganizationId);
			if (!organizationOptional.isPresent()) {
				return ApiResponse.responseBadRequest(ApiResult.badRequest("Invalid rsoOrganizationId"));
			}
			tariffPlan.setRso(organizationOptional.get());
		}

		if (tariffTypeId != null && tariffTypeId > 0) {
			TariffType tt = tariffTypeRepository.findOne(tariffTypeId);
			if (tt == null) {
				return ApiResponse.responseBadRequest(ApiResult.badRequest("Invalid tariffTypeId"));
			}
			tariffPlan.setTariffType(tt);
		}

		if (contObjectIds != null) {

			for (long contObjectId : contObjectIds) {
				Optional<ContObject> co = contObjectService.findContObjectOptional(contObjectId);
				if (!co.isPresent()) {
					return ApiResponse.responseBadRequest(ApiResult.badRequest("Invalid ContObjectId"));
				}
				checkNotNull(tariffPlan.getContObjects());
				tariffPlan.getContObjects().add(co.get());
			}
		}

		tariffPlan.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());

		ApiActionLocation action = new ApiActionEntityLocationAdapter<TariffPlan, Long>(tariffPlan, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public TariffPlan processAndReturnResult() {

				//entity.setIsDefault(entity.is_default());

				return tariffPlanService.createOne(portalUserIdsService.getCurrentIds(), entity);
			}

		};

		return ApiActionTool.processResponceApiActionCreate(action);

	}

    /**
     *
     * @param tariffPlanId
     * @return
     */
	@RequestMapping(value = "/{tariffPlanId}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneTariff(@PathVariable("tariffPlanId") final long tariffPlanId) {

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				tariffPlanService.deleteOne(tariffPlanId);

			}
		};

		return ApiActionTool.processResponceApiActionDelete(action);

	}

	/**
	 *
	 * @param tariffPlanId
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}/contObject", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTariffPlanContObjects(@PathVariable("tariffPlanId") long tariffPlanId) {

		List<ContObject> contObjectList = tariffPlanService.selectTariffPlanContObjects(tariffPlanId,
			portalUserIdsService.getCurrentIds().getSubscriberId());

		return ResponseEntity.ok(contObjectList);
	}

	/**
	 *
	 * @param tariffPlanId
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}/contObject/available", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTariffPlanAvailableContObjects(@PathVariable("tariffPlanId") long tariffPlanId) {

		List<ContObject> contObjectList = tariffPlanService.selectTariffPlanAvailableContObjects(tariffPlanId,
				portalUserIdsService.getCurrentIds().getSubscriberId());

		return ResponseEntity.ok(contObjectList);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/rsoOrganizations", method = RequestMethod.GET)
	public ResponseEntity<?> getRsoOrganizations() {
		List<OrganizationDTO> rsOrganizations = organizationService.selectRsoOrganizations(portalUserIdsService.getCurrentIds());
		List<OrganizationDTO> resultList = portalUserIdsService.isSystemUser() ? rsOrganizations
				: rsOrganizations;
		return ApiResponse.responseOK(resultList);
	}

}
