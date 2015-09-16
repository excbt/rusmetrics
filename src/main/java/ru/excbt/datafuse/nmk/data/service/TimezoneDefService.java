package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.repository.keyname.TimezoneDefRepository;

@Service
public class TimezoneDefService {

	@Autowired
	private TimezoneDefRepository timezoneDefRepository;

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
				
				List<TimezoneDef> vList = timezoneDefRepository
						.findByIsDefault(true);
				checkState(vList.size() == 1);
				result = vList.get(0);
				
				checkState(result.getCononicalId() != null,
						"TimezoneDef canonical ID is null");
				defaultTimezoneDef = result;
			}
		}

		return result;
	}

}
