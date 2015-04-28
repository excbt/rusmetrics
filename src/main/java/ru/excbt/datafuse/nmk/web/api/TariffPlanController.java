package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.model.TariffType;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.repository.TariffTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.TariffOptionRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.TariffPlanService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@RestController
@RequestMapping(value = "/api/subscr/tariff")
public class TariffPlanController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(TariffPlanController.class);

	@Autowired
	private TariffPlanService tariffPlanService;

	@Autowired
	private TariffOptionRepository tariffOptionRepository;

	@Autowired
	private TariffTypeRepository tariffTypeRepository;

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ContObjectService contObjectService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/option", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> tariffOptionGet() {
		return ResponseEntity.ok(tariffOptionRepository.findAll());
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
		return ResponseEntity.ok(subscriberRepository
				.selectRsoOrganizations(currentSubscriberService
						.getSubscriberId()));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/default", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDefaultAll() {
		List<?> resultList = tariffPlanService.getDefaultTariffPlanList();
		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param rsoOrganizationId
	 * @return
	 */
	@RequestMapping(value = "/default/rso", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDefaultAll(
			@RequestParam("rsoOrganizationId") long rsoOrganizationId) {
		List<?> resultList = tariffPlanService
				.getDefaultTariffPlanList(rsoOrganizationId);
		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param tariffPlan
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneTariff(
			@PathVariable("tariffPlanId") long tariffPlanId,
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

		if (tariffPlan.getTariffOptionKey() == null) {
			return ResponseEntity.badRequest().body("Invalid TariffOptionKey");
		}

		if (rsoOrganizationId != null && rsoOrganizationId > 0) {
			Organization rso = organizationRepository
					.findOne(rsoOrganizationId);
			if (rso == null) {
				return ResponseEntity.badRequest().body(
						"Invalid rsoOrganizationId");
			}
			tariffPlan.setRso(rso);
		}

		if (tariffTypeId != null && tariffTypeId > 0) {
			TariffType tt = tariffTypeRepository.findOne(tariffTypeId);
			if (tt == null) {
				return ResponseEntity.badRequest().body("Invalid tariffTypeId");
			}

			tariffPlan.setTariffType(tt);
		}

		if (contObjectIds != null) {

			for (long contObjectId : contObjectIds) {
				ContObject co = contObjectService.findOne(contObjectId);
				if (co == null) {
					return ResponseEntity.badRequest().body(
							"Invalid ContObjectId");
				}
				checkNotNull(tariffPlan.getContObjects());
				tariffPlan.getContObjects().add(co);
			}
		}

		TariffPlan resultEntity = null;

		try {
			resultEntity = tariffPlanService.updateOne(tariffPlan);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during update entity TariffPlan (id={}): {}",
					tariffPlan.getId(), e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().body(resultEntity);
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

		if (tariffPlan.getTariffOptionKey() == null) {
			return ResponseEntity.badRequest().body("Invalid TariffOptionKey");
		}

		if (rsoOrganizationId != null && rsoOrganizationId > 0) {
			Organization rso = organizationRepository
					.findOne(rsoOrganizationId);
			if (rso == null) {
				return ResponseEntity.badRequest().body(
						"Invalid rsoOrganizationId");
			}
			tariffPlan.setRso(rso);
		}

		if (tariffTypeId != null && tariffTypeId > 0) {
			TariffType tt = tariffTypeRepository.findOne(tariffTypeId);
			if (tt == null) {
				return ResponseEntity.badRequest().body("Invalid tariffTypeId");
			}
			tariffPlan.setTariffType(tt);
		}

		if (contObjectIds != null) {

			for (long contObjectId : contObjectIds) {
				ContObject co = contObjectService.findOne(contObjectId);
				if (co == null) {
					return ResponseEntity.badRequest().body(
							"Invalid ContObjectId");
				}
				checkNotNull(tariffPlan.getContObjects());
				tariffPlan.getContObjects().add(co);
			}
		}

		tariffPlan.setSubscriber(currentSubscriberService.getSubscriber());

		TariffPlan resultEntity = null;

		try {
			resultEntity = tariffPlanService.createOne(tariffPlan);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during create entity TariffPlan (id={}): {}",
					tariffPlan.getId(), e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		URI location = URI.create(request.getRequestURI() + "/"
				+ resultEntity.getId());

		return ResponseEntity.created(location).build();
	}

	/**
	 * 
	 * @param tariffPlan
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneTariff(
			@PathVariable("tariffPlanId") long tariffPlanId) {
		try {
			tariffPlanService.deleteOne(tariffPlanId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during delete entity TariffPlan (id={}): {}",
					tariffPlanId, e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @param tariffPlanId
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}/contObject", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTariffPlanContObjects(
			@PathVariable("tariffPlanId") long tariffPlanId) {

		List<ContObject> contObjectList = tariffPlanService
				.selectTariffPlanContObjects(
						currentSubscriberService.getSubscriberId(),
						tariffPlanId);

		return ResponseEntity.ok(contObjectList);
	}

	/**
	 * 
	 * @param tariffPlanId
	 * @return
	 */
	@RequestMapping(value = "/{tariffPlanId}/contObject/available", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTariffPlanAvailableContObjects(
			@PathVariable("tariffPlanId") long tariffPlanId) {

		List<ContObject> contObjectList = tariffPlanService
				.selectTariffPlanAvailableContObjects(
						currentSubscriberService.getSubscriberId(),
						tariffPlanId);

		return ResponseEntity.ok(contObjectList);
	}

}
