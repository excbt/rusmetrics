package ru.excbt.datafuse.nmk.data.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointStatInfo;
import ru.excbt.datafuse.nmk.data.model.support.MinCheck;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.model.vo.ContZPointVO;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContServiceTypeRepository;
import ru.excbt.datafuse.nmk.data.service.support.*;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

	private static final Logger log = LoggerFactory.getLogger(ContZPointService.class);

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

    @Autowired
	private ObjectAccessService objectAccessService;

    @Autowired
    private SubscriberAccessService subscriberAccessService;

	/**
	 * Краткая информация по точке учета
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 17.03.2016
	 *
	 */
	public static class ShortInfo implements ContZPointShortInfo{
		private final Long contZPointId;
		private final Long contObjectId;
		private final String customServiceName;
		private final String contServiceType;
		private final String contServiceTypeCaption;

		public ShortInfo(Long contZPointId, Long contObjectId, String customServiceName,
				String contServiceType, String contServiceTypeCaption)

        {
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
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContZPoint findOne(long contZpointId) {
		ContZPoint result = contZPointRepository.findOne(contZpointId);
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
	public List<ContZPoint> findContObjectZPoints(SubscriberParam subscriberParam, Long contObjectId) {
		List<ContZPoint> result = objectAccessService.findAllContZPoints(subscriberParam, contObjectId);

//            contZPointRepository.findByContObjectId(contObjectId);

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
	public List<ContZPointVO> selectContObjectZPointsVO(SubscriberParam subscriberParam, long contObjectId) {
		List<ContZPoint> zPoints = objectAccessService.findAllContZPoints(subscriberParam, contObjectId);
            contZPointRepository.findByContObjectId(contObjectId);
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
	 * @param zpointList
	 * @return
	 */
	@Deprecated
	private List<ContZPointEx> processContZPointHwater(List<ContZPoint> zpointList) {
		List<ContZPointEx> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zpointList) {

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

	private List<ContZPointVO> makeContZPointVO_Hwater(List<ContZPoint> zpointList) {
		List<ContZPointVO> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zpointList) {

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
	 * @param zpointList
	 * @return
	 */
	@Deprecated
	private List<ContZPointEx> processContZPointEl(List<ContZPoint> zpointList) {
		List<ContZPointEx> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zpointList) {

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
	 * @param zpointList
	 * @return
	 */
	private List<ContZPointVO> makeContZPointVO_El(List<ContZPoint> zpointList) {
		List<ContZPointVO> result = new ArrayList<>();
		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zpointList) {

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
	 * @param contZpointId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkContZPointOwnership(long contZpointId, long contObjectId) {
		List<?> checkIds = contZPointRepository.findByIdAndContObject(contZpointId, contObjectId);
		return checkIds.size() > 0;
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectContZPointIds(long contObjectId) {
		return contZPointRepository.findContZPointIds(contObjectId);
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
				.map(i -> new ImmutablePair<>(DBRowUtil.asString(i[0]), DBRowUtil.asLong(i[1])))
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
		List<Long> contZPointIds = contZPointRepository.findContZPointIds(contObjectId);

		// Date fromDateTime = null;

		MinCheck<Date> minCheck = new MinCheck<>();

		for (Long id : contZPointIds) {
			Date zPointLastDate = contServiceDataHWaterService.selectLastDataDate(id, minCheck.getObject());

			Date startDay = zPointLastDate == null ? null : JodaTimeUtils.startOfDay(zPointLastDate).toDate();

			minCheck.check(startDay);

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
		linkToContObject(result);

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
	 * @param contZpointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN })
	public void deleteManualZPoint(Long contZpointId) {
		ContZPoint contZPoint = findOne(contZpointId);
		checkNotNull(contZPoint);
		if (ExSystemKey.MANUAL.isNotEquals(contZPoint.getExSystemKeyname())) {
			throw new PersistenceException(String.format("Delete ContZPoint(id=%d) with exSystem=%s is not supported ",
					contZpointId, contZPoint.getExSystemKeyname()));
		}
		contZPointRepository.delete(contZPoint);
	}

    /**
     *
     * @param contObjectId
     * @param contZPoint
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public ContZPoint createOne(PortalUserIds userIds, Long contObjectId, ContZPoint contZPoint) {
		checkNotNull(contObjectId);
		checkNotNull(contZPoint);
		checkNotNull(contZPoint.getStartDate());
		checkNotNull(contZPoint.getContServiceTypeKeyname());
		checkNotNull(contZPoint.get_activeDeviceObjectId());
		checkNotNull(contZPoint.getRsoId());

		contZPoint.setContObjectId(contObjectId);
		linkToContObject(contZPoint);
		initDeviceObject(contZPoint);
		initContServiceType(contZPoint);
		initRso(contZPoint);
		contZPoint.setIsManual(true);

		ContZPoint result = contZPointRepository.save(contZPoint);
		contZPointSettingModeService.initContZPointSettingMode(result.getId());

        subscriberAccessService.grantContZPointAccess(new Subscriber().id(userIds.getSubscriberId()), result);

		return result;
	}

	/**
	 *
	 * @param contZpointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOne(PortalUserIds userIds, Long contZpointId) {
		ContZPoint contZPoint = findOne(contZpointId);
		checkNotNull(contZPoint);
		contZPointRepository.save(softDelete(contZPoint));

        subscriberAccessService.revokeContZPointAccess(new Subscriber().id(userIds.getSubscriberId()), contZPoint);

	}

	/**
	 *
	 * @param contZpointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOnePermanent(Long contZpointId) {
		checkNotNull(contZpointId);
		ContZPoint contZPoint = findOne(contZpointId);
		checkNotNull(contZPoint);
		contZPointSettingModeService.deleteByContZPoint(contZpointId);
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

		linkToContObject(contZPoint);
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
	private void linkToContObject(ContZPoint contZPoint) {
		ContObject contObject = contObjectService.findContObjectChecked(contZPoint.getContObjectId());
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

        Organization org = organizationService.findOneOrganization(contZPoint.getRsoId())
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Organization.class, contZPoint.getRsoId()));


		if (!Boolean.TRUE.equals(org.getFlagRso())) {
			throw new PersistenceException(String.format("Organization (id=%d) is not RSO", contZPoint.getRsoId()));
		}

		contZPoint.setRso(org);
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
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<DeviceObject> selectDeviceObjects(long contZpointId) {
		return contZPointRepository.selectDeviceObjects(contZpointId);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> selectDeviceObjectIds(long contZpointId) {
		return contZPointRepository.selectDeviceObjectIds(contZpointId);
	}

	/**
	 *
	 * @param contZpointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public Long selectContObjectId(long contZpointId) {
		List<Long> ids = contZPointRepository.selectContObjectByContZPointId(contZpointId);
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
