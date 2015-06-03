package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.constant.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterCsvFormat;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterCvs;
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
		List<ContServiceDataHWater> srcDataList = contServiceDataHWaterService
				.selectByContZPoint(18811586, TimeDetailKey.TYPE_24H, DateTime
						.now().withMillisOfDay(0).minusMonths(1), DateTime
						.now().withMillisOfDay(0));

		checkState(!srcDataList.isEmpty());
		logger.info("Found {} records", srcDataList.size());

		List<ContServiceDataHWaterCvs> cvsDataList = new ArrayList<>();
		for (ContServiceDataHWater data : srcDataList) {
			ContServiceDataHWaterCvs cvsData = ContServiceDataHWaterCvs
					.newInstance(data);

			ContServiceDataHWater abs = contServiceDataHWaterService
					.selectLastAbsData(
							data.getContZPointId(),
							new LocalDateTime(data.getDataDate()));
			cvsData.copyAbsData(abs);
			cvsDataList.add(cvsData);

		}

		CsvMapper mapper = new CsvMapper();

		mapper.addMixInAnnotations(ContServiceDataHWaterCvs.class,
				ContServiceDataHWaterCsvFormat.class);

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterCvs.class)
				.withHeader();

		String csv = mapper.writer(schema).writeValueAsString(cvsDataList);
		System.out.println();
		System.out.println("CSV");
		System.out.println();
		System.out.println(csv);

	}
}
