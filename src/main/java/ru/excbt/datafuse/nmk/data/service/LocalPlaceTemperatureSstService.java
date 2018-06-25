package ru.excbt.datafuse.nmk.data.service;

import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.model.WeatherForecastCalc;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.repository.LocalPlaceRepository;
import ru.excbt.datafuse.nmk.data.repository.LocalPlaceTemperatureSstRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class LocalPlaceTemperatureSstService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceTemperatureSstService.class);

	private final LocalPlaceTemperatureSstRepository localPlaceTemperatureSstRepository;


    private final LocalPlaceRepository localPlaceRepository;


    private final WeatherForecastService weatherForecastService;

    public LocalPlaceTemperatureSstService(LocalPlaceTemperatureSstRepository localPlaceTemperatureSstRepository, LocalPlaceRepository localPlaceRepository, WeatherForecastService weatherForecastService) {
        this.localPlaceTemperatureSstRepository = localPlaceTemperatureSstRepository;
        this.localPlaceRepository = localPlaceRepository;
        this.weatherForecastService = weatherForecastService;
    }

    /**
	 *
	 * @param localPlaceId
	 * @return
	 */
	@Transactional
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
	@Transactional
	public LocalPlaceTemperatureSst saveSst(LocalPlaceTemperatureSst entity) {
		checkNotNull(entity);
		checkNotNull(entity.getLocalPlaceId());
		checkNotNull(entity.getSstDate());

//		LocalPlace localPlace = localPlaceService.findLocalPlace(entity.getLocalPlaceId());

        Optional<LocalPlace> localPlaceOpt = localPlaceRepository.findById(entity.getLocalPlaceId());

        if (!localPlaceOpt.isPresent()) {
            throw new EntityNotFoundException(LocalPlace.class, entity.getLocalPlaceId());
        }

		LocalPlaceTemperatureSst sst = null;
		if (!entity.isNew()) {
			sst = localPlaceTemperatureSstRepository.findById(entity.getId())
                .orElseThrow(() -> new EntityNotFoundException(LocalPlaceTemperatureSst.class, entity.getId()));
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
	@Transactional
	public List<LocalPlaceTemperatureSst> saveSstList(List<LocalPlaceTemperatureSst> entityList) {
		checkNotNull(entityList);

		Date checkDate = !entityList.isEmpty() ? entityList.get(0).getSstDate() : null;

		LocalDate checkMonth = checkDate != null ? (new LocalDate(checkDate)).withDayOfMonth(1) : null;

		for (LocalPlaceTemperatureSst sst : entityList) {

			LocalDate sstMonth = sst.getSstDate() != null ? (new LocalDate(sst.getSstDate())).withDayOfMonth(1) : null;

			if (sstMonth == null || checkMonth == null || !checkMonth.equals(sstMonth)) {
				throw new IllegalArgumentException(
						String.format("sstDate is not consistence. Required sstDate = %s, actual sstDate = %s ",
								sst.getSstDate() != null ? sst.getSstDate().toString() : "null", sstMonth != null ? sstMonth.toString() : "null"));
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
	@Transactional
	public void deleteLocalPlaceTemperatureSst(Long id) {
		LocalPlaceTemperatureSst entity = localPlaceTemperatureSstRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(LocalPlaceTemperatureSst.class, id));

		localPlaceTemperatureSstRepository.save(EntityActions.softDelete(entity));
	}

	/**
	 *
	 * @param localPlaceId
	 * @param id
	 */
	@Secured({ ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_ADMIN })
	@Transactional
	public void deleteLocalPlaceTemperatureSst(Long localPlaceId, Long id) {
		checkNotNull(localPlaceId);
		checkNotNull(id);
		LocalPlaceTemperatureSst entity = localPlaceTemperatureSstRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(LocalPlaceTemperatureSst.class, id));

		if (!entity.getLocalPlaceId().equals(localPlaceId)) {
			throw new PersistenceException(String
					.format("LocalPlaceTemperatureSst (id=%d) property localPlaceId=%d is invalid", id, localPlaceId));
		}

		localPlaceTemperatureSstRepository.save(EntityActions.softDelete(entity));
	}

	/**
	 *
	 * @param localPlaceId
	 * @param sstDate
	 */
	@Transactional
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
	@Transactional
	public void initMonthNoCheck(Long localPlaceId, LocalDate sstDate) {
//		LocalPlace localPlace = localPlaceService.findLocalPlace(localPlaceId);

		Optional<LocalPlace> localPlaceOpt = localPlaceRepository.findById(localPlaceId);

		if (!localPlaceOpt.isPresent()) {
		    logger.warn("LocalPlace with ID: {} is not found", localPlaceId);
		    return;
        }

		LocalDate beginDate = sstDate.withDayOfMonth(1);
		LocalDate endDate = beginDate.plusMonths(1);
		LocalDate date = beginDate;

		List<LocalPlaceTemperatureSst> newSsts = new ArrayList<>();
		while (date.isBefore(endDate)) {
			LocalPlaceTemperatureSst newSst = new LocalPlaceTemperatureSst();
			newSst.setLocalPlace(localPlaceOpt.get());
			newSst.setSstDate(date.toDate());

			newSsts.add(newSst);

			date = date.plusDays(1);
		}

		localPlaceTemperatureSstRepository.saveAll(newSsts);
	}

	/**
	 *
	 * @param localPlaceId
	 * @param sstDate
	 */
	@Transactional
	public void initSstCalc(Long localPlaceId, LocalDate sstDate) {

		List<LocalPlaceTemperatureSst> monthSstList = selectSstByLocalPlace(localPlaceId, sstDate);

		initSstCalc(monthSstList);

	}

	/**
	 *
	 * @param monthSstList
	 */
	@Transactional
	public List<LocalPlaceTemperatureSst> initSstCalc(List<LocalPlaceTemperatureSst> monthSstList) {

		checkNotNull(monthSstList);

		if (monthSstList.isEmpty()) {
			return monthSstList;
		}

		if (monthSstList.get(0).getLocalPlaceId() == null) {
		    return monthSstList;
        }

//		LocalPlace localPlace = localPlaceService.findLocalPlace(monthSstList.get(0).getLocalPlaceId());

		Optional<LocalPlace> localPlaceOpt = localPlaceRepository.findById(monthSstList.get(0).getLocalPlaceId());

		if (!localPlaceOpt.isPresent()) {
		    return monthSstList;
        }

		if (localPlaceOpt.get().getWeatherPlaceId() == null) {
			return monthSstList;
		}

		for (LocalPlaceTemperatureSst sst : monthSstList) {

			if (!localPlaceOpt.get().getId().equals(sst.getLocalPlaceId())) {
				throw new IllegalStateException("LocalPlaceTemperatureSst is not consistence");
			}

			List<WeatherForecastCalc> forecastCalcList = weatherForecastService
					.selectWeatherForecastCalc(localPlaceOpt.get().getWeatherPlaceId(), sst.getSstDate());

			if (forecastCalcList.size() == 1) {
				WeatherForecastCalc forecastCalc = forecastCalcList.get(0);
				if (forecastCalc.getCalcValue() != null && !forecastCalc.getCalcValue().equals(sst.getSstCalcValue())) {
					sst.setSstCalcValue(forecastCalc.getCalcValue());


					Date calcDateTime = forecastCalc.getLastModifiedDate() != null
							? Date.from(forecastCalc.getLastModifiedDate()) : Date.from(forecastCalc.getCreatedDate());

					if (calcDateTime != null) {
						sst.setSstCalcDateTime(calcDateTime);
					}
				}
			}

		}

		return Lists.newArrayList(localPlaceTemperatureSstRepository.saveAll(monthSstList));

	}

}
