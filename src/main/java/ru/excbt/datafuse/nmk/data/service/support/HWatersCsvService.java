package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWaterImport;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterAbs_Csv;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterImport_CsvFormat;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWater_CsvFormat;

/**
 * Класс для работы с экспортом данных данных по воде
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.07.2015
 *
 */
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
	public byte[] writeDataHWaterToCsv(List<ContServiceDataHWater> contServiceDataHWaterList)
			throws JsonProcessingException {
		checkNotNull(contServiceDataHWaterList);

		CsvMapper mapper = new CsvMapper();

		mapper.addMixIn(ContServiceDataHWater.class, ContServiceDataHWater_CsvFormat.class);

		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWater.class).withHeader();

		byte[] byteArray = mapper.writer(schema).writeValueAsBytes(contServiceDataHWaterList);

		return byteArray;
	}

	/**
	 * 
	 * @param contServiceDataHWaterList
	 * @return
	 * @throws JsonProcessingException
	 */
	public byte[] writeDataHWaterToCsvAbs(List<ContServiceDataHWaterAbs_Csv> contServiceDataHWaterList)
			throws JsonProcessingException {
		checkNotNull(contServiceDataHWaterList);

		CsvMapper mapper = new CsvMapper();

		mapper.addMixIn(ContServiceDataHWaterAbs_Csv.class, ContServiceDataHWater_CsvFormat.class);

		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());

		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterAbs_Csv.class).withHeader();

		byte[] byteArray = mapper.writer(schema).writeValueAsBytes(contServiceDataHWaterList);

		return byteArray;
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public List<ContServiceDataHWater> parseDataHWaterCsv(InputStream inputStream)
			throws JsonProcessingException, IOException {

		CsvMapper mapper = new CsvMapper();
		mapper.addMixIn(ContServiceDataHWater.class, ContServiceDataHWater_CsvFormat.class);
		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());
		CsvSchema schema = mapper.schemaFor(ContServiceDataHWater.class).withHeader();
		ObjectReader reader = mapper.readerFor(ContServiceDataHWater.class).with(schema);

		MappingIterator<ContServiceDataHWater> iterator = null;
		List<ContServiceDataHWater> parsedData = new ArrayList<>();

		iterator = reader.readValues(inputStream);
		while (iterator.hasNext()) {
			ContServiceDataHWater d = iterator.next();
			parsedData.add(d);
		}
		return parsedData;
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public List<ContServiceDataHWaterImport> parseDataHWaterImportCsv(InputStream inputStream)
			throws JsonProcessingException, IOException {

		CsvMapper mapper = new CsvMapper();
		mapper.addMixIn(ContServiceDataHWaterImport.class, ContServiceDataHWaterImport_CsvFormat.class);
		mapper.setTimeZone(timeZoneService.getDefaultTimeZone());
		CsvSchema schema = mapper.schemaFor(ContServiceDataHWaterImport.class).withHeader();
		ObjectReader reader = mapper.readerFor(ContServiceDataHWaterImport.class).with(schema);

		MappingIterator<ContServiceDataHWaterImport> iterator = null;
		List<ContServiceDataHWaterImport> parsedData = new ArrayList<>();

		iterator = reader.readValues(inputStream);
		while (iterator.hasNext()) {
			ContServiceDataHWaterImport d = iterator.next();
			parsedData.add(d);
		}
		return parsedData;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean checkCsvSeparators(String file) throws FileNotFoundException, IOException {
		boolean result = true;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String header = br.readLine();
			String[] headerNames = header.split(",");
			int checkCnt = headerNames.length;
			String line;
			while (result && (line = br.readLine()) != null) {
				String[] lineValues = line.split(",");
				result = result & (lineValues.length == checkCnt);
			}
		}
		return result;
	}

}
