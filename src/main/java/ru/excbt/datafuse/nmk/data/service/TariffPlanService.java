package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.constant.TariffPlanConstant;
import ru.excbt.datafuse.nmk.data.domain.AuditableTools;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.model.TariffType;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.repository.TariffPlanRepository;
import ru.excbt.datafuse.nmk.data.repository.TariffTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.TariffOptionRepository;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с тарифными планами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.04.2015
 *
 */
@Service
public class TariffPlanService implements SecuredRoles {

	// @Autowired
	// private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private TariffPlanRepository tariffPlanRepository;

	@Autowired
	private TariffTypeRepository tariffTypeRepository;

	@Autowired
	private TariffOptionRepository tariffOptionRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TariffPlan> selectTariffPlanList(long subscriberId) {
		return tariffPlanRepository.selectTariffPlanList(subscriberId);
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TariffPlan> selectTariffPlanList(long subscriberId, long rsoOrganizationId) {
		return tariffPlanRepository.selectTariffPlanList(subscriberId, rsoOrganizationId);
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TariffPlan> getContObjectTariffPlanList(long subscriberId, long rsoOrganizationId, long contObjectId) {
		return tariffPlanRepository.selectTariffPlanList(subscriberId, contObjectId);
	}

	/**
	 *
	 * @param rsoOrganizationId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void initDefaultTariffPlan(long subscriberId, long rsoOrganizationId) {
		List<TariffPlan> currentTariffPlan = tariffPlanRepository.selectTariffPlanList(rsoOrganizationId);
		if (currentTariffPlan.size() > 0) {
			throw new PersistenceException(
					String.format("Default Tariff Plan for rsoOrganizationId:{} already exists", rsoOrganizationId));
		}

		Organization rso = organizationRepository.findOne(rsoOrganizationId);
		checkNotNull(rso);

		//Subscriber subscriber = subscriberService.selectSubscriber(subscriberId);

		Iterable<TariffType> tarifTypes = tariffTypeRepository.findAll();
		for (TariffType ttype : tarifTypes) {
			TariffPlan tplan = new TariffPlan();
			tplan.setSubscriberId(subscriberId);
			tplan.setRso(rso);
			tplan.setTariffOptionKeyname(TariffPlanConstant.DEFAULT);
			tplan.setTariffType(ttype);
			tplan.setTariffPlanName(ttype.getTariffTypeName());
			tplan.setTariffPlanValue(BigDecimal.ZERO);
			tplan.setStartDate(DateTime.now().withDayOfMonth(1).withMillisOfDay(0).toDate());
			tplan.setTariffPlanComment(String.format("AUTO GENERATED for %s", ttype.getContServiceType()));
			tariffPlanRepository.save(tplan);
		}
	}

	/**
	 *
	 * @param rsoOrganizationId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteDefaultTariffPlan(long subscriberId, long rsoOrganizationId) {
		tariffPlanRepository.deleteTariffPlan(subscriberId, rsoOrganizationId);
	}

	/**
	 *
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public TariffPlan updateOne(SubscriberParam subscriberParam, TariffPlan tariffPlan) {
		checkNotNull(tariffPlan);
		checkArgument(!tariffPlan.isNew());

		if (!canModifyTariffPlanId(subscriberParam.getSubscriberId(), tariffPlan.getId())) {
			throw new PersistenceException(
					String.format("TariffPlan(id=%d) can not be modified by currentSubscriberId", tariffPlan.getId()));
		}

		checkNotNull(tariffPlan.getStartDate());

		if (tariffPlan.getEndDate() != null && tariffPlan.getStartDate().after(tariffPlan.getEndDate())) {
			throw new IllegalArgumentException(String
					.format("TariffPlan(id=%d) can not be modified. endDate is before startDate", tariffPlan.getId()));
		}

		TariffPlan currentRec = tariffPlanRepository.findOne(tariffPlan.getId());

		AuditableTools.copyAuditableProps(currentRec, tariffPlan);

		tariffPlan.setSubscriberId(currentRec.getSubscriberId());

		if (Boolean.TRUE.equals(tariffPlan.getIsDefault())) {
			setOtherInactive(subscriberParam, tariffPlan.getId(), tariffPlan.getRso().getId(),
					tariffPlan.getTariffType().getId());
		}

		return tariffPlanRepository.save(tariffPlan);
	}

	/**
	 *
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public TariffPlan createOne(SubscriberParam subscriberParam, TariffPlan tariffPlan) {

		checkNotNull(tariffPlan);
		checkArgument(tariffPlan.isNew());
		checkNotNull(tariffPlan.getSubscriberId(), "subscriberId is NULL");
		checkNotNull(tariffPlan.getTariffType(), "tariffType is NULL");
		checkNotNull(tariffPlan.getStartDate(), "startDate is NULL");
		checkNotNull(tariffPlan.getRso(), "rso is NULL");

		if (Boolean.TRUE.equals(tariffPlan.getIsDefault())) {
			setOtherInactive(subscriberParam, null, tariffPlan.getRso().getId(), tariffPlan.getTariffType().getId());
		}

		return tariffPlanRepository.save(tariffPlan);
	}

	/**
	 *
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(TariffPlan tariffPlan) {
		checkNotNull(tariffPlan);
		checkArgument(!tariffPlan.isNew());

		if (!canModifyTariffPlanId(tariffPlan.getSubscriberId(), tariffPlan.getId())) {
			throw new PersistenceException(
					String.format("TariffPlan(id=%d) can not be modified by currentSubscriberId", tariffPlan.getId()));
		}

		tariffPlanRepository.delete(tariffPlan);
	}

	/**
	 *
	 * @param id
	 * @param tariffPlan
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean canModifyTariffPlanId(long subscriberId, long tariffPlanId) {
		List<Long> ids = tariffPlanRepository.selectTariffPlanId(subscriberId, tariffPlanId);
		return ids.size() == 1;
	}

	/**
	 *
	 * @param tariffPlanId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectTariffPlanContObjects(long tariffPlanId, long subscriberId) {
		return tariffPlanRepository.selectTariffPlanContObjects(subscriberId, tariffPlanId);
	}

	/**
	 *
	 * @param tariffPlanId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectTariffPlanAvailableContObjects(long tariffPlanId, long subscriberId) {
		return tariffPlanRepository.selectAvailableContObjects(subscriberId, tariffPlanId);
	}

	/**
	 *
	 * @param tariffPlanId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public TariffPlan findOne(long tariffPlanId) {
		return tariffPlanRepository.findOne(tariffPlanId);
	}

	@Transactional(value = TxConst.TX_DEFAULT)
	private void setOtherInactive(SubscriberParam subscriberParam, Long tariffPlanId, Long rsoOrganizationId,
			Long tariffTypeId) {
		List<TariffPlan> allTariffs = tariffPlanRepository.selectTariffPlanList(subscriberParam.getSubscriberId());
		List<TariffPlan> modTariffs = allTariffs.stream()
				.filter(i -> (tariffPlanId == null || !tariffPlanId.equals(i.getId()))
						&& i.getRsoOrganizationId().equals(rsoOrganizationId)
						&& i.getTariffTypeId().equals(tariffTypeId))
				.collect(Collectors.toList());
		modTariffs.forEach(i -> {
			i.setIsDefault(false);
		});
		tariffPlanRepository.save(modTariffs);
	}

}
