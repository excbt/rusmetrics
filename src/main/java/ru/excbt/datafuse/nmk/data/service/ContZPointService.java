package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointStatInfo;
import ru.excbt.datafuse.nmk.data.model.support.MinCheck;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.utils.JodaTimeUtils;

@Service
@Transactional
public class ContZPointService {

	private final static boolean CONT_ZPOINT_EX_OPTIMIZE = false;

	@Autowired
	private ContZPointRepository contZPointRepository;

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	/**
	 * /**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = false)
	public ContZPoint findOne(long contZPointId) {
		return contZPointRepository.findOne(contZPointId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPoint> findContZPoints(long contObjectId) {
		List<ContZPoint> result = contZPointRepository
				.findByContObjectId(contObjectId);
		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPointEx> findContZPointsEx(long contObjectId) {
		List<ContZPoint> zPoints = contZPointRepository
				.findByContObjectId(contObjectId);
		List<ContZPointEx> result = new ArrayList<>();

		MinCheck<Date> minCheck = new MinCheck<>();

		for (ContZPoint zp : zPoints) {

			if (CONT_ZPOINT_EX_OPTIMIZE) {

				Boolean existsData = null;
				existsData = contServiceDataHWaterService
						.selectExistsAnyData(zp.getId());
				result.add(new ContZPointEx(zp, existsData));

			} else {
				
				Date zPointLastDate = contServiceDataHWaterService
						.selectLastDataDate(zp.getId(), minCheck.getObject());

				Date startDay = zPointLastDate == null ? null : JodaTimeUtils
						.startOfDay(zPointLastDate).toDate();

				minCheck.check(startDay);
				result.add(new ContZPointEx(zp, zPointLastDate));
			}

		}

		return result;
	}

	/**
	 * 
	 * @param contZPointId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean checkContZPointOwnership(long contZPointId, long contObjectId) {
		List<?> checkIds = contZPointRepository.findByIdAndContObject(
				contZPointId, contObjectId);
		return checkIds.size() > 0;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Long> selectContZPointIds(long contObjectId) {
		return contZPointRepository.selectContZPointIds(contObjectId);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPointStatInfo> selectContZPointStatInfo(Long contObjectId) {
		List<ContZPointStatInfo> resultList = new ArrayList<>();
		List<Long> contZPointIds = contZPointRepository
				.selectContZPointIds(contObjectId);

		// Date fromDateTime = null;

		MinCheck<Date> minCheck = new MinCheck<>();

		for (Long id : contZPointIds) {
			Date zPointLastDate = contServiceDataHWaterService
					.selectLastDataDate(id, minCheck.getObject());

			Date startDay = zPointLastDate == null ? null : JodaTimeUtils
					.startOfDay(zPointLastDate).toDate();

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

}
