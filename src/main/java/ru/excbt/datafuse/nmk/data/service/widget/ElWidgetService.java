/**
 *
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataElService;
import ru.excbt.datafuse.nmk.utils.DateInterval;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.01.2017
 *
 */
@Service
public class ElWidgetService extends WidgetService {

	private static final Logger log = LoggerFactory.getLogger(ElWidgetService.class);

	private final static MODES[] availableModes = { MODES.DAY, MODES.TODAY, MODES.YESTERDAY, MODES.WEEK, MODES.MONTH,
			MODES.YEAR };

	private final static Collection<MODES> availableModesCollection = Collections
			.unmodifiableList(Arrays.asList(availableModes));

	@Inject
	private ContServiceDataElService contServiceDataElService;

	/**
	 *
	 * @return
	 */
	@Override
	public Collection<MODES> getAvailableModes() {
		return availableModesCollection;
	}

	/**
	 *
	 * @param contZpointId
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContServiceDataElCons> selectChartData(Long contZpointId, java.time.ZonedDateTime dateTime,
			String mode) {

		checkArgument(contZpointId != null && contZpointId > 0);

		DateInterval dateInterval = calculateModeDateInterval(dateTime, mode);

		if (dateInterval == null) {
			throw new UnsupportedOperationException();
		}

		TimeDetailKey timeDetail = getDetailTypeKey(mode);

		log.debug("from {} to {}", dateInterval.getFromDateStr(), dateInterval.getToDateStr());
		log.debug("timeDetail {}", timeDetail.getKeyname());

		List<ContServiceDataElCons> result = contServiceDataElService.selectConsByContZPoint(contZpointId, timeDetail,
				dateInterval);

		return result;
	}

}
