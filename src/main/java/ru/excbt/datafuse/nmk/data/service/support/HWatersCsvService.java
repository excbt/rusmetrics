package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWater_CsvFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Service
public class HWatersCsvService {

	public static final MediaType MEDIA_TYPE_CSV = MediaType.valueOf("text/csv");
	
	@Autowired
	public TimeZoneService timeZoneService;
	
	/**
	 * 
	 * @param contServiceDataHWaterList
	 * @return
	 * @throws JsonProcessingException
	 */
	public byte[] writeHWaterDataToCsv(
			List<ContServiceDataHWater> contServiceDataHWaterList)
			throws JsonProcessingException {
		checkNotNull(contServiceDataHWaterList);

		CsvMapper mapper = new CsvMapper();

		mapper.addMixInAnnotations(ContServiceDataHWater.class,
				ContServiceDataHWater_CsvFormat.class);

		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWater.class)
				.withHeader();

		byte[] byteArray = mapper.writer(schema).writeValueAsBytes(
				contServiceDataHWaterList);

		return byteArray;
	}

	/**
	 * 
	 * @param contServiceDataHWaterList
	 * @return
	 * @throws JsonProcessingException
	 */
	public byte[] writeHWaterDataToCsvAbs(
			List<ContServiceDataHWaterAbs_Csv> contServiceDataHWaterList)
					throws JsonProcessingException {
		checkNotNull(contServiceDataHWaterList);
		
		CsvMapper mapper = new CsvMapper();
		
		mapper.addMixInAnnotations(ContServiceDataHWaterAbs_Csv.class,
				ContServiceDataHWater_CsvFormat.class);
		
		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());
		
		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterAbs_Csv.class)
				.withHeader();
		
		byte[] byteArray = mapper.writer(schema).writeValueAsBytes(
				contServiceDataHWaterList);
		
		return byteArray;
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public List<ContServiceDataHWater> parseHWaterDataCsv(
			InputStream inputStream) throws JsonProcessingException,
			IOException {

		CsvMapper mapper = new CsvMapper();
		mapper.addMixInAnnotations(ContServiceDataHWater.class,
				ContServiceDataHWater_CsvFormat.class);
		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());
		CsvSchema schema = mapper.schemaFor(ContServiceDataHWater.class)
				.withHeader();
		ObjectReader reader = mapper.reader(ContServiceDataHWater.class).with(
				schema);

		MappingIterator<ContServiceDataHWater> iterator = null;
		List<ContServiceDataHWater> parsedData = new ArrayList<>();

		iterator = reader.readValues(inputStream);
		while (iterator.hasNext()) {
			ContServiceDataHWater d = iterator.next();
			parsedData.add(d);
		}
		return parsedData;
	}	
	
}
