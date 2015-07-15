package ru.excbt.datafuse.nmk.cli;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWater_CsvFormat;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.support.TimeZoneService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class HWatersCsvCli extends AbstractDBToolCli {

	private final static long HW_CONT_ZPOINT_ID = 18811586;

	private static final Logger logger = LoggerFactory
			.getLogger(HWatersCsvCli.class);

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Autowired
	private TimeZoneService timeZoneService;

	/**
	 * 
	 * @param args
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IllegalAccessException,
			InvocationTargetException, IOException {
		HWatersCsvCli app = new HWatersCsvCli();
		app.autowireBeans();
		app.showAppStatus();
		String csv = app.makeCsvData();
		app.readCsv(csv);
	}

	/**
	 * 
	 * @throws JsonProcessingException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void makeDataAbs() throws JsonProcessingException,
			IllegalAccessException, InvocationTargetException {

		List<ContServiceDataHWaterAbs_Csv> cvsDataList = contServiceDataHWaterService
				.selectDataAbs_Csv(18811586, TimeDetailKey.TYPE_24H, DateTime
						.now().withMillisOfDay(0).minusMonths(1), DateTime
						.now().withMillisOfDay(0));

		CsvMapper mapper = new CsvMapper();

		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

		mapper.addMixInAnnotations(ContServiceDataHWaterAbs_Csv.class,
				ContServiceDataHWater_CsvFormat.class);

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterAbs_Csv.class)
				.withHeader();

		String csv = mapper.writer(schema).writeValueAsString(cvsDataList);
		printCsv(csv);
	}

	/**
	 * 
	 * @param arg
	 */
	private void printCsv(String arg) {
		System.out.println();
		System.out.println("CSV");
		System.out.println();
		System.out.println(arg);
	}

	/**
	 * @throws JsonProcessingException
	 * 
	 */
	private String makeCsvData() throws JsonProcessingException {
		LocalDatePeriod dp = LocalDatePeriod.lastWeek();
		List<ContServiceDataHWater> dataHWater = contServiceDataHWaterService
				.selectByContZPoint(HW_CONT_ZPOINT_ID, TimeDetailKey.TYPE_24H,
						dp.getDateTimeFrom(), dp.getDateTimeTo());
		logger.info("Date Period {} ", dp.toString());
		logger.info("Date Period from {} to {} ", dp.getDateFrom(),
				dp.getDateTo());
		logger.info("Found {} records", dataHWater.size());

		ContServiceDataHWater row = dataHWater.get(0);

		logger.info("row dataDate:{}", row.getDataDate());

		CsvMapper mapper = new CsvMapper();

		mapper.addMixInAnnotations(ContServiceDataHWater.class,
				ContServiceDataHWater_CsvFormat.class);

		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWater.class)
				.withHeader();

		String csv = mapper.writer(schema).writeValueAsString(dataHWater);
		printCsv(csv);

		return csv;

	}

	/**
	 * 
	 * @param csv
	 * @throws IOException
	 */
	public void readCsv(String csv) throws IOException {
		CsvMapper mapper = new CsvMapper();

		mapper.addMixInAnnotations(ContServiceDataHWater.class,
				ContServiceDataHWater_CsvFormat.class);

		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWater.class)
				.withHeader();

		ObjectReader reader = mapper.reader(ContServiceDataHWater.class).with(
				schema);

		MappingIterator<ContServiceDataHWater> iterator = reader
				.readValues(csv);

		while (iterator.hasNext()) {
			ContServiceDataHWater d = iterator.next();
			logger.info("readed dataDate:{}; timeDetail:{}", d.getDataDate(),
					d.getTimeDetailType());

		}
	}

}
