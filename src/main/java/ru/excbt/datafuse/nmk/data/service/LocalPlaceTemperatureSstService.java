package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.model.WeatherForecastCalc;
import ru.excbt.datafuse.nmk.data.repository.LocalPlaceTemperatureSstRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class LocalPlaceTemperatureSstService extends AbstractService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceTemperatureSstService.class);

	@Autowired
	private LocalPlaceTemperatureSstRepository localPlaceTemperatureSstRepository;

	@Autowired
	private LocalPlaceService localPlaceService;

	@Autowired
	private WeatherForecastService weatherForecastService;

	/**
	 *
	 * @param localPlaceId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<LocalPlaceTemperatureSst> selectSstByLocalPlace(Long localPlaceId, LocalDate sstDate) {
		checkNotNull(localPlaceId);
		checkNotNull(sstDate);
		LocalDate beginDate = sstDate.withDayOfMonth(1);
		LocalDate endDate = beginDate.plusMonths(1).minusDays(1);

		List<LocalPlaceTemperatureSst> resultList = localPlaceTemperatureSstRepository.selectByLocalPlace(localPlaceId,
				beginDate.toDate(), endDate.toDate());

		return initSstCalc(resultList);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public LocalPlaceTemperatureSst saveSst(LocalPlaceTemperatureSst entity) {
		checkNotNull(entity);
		checkNotNull(entity.getLocalPlaceId());
		checkNotNull(entity.getSstDate());

		LocalPlace localPlace = localPlaceService.findLocalPlace(entity.getLocalPlaceId());
		if (localPlace == null) {
			throw new PersistenceException(String.format("LocalPlace (id=%d) is not found", entity.getLocalPlaceId()));
		}

		LocalPlaceTemperatureSst sst = null;
		if (!entity.isNew()) {
			sst = localPlaceTemperatureSstRepository.findOne(entity.getId());
			if (sst == null) {
				throw new PersistenceException(
						String.format("LocalPlaceTemperatureSst (id=%d) is not found", entity.getId()));
			}
			sst.setSstValue(entity.getSstValue());
			sst.setSstComment(entity.getSstComment());
		} else {
			sst = entity;
		}

		return localPlaceTemperatureSstRepository.save(sst);
	}

	/**
	 *
	 * @param entityList
	 * @return
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<LocalPlaceTemperatureSst> saveSstList(List<LocalPlaceTemperatureSst> entityList) {
		checkNotNull(entityList);

		Date checkDate = !entityList.isEmpty() ? entityList.get(0).getSstDate() : null;

		LocalDate checkMonth = checkDate != null ? (new LocalDate(checkDate)).withDayOfMonth(1) : null;

		for (LocalPlaceTemperatureSst sst : entityList) {

			LocalDate sstMonth = sst.getSstDate() != null ? (new LocalDate(sst.getSstDate())).withDayOfMonth(1) : null;

			if (sstMonth == null || checkMonth == null || !checkMonth.equals(sstMonth)) {
				new IllegalArgumentException(
						String.format("sstDate is not consistence. Required sstDate = %s, actual sstDate = %s ",
								sst.getSstDate() != null ? sst.getSstDate().toString() : "null"));
			}

			saveSst(sst);
		}
		return entityList;
	}

	/**
	 *
	 * @param id
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteLocalPlaceTemperatureSst(Long id) {
		LocalPlaceTemperatureSst entity = localPlaceTemperatureSstRepository.findOne(id);
		if (entity == null) {
			throw new PersistenceException(String.format("LocalPlaceTemperatureSst (id=%d) is not found", id));
		}
		localPlaceTemperatureSstRepository.save(softDelete(entity));
	}

	/**
	 *
	 * @param localPlaceId
	 * @param id
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteLocalPlaceTemperatureSst(Long localPlaceId, Long id) {
		checkNotNull(localPlaceId);
		checkNotNull(id);
		LocalPlaceTemperatureSst entity = localPlaceTemperatureSstRepository.findOne(id);
		if (entity == null) {
			throw new PersistenceException(String.format("LocalPlaceTemperatureSst (id=%d) is not found", id));
		}

		if (!entity.getLocalPlaceId().equals(localPlaceId)) {
			throw new PersistenceException(String
					.format("LocalPlaceTemperatureSst (id=%d) property localPlaceId=%d is invalid", id, localPlaceId));
		}

		localPlaceTemperatureSstRepository.save(softDelete(entity));
	}

	/**
	 *
	 * @param localPlaceId
	 * @param sstDate
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void initMonth(Long localPlaceId, LocalDate sstDate) {
		List<LocalPlaceTemperatureSst> check = selectSstByLocalPlace(localPlaceId, sstDate);

		if (check.isEmpty()) {
			initMonthNoCheck(localPlaceId, sstDate);
		}
	}

	/**
	 *
	 * @param localPlaceId
	 * @param sstDate
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void initMonthNoCheck(Long localPlaceId, LocalDate sstDate) {
		LocalPlace localPlace = localPlaceService.findLocalPlace(localPlaceId);

		if (localPlace == null) {
		    logger.warn("LocalPlace with ID: {} is not found", localPlaceId);
		    return;
        }

		LocalDate beginDate = sstDate.withDayOfMonth(1);
		LocalDate endDate = beginDate.plusMonths(1);
		LocalDate date = beginDate;

		List<LocalPlaceTemperatureSst> newSsts = new ArrayList<>();
		while (date.isBefore(endDate)) {
			LocalPlaceTemperatureSst newSst = new LocalPlaceTemperatureSst();
			newSst.setLocalPlace(localPlace);
			newSst.setSstDate(date.toDate());

			newSsts.add(newSst);

			date = date.plusDays(1);
		}

		localPlaceTemperatureSstRepository.save(newSsts);
	}

	/**
	 *
	 * @param localPlaceId
	 * @param sstDate
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void initSstCalc(Long localPlaceId, LocalDate sstDate) {

		List<LocalPlaceTemperatureSst> monthSstList = selectSstByLocalPlace(localPlaceId, sstDate);

		initSstCalc(monthSstList);

	}

	/**
	 *
	 * @param monthSstList
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<LocalPlaceTemperatureSst> initSstCalc(List<LocalPlaceTemperatureSst> monthSstList) {

		checkNotNull(monthSstList);

		if (monthSstList.isEmpty()) {
			return monthSstList;
		}

		LocalPlace localPlace = localPlaceService.findLocalPlace(monthSstList.get(0).getLocalPlaceId());

		if (localPlace.getWeatherPlaceId() == null) {
			return monthSstList;
		}

		for (LocalPlaceTemperatureSst sst : monthSstList) {

			if (!localPlace.getId().equals(sst.getLocalPlaceId())) {
				throw new IllegalStateException("LocalPlaceTemperatureSst is not consistence");
			}

			List<WeatherForecastCalc> forecastCalcList = weatherForecastService
					.selectWeatherForecastCalc(localPlace.getWeatherPlaceId(), sst.getSstDate());

			if (forecastCalcList.size() == 1) {
				WeatherForecastCalc forecastCalc = forecastCalcList.get(0);
				if (forecastCalc.getCalcValue() != null && !forecastCalc.getCalcValue().equals(sst.getSstCalcValue())) {
					sst.setSstCalcValue(forecastCalc.getCalcValue());

					DateTime calcDateTime = forecastCalc.getLastModifiedDate() != null
							? forecastCalc.getLastModifiedDate() : forecastCalc.getCreatedDate();

					if (calcDateTime != null) {
						sst.setSstCalcDateTime(calcDateTime.toDate());
					}
				}
			}

		}

		return Lists.newArrayList(localPlaceTemperatureSstRepository.save(monthSstList));

	}

}
