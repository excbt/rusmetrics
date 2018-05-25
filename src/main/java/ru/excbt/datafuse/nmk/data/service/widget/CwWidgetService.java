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


import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.utils.DateInterval;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.01.2017
 *
 */
@Service
public class CwWidgetService extends WidgetService {

	private static final Logger log = LoggerFactory.getLogger(CwWidgetService.class);

	private final static MODES[] availableModes = { MODES.DAY, MODES.TODAY, MODES.YESTERDAY, MODES.WEEK, MODES.MONTH };

	private final static Collection<MODES> availableModesCollection = Collections
			.unmodifiableList(Arrays.asList(availableModes));

	@Inject
	private ContServiceDataHWaterService contServiceDataHWaterService;

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
	public List<ContServiceDataHWater> selectChartData(Long contZpointId, java.time.ZonedDateTime dateTime,
			String mode) {

		checkArgument(contZpointId != null && contZpointId > 0);

		DateInterval dateInterval = calculateModeDateInterval(dateTime, mode);

		if (dateInterval == null) {
			throw new UnsupportedOperationException();
		}

		TimeDetailKey timeDetail = getDetailTypeKey(mode);

		log.debug("from: {} to :{}", dateInterval.getFromDate(), dateInterval.getToDate());
		log.debug("timeDetail: {}", timeDetail.getKeyname());

		List<ContServiceDataHWater> result = contServiceDataHWaterService.selectByContZPoint(contZpointId, timeDetail,
				dateInterval.getFromDate(), dateInterval.getToDate());

		return ObjectFilters.deletedFilter(result);
	}

}
