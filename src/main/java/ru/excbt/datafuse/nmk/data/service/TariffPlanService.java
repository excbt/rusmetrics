package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.TariffPlanConstant;
import ru.excbt.datafuse.nmk.data.domain.AuditableTools;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.model.TariffType;
import ru.excbt.datafuse.nmk.data.model.keyname.TariffOption;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.repository.TariffPlanRepository;
import ru.excbt.datafuse.nmk.data.repository.TariffTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.TariffOptionRepository;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class TariffPlanService implements SecuredRoles {

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private TariffPlanRepository tariffPlanRepository;

	@Autowired
	private TariffTypeRepository tariffTypeRepository;

	@Autowired
	private TariffOptionRepository tariffOptionRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<TariffPlan> selectTariffPlanList() {
		return tariffPlanRepository
				.selectTariffPlanList(currentSubscriberService
						.getSubscriberId());
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<TariffPlan> selectTariffPlanList(long rsoOrganizationId) {
		return tariffPlanRepository.selectTariffPlanList(
				currentSubscriberService.getSubscriberId(), rsoOrganizationId);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<TariffPlan> getContObjectTariffPlanList(long rsoOrganizationId,
			long contObjectId) {
		return tariffPlanRepository.selectTariffPlanList(
				currentSubscriberService.getSubscriberId(), contObjectId);
	}

	/**
	 * 
	 * @param rsoOrganizationId
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void initDefaultTariffPlan(long rsoOrganizationId) {
		List<TariffPlan> currentTariffPlan = tariffPlanRepository
				.selectTariffPlanList(rsoOrganizationId);
		if (currentTariffPlan.size() > 0) {
			throw new PersistenceException(
					String.format(
							"Default Tariff Plan for rsoOrganizationId:{} already exists",
							rsoOrganizationId));
		}

		TariffOption toption = tariffOptionRepository
				.findOne(TariffPlanConstant.DEFAULT);
		checkNotNull(toption);

		Organization rso = organizationRepository.findOne(rsoOrganizationId);
		checkNotNull(rso);

		Iterable<TariffType> tarifTypes = tariffTypeRepository.findAll();
		for (TariffType ttype : tarifTypes) {
			TariffPlan tplan = new TariffPlan();
			tplan.setSubscriber(currentSubscriberService.getSubscriber());
			tplan.setRso(rso);
			tplan.setTariffOption(toption);
			tplan.setTariffType(ttype);
			tplan.setTariffPlanName(ttype.getTariffTypeName());
			tplan.setTariffPlanValue(BigDecimal.ZERO);
			tplan.setStartDate(DateTime.now().withDayOfMonth(1)
					.withMillisOfDay(0).toDate());
			tplan.setTariffPlanComment(String.format("AUTO GENERATED for %s",
					ttype.getContServiceType()));
			tariffPlanRepository.save(tplan);
		}
	}

	/**
	 * 
	 * @param rsoOrganizationId
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteDefaultTariffPlan(long rsoOrganizationId) {
		tariffPlanRepository.deleteTariffPlan(
				currentSubscriberService.getSubscriberId(), rsoOrganizationId);
	}

	/**
	 * 
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public TariffPlan updateOne(TariffPlan tariffPlan) {
		checkNotNull(tariffPlan);
		checkArgument(!tariffPlan.isNew());
		checkNotNull(tariffPlan.getTariffOptionKey(), "tariffOptionKey is NULL");

		if (!canModifyTariffPlanId(tariffPlan.getId())) {
			throw new PersistenceException(
					String.format(
							"TariffPlan(id=%d) can not be modified by currentSubscriberId",
							tariffPlan.getId()));
		}

		TariffPlan currentRec = tariffPlanRepository
				.findOne(tariffPlan.getId());

		AuditableTools.copyAuditableProps(currentRec, tariffPlan);

		tariffPlan.setSubscriber(currentRec.getSubscriber());

		return tariffPlanRepository.save(tariffPlan);
	}

	/**
	 * 
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public TariffPlan createOne(TariffPlan tariffPlan) {

		checkNotNull(tariffPlan);
		checkArgument(tariffPlan.isNew());
		checkNotNull(tariffPlan.getSubscriber(), "subscriber is NULL");
		checkNotNull(tariffPlan.getTariffOptionKey(), "tariffOptionKey is NULL");
		checkNotNull(tariffPlan.getTariffType(), "tariffType is NULL");
		checkNotNull(tariffPlan.getStartDate(), "startDate is NULL");
		checkNotNull(tariffPlan.getRso(), "rso is NULL");

		return tariffPlanRepository.save(tariffPlan);
	}

	/**
	 * 
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(TariffPlan tariffPlan) {
		checkNotNull(tariffPlan);
		checkArgument(!tariffPlan.isNew());

		if (!canModifyTariffPlanId(tariffPlan.getId())) {
			throw new PersistenceException(
					String.format(
							"TariffPlan(id=%d) can not be modified by currentSubscriberId",
							tariffPlan.getId()));
		}

		tariffPlanRepository.delete(tariffPlan);
	}

	/**
	 * 
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(long tariffPlanId) {
		TariffPlan tariffPlan = tariffPlanRepository.findOne(tariffPlanId);
		checkNotNull(tariffPlan);

		deleteOne(tariffPlan);
	}

	/**
	 * 
	 * @param tariffPlanId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean canModifyTariffPlanId(long tariffPlanId) {
		List<Long> ids = tariffPlanRepository.selectTariffPlanId(
				currentSubscriberService.getSubscriberId(), tariffPlanId);
		return ids.size() == 1;
	}

	@Transactional(readOnly = true)
	public List<ContObject> selectTariffPlanContObjects(long tariffPlanId,
			long subscriberId) {
		return tariffPlanRepository.selectTariffPlanContObjects(subscriberId,
				tariffPlanId);
	}

	@Transactional(readOnly = true)
	public List<ContObject> selectTariffPlanAvailableContObjects(
			long tariffPlanId, long subscriberId) {
		return tariffPlanRepository.selectAvailableContObjects(subscriberId,
				tariffPlanId);
	}

	@Transactional(readOnly = true)
	public TariffPlan findOne(long tariffPlanId) {
		return tariffPlanRepository.findOne(tariffPlanId);
	}

}
