/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.01.2017
 * 
 */
@Service
public class CwWidgetService extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(CwWidgetService.class);

	private final static String[] availableModes = { "WEEK" };

	private final static Collection<String> availableModesCollection = Collections
			.unmodifiableList(Arrays.asList(availableModes));

	@Inject
	private ContServiceDataHWaterService contServiceDataHWaterService;

	/**
	 * 
	 * @param contZpointId
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataHWater> selectChartData(Long contZpointId, java.time.ZonedDateTime dateTime,
			String mode) {

		checkArgument(contZpointId != null && contZpointId > 0);
		checkArgument(availableModesCollection.contains(mode));

		Pair<LocalDateTime, LocalDateTime> datePairs = WidgetServiceUtils.calculateDatePairs(dateTime, mode);

		if (datePairs == null) {
			throw new UnsupportedOperationException();
		}

		TimeDetailKey timeDetail = "WEEK".equals(mode) ? TimeDetailKey.TYPE_24H : TimeDetailKey.TYPE_1H;

		log.debug("from: {} to :{}", LocalDateUtils.asDate(datePairs.getLeft()),
				LocalDateUtils.asDate(datePairs.getRight()));
		log.debug("timeDetail: {}", timeDetail.getKeyname());

		List<ContServiceDataHWater> result = contServiceDataHWaterService.selectByContZPoint(contZpointId, timeDetail,
				LocalDateUtils.asDate(datePairs.getLeft()), LocalDateUtils.asDate(datePairs.getRight()));

		return ObjectFilters.deletedFilter(result);
	}

}
