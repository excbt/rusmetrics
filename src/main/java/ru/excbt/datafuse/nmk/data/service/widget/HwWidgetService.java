/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
@Service
public class HwWidgetService extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(HwWidgetService.class);

	private final static String[] availableModes = { "TODAY", "YESTERDAY", "WEEK" };

	private final static Collection<String> availableModesCollection = Collections
			.unmodifiableList(Arrays.asList(availableModes));

	@Inject
	private ContServiceDataHWaterService contServiceDataHWaterService;

	/**
	 * 
	 * @return
	 */
	public Collection<String> getAvailableModes() {
		return availableModesCollection;
	}

	/**
	 * 
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	private Pair<LocalDateTime, LocalDateTime> calculateDatePairs(ZonedDateTime dateTime, String mode) {

		ZonedDateTime endOfDay = dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusSeconds(1);

		if ("TODAY".equals(mode)) {
			return Pair.of(dateTime.truncatedTo(ChronoUnit.DAYS).toLocalDateTime(), endOfDay.toLocalDateTime());
		}

		if ("YESTERDAY".equals(mode)) {
			return Pair.of(dateTime.minusDays(1).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
					endOfDay.minusDays(1).toLocalDateTime());
		}

		if ("WEEK".equals(mode)) {
			return Pair.of(dateTime.minusDays(7).truncatedTo(ChronoUnit.DAYS).toLocalDateTime(),
					endOfDay.toLocalDateTime());
		}

		throw new UnsupportedOperationException();
	}

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

		Pair<LocalDateTime, LocalDateTime> datePairs = calculateDatePairs(dateTime, mode);

		TimeDetailKey timeDetail = "WEEK".equals(mode) ? TimeDetailKey.TYPE_24H : TimeDetailKey.TYPE_1H;

		log.info("from: {} to :{}", LocalDateUtils.asDate(datePairs.getLeft()),
				LocalDateUtils.asDate(datePairs.getRight()));
		log.info("timeDetail: {}", timeDetail.getKeyname());

		List<ContServiceDataHWater> result = contServiceDataHWaterService.selectByContZPoint(contZpointId, timeDetail,
				LocalDateUtils.asDate(datePairs.getLeft()), LocalDateUtils.asDate(datePairs.getRight()));

		return result;

	}

}
