package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.LogSessionStep;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.vo.LogSessionVO;
import ru.excbt.datafuse.nmk.data.repository.LogSessionRepository;
import ru.excbt.datafuse.nmk.data.repository.LogSessionStepRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;

@Service
public class LogSessionService extends AbstractService {

	@Autowired
	private LogSessionRepository logSessionRepository;

	@Autowired
	private LogSessionStepRepository logSessionStepRepository;

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	/**
	 * 
	 * @param logSessionList
	 */
	private void loadLogSessionProps(final List<LogSession> logSessionList) {

		Set<Long> dataSourceIds = logSessionList.stream().filter(i -> i.getDataSourceId() != null)
				.map(i -> i.getDataSourceId()).collect(Collectors.toSet());
		Set<Long> deviceObjectIds = logSessionList.stream().filter(i -> i.getDeviceObjectId() != null)
				.map(i -> i.getDeviceObjectId()).collect(Collectors.toSet());

		List<SubscrDataSource> subscrDataSources = dataSourceIds.isEmpty() ? new ArrayList<>()
				: subscrDataSourceService.selectDataSourceByIds(dataSourceIds);

		List<DeviceObject> deviceObjects = deviceObjectIds.isEmpty() ? new ArrayList<>()
				: deviceObjectService.selectDeviceObjectsByIds(deviceObjectIds);

		final Map<Long, SubscrDataSource> dataSourceMap = subscrDataSources.stream()
				.collect(Collectors.toMap(SubscrDataSource::getId,
						Function.identity()));

		final Map<Long, DeviceObject> deviceObjectsMap = deviceObjects.stream()
				.collect(Collectors.toMap(DeviceObject::getId,
						Function.identity()));

		logSessionList.forEach(i -> {
			i.setSubscrDataSource(dataSourceMap.get(i.getDataSourceId()));
			i.setDeviceObject(deviceObjectsMap.get(i.getDeviceObjectId()));
		});

	}

	/**
	 * 
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LogSessionVO> selectLogSessions(List<Long> dataSourceIds, LocalDatePeriod localDatePeriod) {
		checkNotNull(localDatePeriod);
		if (localDatePeriod.isInvalidEq() || dataSourceIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<LogSession> preResult = logSessionRepository.selectLogSessions(dataSourceIds,
				localDatePeriod.getDateFrom(),
				localDatePeriod.getDateTo());

		loadLogSessionProps(preResult);

		return preResult.stream().map(i -> new LogSessionVO(i)).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param localDatePeriod
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LogSessionVO> selectLogSessions(List<Long> dataSourceIds, LocalDatePeriod localDatePeriod,
			List<Long> contObjectIds) {
		checkNotNull(localDatePeriod);
		checkNotNull(contObjectIds);
		if (localDatePeriod.isInvalidEq() || dataSourceIds.isEmpty() || contObjectIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<LogSession> preResult = logSessionRepository.selectLogSessions(dataSourceIds,
				localDatePeriod.getDateFrom(),
				localDatePeriod.getDateTo(),
				contObjectIds);

		loadLogSessionProps(preResult);

		return preResult.stream().map(i -> new LogSessionVO(i)).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LogSessionStep> selectLogSessionSteps(Long sessionId) {
		checkNotNull(sessionId);
		return logSessionStepRepository.selectSessionSteps(sessionId);
	}

}
