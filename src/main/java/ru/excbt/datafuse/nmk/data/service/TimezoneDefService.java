package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.repository.keyname.TimezoneDefRepository;
import ru.excbt.datafuse.nmk.service.dto.TimezoneDefDTO;
import ru.excbt.datafuse.nmk.service.mapper.TimezoneDefMapper;

/**
 * Сервис для работы с временными зонами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.07.2015
 *
 */
@Service
public class TimezoneDefService {

	private final TimezoneDefRepository timezoneDefRepository;

	private final TimezoneDefMapper timezoneDefMapper;

    public TimezoneDefService(TimezoneDefRepository timezoneDefRepository, TimezoneDefMapper timezoneDefMapper) {
        this.timezoneDefRepository = timezoneDefRepository;
        this.timezoneDefMapper = timezoneDefMapper;
    }

    private static volatile TimezoneDef defaultTimezoneDef = null;

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public TimezoneDef getDefaultTimezoneDef() {

		TimezoneDef result = defaultTimezoneDef;

		if (result != null) {
			return result;
		}

		synchronized (TimezoneDefService.class) {
			result = defaultTimezoneDef;

			if (result == null) {

				List<TimezoneDef> vList = timezoneDefRepository.findByIsDefault(true);
				checkState(vList.size() == 1);
				result = vList.get(0);

				checkState(result.getCononicalId() != null, "TimezoneDef canonical ID is null");
				defaultTimezoneDef = result;
			}
		}

		return result;
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TimezoneDef> selectTimeZoneDefs() {
		List<TimezoneDef> result = timezoneDefRepository.selectAll();
		return ObjectFilters.deletedFilter(result);
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<TimezoneDefDTO> findTimeZoneDefs() {
		List<TimezoneDef> result = timezoneDefRepository.selectAll();
		return result.stream().filter(i -> i.getTimezoneName() != null && i.getDeleted() == 0)
            .map(timezoneDefMapper::toDTO).collect(Collectors.toList());
	}

	/**
	 *
	 * @param keyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public TimezoneDef findTimeZoneDef(String keyname) {
		if (keyname == null) {
			return null;
		}
		return timezoneDefRepository.findOne(keyname);
	}

}
