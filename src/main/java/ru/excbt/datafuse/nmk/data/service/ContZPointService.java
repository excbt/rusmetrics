package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointStatInfo;
import ru.excbt.datafuse.nmk.data.model.support.MinCheck;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContServiceTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

@Service
public class ContZPointService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContZPointService.class);

	private final static boolean CONT_ZPOINT_EX_OPTIMIZE = false;

	@Autowired
	private ContZPointRepository contZPointRepository;

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private ContServiceTypeRepository contServiceTypeRepository;

	/**
	 * /**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContZPoint findContZPoint(long contZPointId) {
		ContZPoint result = contZPointRepository.findOne(contZPointId);
		result.getDeviceObjects().forEach(j -> {
			j.loadLazyProps();
		});
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

		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zPoints) {

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

		result.forEach(i -> {
			i.getObject().getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});
		});

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

		ContObject contObject = contObjectService.findOne(contObjectId);
		if (contObject == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}

		ContServiceType contServiceType = contServiceTypeRepository.findOne(contServiceTypeKey.getKeyname());
		checkNotNull(contServiceType);

		ContZPoint result = new ContZPoint();
		result.setContObject(contObject);
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
		ContZPoint contZPoint = findContZPoint(contZPointId);
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
		checkNotNull(contZPoint.getDeviceObjectId());

		ContObject contObject = contObjectService.findOne(contObjectId);
		if (contObject == null) {
			throw new PersistenceException(String.format("ContObject(id=%d) is not found", contObjectId));
		}

		DeviceObject deviceObject = deviceObjectService.findOne(contZPoint.getDeviceObjectId());

		if (deviceObject == null) {
			throw new PersistenceException(
					String.format("DeviceObject(id=%d) is not found", contZPoint.getDeviceObjectId()));
		}

		ContServiceType contServiceType = contServiceTypeRepository.findOne(contZPoint.getContServiceTypeKeyname());
		checkNotNull(contServiceType);

		contZPoint.setContObject(contObject);
		contZPoint.getDeviceObjects().add(deviceObject);
		contZPoint.setContServiceType(contServiceType);
		contZPoint.setIsManual(true);

		return contZPointRepository.save(contZPoint);
	}

	/**
	 * 
	 * @param contZPointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOne(Long contZPointId) {
		ContZPoint contZPoint = findContZPoint(contZPointId);
		checkNotNull(contZPoint);
		contZPoint.setDeleted(1);
		contZPointRepository.save(contZPoint);
	}

	/**
	 * 
	 * @param contZPointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	public void deleteOnePermanent(Long contZPointId) {
		ContZPoint contZPoint = findContZPoint(contZPointId);
		checkNotNull(contZPoint);
		contZPointRepository.delete(contZPoint);
	}

}
