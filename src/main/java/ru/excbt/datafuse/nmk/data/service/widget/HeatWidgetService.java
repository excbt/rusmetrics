/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import static com.google.common.base.Preconditions.checkState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.widget.HeatWidgetTemperatureDto;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.ColumnHelper;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
@Service
public class HeatWidgetService extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(HeatWidgetService.class);

	/**
	 * 
	 * @param contZpointId
	 * @param date
	 * @param mode
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<HeatWidgetTemperatureDto> selectChartData(Long contZpointId, java.time.LocalDate date, String mode) {

		List<HeatWidgetTemperatureDto> result = new ArrayList<>();

		ColumnHelper columnHelper = new ColumnHelper("cont_zpoint_id", "time_detail_type", "b_date", "e_date",
				"data_date", "t_in", "t_out", "chart_t_in", "chart_t_out", "t_ambience");

		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ").append(columnHelper.build());
		sqlString.append(" FROM widgets.get_heat_data(:contZpointId, :currentDate, :mode)");
		log.debug("Sql: {}", sqlString.toString());

		Query q1 = em.createNativeQuery(sqlString.toString());

		q1.setParameter("contZpointId", contZpointId);
		q1.setParameter("currentDate", LocalDateUtils.asDate(date));
		q1.setParameter("mode", mode);

		List<?> results = q1.getResultList();
		for (Object row : results) {
			checkState(row instanceof Object[]);
			Object[] values = (Object[]) row;
			HeatWidgetTemperatureDto t = new HeatWidgetTemperatureDto();
			t.setDataDate(columnHelper.getResultAsClass(values, "data_date", Date.class));
			t.setTimeDetailType(columnHelper.getResultAsClass(values, "time_detail_type", String.class));
			t.setT_in(columnHelper.getResultAsClass(values, "t_in", BigDecimal.class));
			t.setT_out(columnHelper.getResultAsClass(values, "t_out", BigDecimal.class));
			t.setChartT_in(columnHelper.getResultAsClass(values, "chart_t_in", BigDecimal.class));
			t.setChartT_out(columnHelper.getResultAsClass(values, "chart_t_out", BigDecimal.class));
			t.setT_ambiance(columnHelper.getResultAsClass(values, "t_ambience", BigDecimal.class));
			result.add(t);
		}

		return result;
	}

}
