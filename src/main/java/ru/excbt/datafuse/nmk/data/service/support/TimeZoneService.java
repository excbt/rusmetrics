package ru.excbt.datafuse.nmk.data.service.support;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.service.TimezoneDefService;

@Service
public class TimeZoneService {

	private static volatile String defaultTimezoneCanonicalId = null;

	@Autowired
	private TimezoneDefService timezoneDefService;

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
				TimezoneDef defaultTimezoneDef = timezoneDefService
						.getDefaultTimezoneDef();
				defaultTimezoneCanonicalId = result = defaultTimezoneDef
						.getCononicalId();
			}
		}

		return TimeZone.getTimeZone(result);

	}
}
