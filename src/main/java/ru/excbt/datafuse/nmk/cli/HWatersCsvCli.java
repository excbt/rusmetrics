package ru.excbt.datafuse.nmk.cli;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWater_CsvFormat;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class HWatersCsvCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(HWatersCsvCli.class);

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	/**
	 * 
	 * @param args
	 * @throws JsonProcessingException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void main(String[] args) throws JsonProcessingException,
			IllegalAccessException, InvocationTargetException {
		HWatersCsvCli app = new HWatersCsvCli();
		app.autowireBeans();
		app.showAppStatus();
		app.readData();
	}

	private void readData() throws JsonProcessingException,
			IllegalAccessException, InvocationTargetException {

		List<ContServiceDataHWaterAbs_Csv> cvsDataList = contServiceDataHWaterService
				.selectDataAbs_Csv(18811586, TimeDetailKey.TYPE_24H, DateTime.now()
						.withMillisOfDay(0).minusMonths(1), DateTime.now()
						.withMillisOfDay(0));

		CsvMapper mapper = new CsvMapper();

		mapper.addMixInAnnotations(ContServiceDataHWaterAbs_Csv.class,
				ContServiceDataHWater_CsvFormat.class);

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterAbs_Csv.class)
				.withHeader();

		String csv = mapper.writer(schema).writeValueAsString(cvsDataList);
		System.out.println();
		System.out.println("CSV");
		System.out.println();
		System.out.println(csv);

	}
}
