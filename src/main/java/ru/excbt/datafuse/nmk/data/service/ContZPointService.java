package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.V_DeviceObjectTimeOffset;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointStatInfo;
import ru.excbt.datafuse.nmk.data.model.support.MinCheck;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.model.vo.ContZPointVO;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContServiceTypeRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.DBRowUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

/**
 * Сервис для работы с точками учета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.03.2015
 *
 */
@Service
public class ContZPointService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContZPointService.class);

	private final static boolean CONT_ZPOINT_EX_OPTIMIZE = false;
	private final static String[] CONT_SERVICE_HWATER = new String[] { ContServiceTypeKey.HEAT.getKeyname(),
			ContServiceTypeKey.HW.getKeyname(), ContServiceTypeKey.CW.getKeyname() };

	private final static String[] CONT_SERVICE_EL = new String[] { ContServiceTypeKey.EL.getKeyname() };

	private final static List<String> CONT_SERVICE_HWATER_LIST = Arrays.asList(CONT_SERVICE_HWATER);
	private final static List<String> CONT_SERVICE_EL_LIST = Arrays.asList(CONT_SERVICE_EL);

	@Autowired
	private ContZPointRepository contZPointRepository;

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Autowired
	private ContServiceDataElService contServiceDataElService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private ContServiceTypeRepository contServiceTypeRepository;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private ContZPointSettingModeService contZPointSettingModeService;

	@Autowired
	private TemperatureChartService temperatureChartService;

	/**
	 * Краткая информация по точке учета
	 * 
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 17.03.2016
	 *
	 */
	public static class ContZPointShortInfo {
		private final Long contZPointId;
		private final Long contObjectId;
		private final String customServiceName;
		private final String contServiceType;
		private final String contServiceTypeCaption;

		public ContZPointShortInfo(Long contZPointId, Long contObjectId, String customServiceName,
				String contServiceType, String contServiceTypeCaption) {
			this.contZPointId = contZPointId;
			this.contObjectId = contObjectId;
			this.customServiceName = customServiceName;
			this.contServiceType = contServiceType;
			this.contServiceTypeCaption = contServiceTypeCaption;
		}

		public Long getContZPointId() {
			return contZPointId;
		}

		public Long getContObjectId() {
			return contObjectId;
		}

		public String getCustomServiceName() {
			return customServiceName;
		}

		public String getContServiceType() {
			return contServiceType;
		}

		public String getContServiceTypeCaption() {
			return contServiceTypeCaption;
		}
	}

	/**
	 * /**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContZPoint findOne(long contZPointId) {
		ContZPoint result = contZPointRepository.findOne(contZPointId);
		if (result != null && result.getDeviceObjects() != null) {
			result.getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});
		}
		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPoint> findContObjectZPoints(long contObjectId) {
		List<ContZPoint> result = contZPointRepository.findByContObjectId(contObjectId);

		result.forEach(i -> {
			i.getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});

			if (i.getTemperatureChart() != null) {
				i.getTemperatureChart().getId();
				i.getTemperatureChart().getChartComment();
			}

		});

		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPointEx> findContObjectZPointsEx(long contObjectId) {
		List<ContZPoint> zPoints = contZPointRepository.findByContObjectId(contObjectId);
		List<ContZPointEx> result = new ArrayList<>();

		List<ContZPointEx> resultHWater = processContZPointHwater(zPoints);
		List<ContZPointEx> resultEl = processContZPointEl(zPoints);

		result.addAll(resultHWater);
		result.addAll(resultEl);

		result.forEach(i -> {
			i.getObject().getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});

			if (i.getObject().getTemperatureChart() != null) {
				i.getObject().getTemperatureChart().getId();
				i.getObject().getTemperatureChart().getChartComment();
			}

		});

		return result;
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPointVO> selectContObjectZPointsVO(long contObjectId) {
		List<ContZPoint> zPoints = contZPointRepository.findByContObjectId(contObjectId);
		List<ContZPointVO> result = new ArrayList<>();

		List<ContZPointVO> resultHWater = makeContZPointVO_Hwater(zPoints);
		List<ContZPointVO> resultEl = makeContZPointVO_El(zPoints);

		result.addAll(resultHWater);
		result.addAll(resultEl);

		result.forEach(i -> {
			i.getModel().getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});

			if (i.getModel().getTemperatureChart() != null) {
				i.getModel().getTemperatureChart().getId();
				i.getModel().getTemperatureChart().getChartComment();
			}

			V_DeviceObjectTimeOffset deviceObjectTimeOffset = deviceObjectService
					.selectDeviceObjsetTimeOffset(i.getModel().get_activeDeviceObjectId());

			i.setDeviceObjectTimeOffset(deviceObjectTimeOffset);
		});

		return result;
	}

	/**
	 * 
	 * @param zPointList
	 * @return
	 */
	@Deprecated
	private List<ContZPointEx> processContZPointHwater(List<ContZPoint> zPointList) {
		List<ContZPointEx> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zPointList) {

			if (!CONT_SERVICE_HWATER_LIST.contains(zp.getContServiceTypeKeyname())) {
				continue;
			}

			if (CONT_ZPOINT_EX_OPTIMIZE) {

				Boolean existsData = null;
				existsData = contServiceDataHWaterService.selectExistsAnyData(zp.getId());

				result.add(new ContZPointEx(zp, existsData));

			} else {

				Date zPointLastDate = contServiceDataHWaterService.selectLastDataDate(zp.getId(), minCheck.getObject());

				Date startDay = zPointLastDate == null ? null : JodaTimeUtils.startOfDay(zPointLastDate).toDate();

				minCheck.check(startDay);
				result.add(new ContZPointEx(zp, zPointLastDate));
			}

		}
		return result;
	}

	private List<ContZPointVO> makeContZPointVO_Hwater(List<ContZPoint> zPointList) {
		List<ContZPointVO> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zPointList) {

			if (!CONT_SERVICE_HWATER_LIST.contains(zp.getContServiceTypeKeyname())) {
				continue;
			}

			Date zPointLastDate = contServiceDataHWaterService.selectLastDataDate(zp.getId(), minCheck.getObject());

			Date startDay = zPointLastDate == null ? null : JodaTimeUtils.startOfDay(zPointLastDate).toDate();

			minCheck.check(startDay);
			result.add(new ContZPointVO(zp, zPointLastDate));

		}
		return result;
	}

	/**
	 * 
	 * @param zPointList
	 * @return
	 */
	@Deprecated
	private List<ContZPointEx> processContZPointEl(List<ContZPoint> zPointList) {
		List<ContZPointEx> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zPointList) {

			if (!CONT_SERVICE_EL_LIST.contains(zp.getContServiceTypeKeyname())) {
				continue;
			}

			if (CONT_ZPOINT_EX_OPTIMIZE) {

				Boolean existsData = null;
				existsData = contServiceDataElService.selectExistsAnyConsData(zp.getId());

				result.add(new ContZPointEx(zp, existsData));

			} else {

				Date zPointLastDate = contServiceDataElService.selectLastConsDataDate(zp.getId(), minCheck.getObject());

				Date startDay = zPointLastDate == null ? null : JodaTimeUtils.startOfDay(zPointLastDate).toDate();

				minCheck.check(startDay);
				result.add(new ContZPointEx(zp, zPointLastDate));
			}

		}
		return result;
	}

	/**
	 * 
	 * @param zPointList
	 * @return
	 */
	private List<ContZPointVO> makeContZPointVO_El(List<ContZPoint> zPointList) {
		List<ContZPointVO> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zPointList) {

			if (!CONT_SERVICE_EL_LIST.contains(zp.getContServiceTypeKeyname())) {
				continue;
			}

			Date zPointLastDate = contServiceDataElService.selectLastConsDataDate(zp.getId(), minCheck.getObject());

			Date startDay = zPointLastDate == null ? null : JodaTimeUtils.startOfDay(zPointLastDate).toDate();

			minCheck.check(startDay);
			result.add(new ContZPointVO(zp, zPointLastDate));

		}
		return result;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkContZPointOwnership(long contZPointId, long contObjectId) {
		List<?> checkIds = contZPointRepository.findByIdAndContObject(contZPointId, contObjectId);
		return checkIds.size() > 0;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectContZPointIds(long contObjectId) {
		return contZPointRepository.selectContZPointIds(contObjectId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Pair<String, Long>> selectContZPointServiceTypeIds(long contObjectId) {

		List<Object[]> qryResult = contZPointRepository.selectContZPointServiceTypeIds(contObjectId);

		List<Pair<String, Long>> resultPairList = qryResult.stream()
				.map(i -> new ImmutablePair<>(DBRowUtils.asString(i[0]), DBRowUtils.asLong(i[1])))
				.collect(Collectors.toList());

		return resultPairList;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPointStatInfo> selectContZPointStatInfo(Long contObjectId) {
		List<ContZPointStatInfo> resultList = new ArrayList<>();
		List<Long> contZPointIds = contZPointRepository.selectContZPointIds(contObjectId);

		// Date fromDateTime = null;

		MinCheck<Date> minCheck = new MinCheck<>();

		for (Long id : contZPointIds) {
			Date zPointLastDate = contServiceDataHWaterService.selectLastDataDate(id, minCheck.getObject());

			Date startDay = zPointLastDate == null ? null : JodaTimeUtils.startOfDay(zPointLastDate).toDate();

			minCheck.check(startDay);

			// if (zPointLastDate != null) {
			// if (fromDateTime == null) {
			// fromDateTime = zPointLastDate;
			// } else if (fromDateTime.compareTo(zPointLastDate) < 0) {
			// fromDateTime = zPointLastDate;
			// }
			// }

			ContZPointStatInfo item = new ContZPointStatInfo(id, zPointLastDate);
			resultList.add(item);
		}
		return resultList;
	}

	/**
	 * 
	 * @param contZPoint
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN })
	public ContZPoint updateContZPoint(ContZPoint contZPoint) {
		checkNotNull(contZPoint);
		checkArgument(!contZPoint.isNew());
		ContZPoint result = contZPointRepository.save(contZPoint);
		result.getDeviceObjects().forEach(j -> {
			j.loadLazyProps();
		});
		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contServiceTypeKey
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN })
	public ContZPoint createManualZPoint(Long contObjectId, ContServiceTypeKey contServiceTypeKey, LocalDate startDate,
			Integer tsNumber, Boolean isDoublePipe, DeviceObject deviceObject) {
		checkNotNull(contObjectId);
		checkNotNull(contServiceTypeKey);
		checkNotNull(startDate);

		ContServiceType contServiceType = contServiceTypeRepository.findOne(contServiceTypeKey.getKeyname());
		checkNotNull(contServiceType);

		ContZPoint result = new ContZPoint();

		result.setContObjectId(contObjectId);
		initContObject(result);

		result.setContServiceType(contServiceType);
		result.setExSystemKeyname(ExSystemKey.MANUAL.getKeyname());
		result.setIsManualLoading(true);
		result.setStartDate(startDate.toDate());
		result.setTsNumber(tsNumber);
		result.setDoublePipe(isDoublePipe);
		if (deviceObject != null) {
			result.getDeviceObjects().add(deviceObject);
		}
		return contZPointRepository.save(result);
	}

	/**
	 * 
	 * @param contZPointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN })
	public void deleteManualZPoint(Long contZPointId) {
		ContZPoint contZPoint = findOne(contZPointId);
		checkNotNull(contZPoint);
		if (ExSystemKey.MANUAL.isNotEquals(contZPoint.getExSystemKeyname())) {
			throw new PersistenceException(String.format("Delete ContZPoint(id=%d) with exSystem=%s is not supported ",
					contZPointId, contZPoint.getExSystemKeyname()));
		}
		contZPointRepository.delete(contZPoint);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contServiceTypeKey
	 * @param startDate
	 * @param tsNumber
	 * @param isDoublePipe
	 * @param deviceObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public ContZPoint createOne(Long contObjectId, ContZPoint contZPoint) {
		checkNotNull(contObjectId);
		checkNotNull(contZPoint);
		checkNotNull(contZPoint.getStartDate());
		checkNotNull(contZPoint.getContServiceTypeKeyname());
		checkNotNull(contZPoint.get_activeDeviceObjectId());
		checkNotNull(contZPoint.getRsoId());

		contZPoint.setContObjectId(contObjectId);
		initContObject(contZPoint);
		initDeviceObject(contZPoint);
		initContServiceType(contZPoint);
		initRso(contZPoint);
		contZPoint.setIsManual(true);

		ContZPoint result = contZPointRepository.save(contZPoint);
		contZPointSettingModeService.initContZPointSettingMode(result.getId());
		return result;
	}

	/**
	 * 
	 * @param contZPointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOne(Long contZPointId) {
		ContZPoint contZPoint = findOne(contZPointId);
		checkNotNull(contZPoint);
		contZPointRepository.save(softDelete(contZPoint));
	}

	/**
	 * 
	 * @param contZPointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOnePermanent(Long contZPointId) {
		checkNotNull(contZPointId);
		ContZPoint contZPoint = findOne(contZPointId);
		checkNotNull(contZPoint);
		contZPointSettingModeService.deleteByContZPoint(contZPointId);
		contZPointRepository.delete(contZPoint);
	}

	/**
	 * 
	 * @param contZPoint
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public ContZPoint updateOne(ContZPoint contZPoint) {
		checkNotNull(contZPoint);
		checkNotNull(contZPoint.getContObjectId());
		checkNotNull(contZPoint.getStartDate());
		checkNotNull(contZPoint.getContServiceTypeKeyname());
		checkNotNull(contZPoint.get_activeDeviceObjectId());
		checkNotNull(contZPoint.getRsoId());

		initContObject(contZPoint);
		initDeviceObject(contZPoint);
		initContServiceType(contZPoint);
		initRso(contZPoint);

		if (contZPoint.getTemperatureChartId() != null) {
			TemperatureChart chart = temperatureChartService.selectTemperatureChart(contZPoint.getTemperatureChartId());
			if (chart == null) {
				throw new PersistenceException(
						String.format("TemperatureChart (id=%d) is not found", contZPoint.getTemperatureChartId()));
			}
			contZPoint.setTemperatureChart(chart);
		}

		contZPoint.setIsManual(true);

		ContZPoint result = contZPointRepository.save(contZPoint);
		contZPointSettingModeService.initContZPointSettingMode(result.getId());

		return result;
	}

	/**
	 * 
	 * @param contZPoint
	 */
	private void initContObject(ContZPoint contZPoint) {
		ContObject contObject = contObjectService.findContObject(contZPoint.getContObjectId());
		if (contObject == null) {
			throw new PersistenceException(
					String.format("ContObject(id=%d) is not found", contZPoint.getContObjectId()));
		}
		contZPoint.setContObject(contObject);
	}

	/**
	 * 
	 * @param contZPoint
	 */
	private void initDeviceObject(ContZPoint contZPoint) {
		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(contZPoint.get_activeDeviceObjectId());

		if (deviceObject == null) {
			throw new PersistenceException(
					String.format("DeviceObject(id=%d) is not found", contZPoint.get_activeDeviceObjectId()));
		}
		contZPoint.getDeviceObjects().clear();
		contZPoint.getDeviceObjects().add(deviceObject);
	}

	/**
	 * 
	 * @param contZPoint
	 */
	private void initContServiceType(ContZPoint contZPoint) {
		ContServiceType contServiceType = contServiceTypeRepository.findOne(contZPoint.getContServiceTypeKeyname());
		checkNotNull(contServiceType);
		contZPoint.setContServiceType(contServiceType);
	}

	/**
	 * 
	 * @param contZPoint
	 */
	private void initRso(ContZPoint contZPoint) {
		Organization organization = organizationService.selectOrganization(contZPoint.getRsoId());
		if (organization == null) {
			throw new PersistenceException(
					String.format("RSO organization (id=%d) is not found", contZPoint.getRsoId()));
		}

		if (!Boolean.TRUE.equals(organization.getFlagRso())) {
			throw new PersistenceException(String.format("Organization (id=%d) is not RSO", contZPoint.getRsoId()));
		}

		contZPoint.setRso(organization);
	}

	/**
	 * 
	 * @return
	 */
	public List<ContServiceType> selectContServiceTypes() {
		List<ContServiceType> serviceTypes = contServiceTypeRepository.selectAll();
		return serviceTypes;
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<DeviceObject> selectDeviceObjects(long contZPointId) {
		return contZPointRepository.selectDeviceObjects(contZPointId);
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> selectDeviceObjectIds(long contZPointId) {
		return contZPointRepository.selectDeviceObjectIds(contZPointId);
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public Long selectContObjectId(long contZPointId) {
		List<Long> ids = contZPointRepository.selectContObjectByContZPointId(contZPointId);
		return ids.isEmpty() ? null : ids.get(0);
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPoint> selectContPointsByDeviceObject(Long deviceObjectId) {
		return Lists.newArrayList(contZPointRepository.selectContZPointsByDeviceObjectId(deviceObjectId));
	}

}
