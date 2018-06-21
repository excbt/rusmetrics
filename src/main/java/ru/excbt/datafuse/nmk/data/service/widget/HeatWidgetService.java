/**
 *
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.widget.HeatWidgetTemperatureDto;
import ru.excbt.datafuse.nmk.service.QueryDSLService;
import ru.excbt.datafuse.nmk.utils.DateInterval;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 *
 */
@Service
public class HeatWidgetService extends WidgetService {

	private static final Logger log = LoggerFactory.getLogger(HeatWidgetService.class);

	private final static MODES[] availableModes = { MODES.DAY, MODES.TODAY, MODES.YESTERDAY, MODES.WEEK, MODES.MONTH };

	private final static Collection<MODES> availableModesCollection = Collections
			.unmodifiableList(Arrays.asList(availableModes));

	private final QueryDSLService queryDSLService;

	@Autowired
    public HeatWidgetService(QueryDSLService queryDSLService) {
        this.queryDSLService = queryDSLService;
    }


    private final static class StoredProcResultPaths {
        private static final NumberPath<Long> contZPointId = Expressions.numberPath(Long.class, "cont_zpoint_id");
        private static final StringPath timeDetailType = Expressions.stringPath("time_detail_type");
        private static final DateTimePath<Instant> bDate = Expressions.dateTimePath(Instant.class, "b_date");
        private static final DateTimePath<Instant> eDate = Expressions.dateTimePath(Instant.class, "e_date");
        private static final DateTimePath<Instant> dataDate = Expressions.dateTimePath(Instant.class, "data_date");
        private static final NumberPath<Double> tIn = Expressions.numberPath(Double.class, "t_in");
        private static final NumberPath<Double> tOut = Expressions.numberPath(Double.class, "t_out");
        private static final NumberPath<Double> chartT_In = Expressions.numberPath(Double.class, "chart_t_in");
        private static final NumberPath<Double> chartT_Out = Expressions.numberPath(Double.class, "chart_t_out");
        private static final NumberPath<Double> tAmbience = Expressions.numberPath(Double.class, "t_ambience");
    }

    private final static Expression<?>[] storedProcColumns = Arrays.asList(
        StoredProcResultPaths.contZPointId,
        StoredProcResultPaths.timeDetailType,
        StoredProcResultPaths.bDate,
        StoredProcResultPaths.eDate,
        StoredProcResultPaths.dataDate,
        StoredProcResultPaths.tIn,
        StoredProcResultPaths.tOut,
        StoredProcResultPaths.chartT_In,
        StoredProcResultPaths.chartT_Out,
        StoredProcResultPaths.tAmbience
    ).toArray(new Expression<?>[0]);

    /**
     *
     * @param tuple
     * @return
     */
    private HeatWidgetTemperatureDto tupleToTemperatureDTO (Tuple tuple) {
        HeatWidgetTemperatureDto t = new HeatWidgetTemperatureDto();
        t.setDataDate(Date.from(tuple.get(StoredProcResultPaths.dataDate)));
        t.setTimeDetailType(tuple.get(StoredProcResultPaths.timeDetailType));
        t.setT_in(BigDecimal.valueOf(tuple.get(StoredProcResultPaths.tIn)));
        t.setT_out(BigDecimal.valueOf(tuple.get(StoredProcResultPaths.tOut)));
        t.setChartT_in(BigDecimal.valueOf(tuple.get(StoredProcResultPaths.chartT_In)));
        t.setChartT_out(BigDecimal.valueOf(tuple.get(StoredProcResultPaths.chartT_Out)));
        t.setT_ambiance(BigDecimal.valueOf(tuple.get(StoredProcResultPaths.tAmbience)));
        return t;
    }

    /**
	 *
	 * @param contZpointId
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<HeatWidgetTemperatureDto> selectChartData2(Long contZpointId, java.time.ZonedDateTime dateTime,
			String mode) {

		log.debug("widgets.get_heat_data({},{},{})", contZpointId, dateTime, mode);

        StringTemplate widgetFunction = Expressions.stringTemplate("widgets.get_heat_data({0}, {1}, {2})",
                Expressions.asNumber(contZpointId),
                Expressions.asDateTime(dateTime),
                Expressions.asString(mode));

        List<HeatWidgetTemperatureDto> result = queryDSLService.doReturningWork((c) -> {
            SQLQuery<Tuple> query = new SQLQuery<>(c, QueryDSLService.templates)
                .select(storedProcColumns)
                .from(widgetFunction);
            log.debug("QuerySQL: {}", query.toString());
            List<Tuple> resultTupleList = query.fetch();
            return resultTupleList.stream().map(this::tupleToTemperatureDTO).collect(Collectors.toList());
        });

        return result;

	}

	/**
	 *
	 * @param contZpointId
	 * @param dateTime
	 * @param mode
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<HeatWidgetTemperatureDto> selectChartData(Long contZpointId, java.time.ZonedDateTime dateTime,
			String mode) {

		checkArgument(contZpointId != null && contZpointId > 0);

		DateInterval dateInterval = calculateModeDateInterval(dateTime, mode);

		if (dateInterval == null) {
			throw new UnsupportedOperationException();
		}
		TimeDetailKey timeDetail = getDetailTypeKey(mode);
		log.debug("from {} to {}", dateInterval.getFromDateStr(), dateInterval.getToDateStr());
		log.debug("timeDetail {}", timeDetail.getKeyname());
        log.debug("widgets.get_heat_data_ex({},{},{})", contZpointId, dateTime, mode);

        StringTemplate widgetFunction = Expressions.stringTemplate("widgets.get_heat_data_ex({0}, {1}, {2}, {3})",
            Expressions.asNumber(contZpointId),
            Expressions.asString(timeDetail.getKeyname()),
            Expressions.asDateTime(dateTime),
            Expressions.asDateTime(dateTime)
            );

        List<HeatWidgetTemperatureDto> result = queryDSLService.doReturningWork((c) -> {
            SQLQuery<Tuple> query = new SQLQuery<>(c, QueryDSLService.templates)
                .select(storedProcColumns)
                .from(widgetFunction);
            log.debug("QuerySQL: {}", query.toString());
            List<Tuple> resultTupleList = query.fetch();
            return resultTupleList.stream().map(this::tupleToTemperatureDTO).collect(Collectors.toList());
        });
		return result;
	}

    /**
     *
     * @return
     */
	@Override
	public Collection<MODES> selectAvailableModes() {
		return availableModesCollection;
	}

}
