package ru.excbt.datafuse.nmk.data.service;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;

/**
 * Класс для работы с часовыми поясами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.07.2015
 *
 */
@Service
public class TimeZoneService {

	private static volatile String defaultTimezoneCanonicalId = null;


	private final TimezoneDefService timezoneDefService;

    @Autowired
    public TimeZoneService(TimezoneDefService timezoneDefService) {
        this.timezoneDefService = timezoneDefService;
    }

    /**
	 *
	 * @return
	 */
	public TimeZone getDefaultTimeZone() {
		String result = defaultTimezoneCanonicalId;
		if (result != null) {
			return TimeZone.getTimeZone(result);
		}

		synchronized (TimeZoneService.class) {
			result = defaultTimezoneCanonicalId;

			if (result == null) {
				TimezoneDef defaultTimezoneDef = timezoneDefService.getDefaultTimezoneDef();
				defaultTimezoneCanonicalId = result = defaultTimezoneDef.getCononicalId();
			}
		}

		return TimeZone.getTimeZone(result);

	}
}
