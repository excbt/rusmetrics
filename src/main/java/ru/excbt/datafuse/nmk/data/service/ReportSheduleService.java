package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.repository.ReportSheduleRepository;
import ru.excbt.datafuse.nmk.report.ReportActionKey;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service

public class ReportSheduleService implements SecuredRoles {

	@Autowired
	private ReportSheduleRepository reportSheduleRepository;

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ReportShedule> selectReportShedule(long subscriberId, LocalDateTime dateTime) {

		List<ReportShedule> result = reportSheduleRepository.selectReportShedule(subscriberId, dateTime.toDate());

		result.forEach((s) -> s.getReportParamset().getParamSpecialList().size());

		return result;
	}

	/**
	 * 
	 * @param dateTime
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ReportShedule> selectReportShedule(long subscriberId) {
		List<ReportShedule> result = reportSheduleRepository.findBySubscriberId(subscriberId);

		result.forEach((s) -> s.getReportParamset().getParamSpecialList().size());

		return result;
	}

	/**
	 * 
	 * @param action
	 * @param param
	 * @return
	 */
	private boolean checkReportAction(ReportActionKey action, String param) {
		if (action == null && param != null) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param reportShedule
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportShedule createOne(ReportShedule reportShedule) {
		checkNotNull(reportShedule, "argument reportShedule is NULL");
		checkArgument(reportShedule.isNew());

		checkNotNull(reportShedule.getSubscriber(), "Subscriber of reportShedule is NULL");
		checkNotNull(reportShedule.getReportTemplate(), "ReportTemplate of reportShedule is NULL");
		checkNotNull(reportShedule.getReportParamset(), "ReportParamset of reportShedule is NULL");
		checkNotNull(reportShedule.getReportSheduleTypeKey(), "getReportSheduleTypeKey of reportShedule IS NULL");

		checkNotNull(reportShedule.getSheduleStartDate(), "SheduleStartDate of reportShedule is NULL");

		if (reportShedule.getSheduleEndDate() != null) {
			checkArgument(reportShedule.getSheduleStartDate().compareTo(reportShedule.getSheduleEndDate()) <= 0,
					"SheduleEndDate of reportShedule is less than SheduleStartDate");
		}

		checkArgument(checkReportAction(reportShedule.getSheduleAction1Key(), reportShedule.getSheduleAction1Param()),
				"SheduleAction1 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction2Key(), reportShedule.getSheduleAction2Param()),
				"SheduleAction2 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction3Key(), reportShedule.getSheduleAction3Param()),
				"SheduleAction3 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction4Key(), reportShedule.getSheduleAction4Param()),
				"SheduleAction4 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction5Key(), reportShedule.getSheduleAction5Param()),
				"SheduleAction5 is not properly configured");

		ReportShedule resultEntity = reportSheduleRepository.save(reportShedule);

		return resultEntity;
	}

	/**
	 * 
	 * @param reportShedule
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public ReportShedule updateOne(ReportShedule reportShedule) {
		checkNotNull(reportShedule, "argument reportShedule is NULL");
		checkArgument(!reportShedule.isNew());

		checkNotNull(reportShedule.getSubscriber(), "Subscriber of reportShedule is NULL");
		checkNotNull(reportShedule.getReportTemplate(), "ReportTemplate of reportShedule is NULL");
		checkNotNull(reportShedule.getReportParamset(), "ReportParamset of reportShedule is NULL");
		checkNotNull(reportShedule.getReportSheduleTypeKey(), "getReportSheduleTypeKey of reportShedule IS NULL");

		checkNotNull(reportShedule.getSheduleStartDate(), "SheduleStartDate of reportShedule is NULL");

		if (reportShedule.getSheduleEndDate() != null) {
			checkArgument(reportShedule.getSheduleStartDate().compareTo(reportShedule.getSheduleEndDate()) <= 0,
					"SheduleEndDate of reportShedule is less than SheduleStartDate");
		}

		checkArgument(checkReportAction(reportShedule.getSheduleAction1Key(), reportShedule.getSheduleAction1Param()),
				"SheduleAction1 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction2Key(), reportShedule.getSheduleAction2Param()),
				"SheduleAction2 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction3Key(), reportShedule.getSheduleAction3Param()),
				"SheduleAction3 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction4Key(), reportShedule.getSheduleAction4Param()),
				"SheduleAction4 is not properly configured");
		checkArgument(checkReportAction(reportShedule.getSheduleAction5Key(), reportShedule.getSheduleAction5Param()),
				"SheduleAction5 is not properly configured");

		ReportShedule resultEntity = reportSheduleRepository.save(reportShedule);

		resultEntity.getReportParamset().getParamSpecialList().size();

		return resultEntity;
	}

	/**
	 * 
	 * @param reportSheduleId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportShedule findOne(long reportSheduleId) {
		ReportShedule result = reportSheduleRepository.findOne(reportSheduleId);
		result.getReportParamset().getParamSpecialList().size();
		return result;
	}

	/**
	 * 
	 * @param reportSheduleId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(long reportSheduleId) {
		reportSheduleRepository.delete(reportSheduleId);
	}

	/**
	 * 
	 * @param reportParamsetId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteByReportParamset(long reportParamsetId) {
		reportSheduleRepository.softDeleteByReportParamset(reportParamsetId);
	}

}
