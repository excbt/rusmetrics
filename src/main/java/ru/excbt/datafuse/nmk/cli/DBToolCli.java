package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.utils.JsonCleaner;
import ru.excbt.datafuse.raw.data.model.DeviceObjectDataJson;
import ru.excbt.datafuse.raw.data.service.DeviceObjectDataJsonService;

public class DBToolCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(DBToolCli.class);

	private static final PageRequest PAGE_LIMIT_1 = new PageRequest(0, 1);
	private static final long DEVICE_OBJECT_ID = 28071562;

	//@Autowired
	//private DeviceObjectDataJsonRepository deviceObjectDataJsonRepository;

	@Autowired
	private DeviceObjectDataJsonService deviceObjectDataJsonService;

	@Autowired
	private ReportService dbService;

	
	/**
	 * 
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		DBToolCli app = new DBToolCli();
		app.autowireBeans();
		app.showAppStatus();

		app.readJson();
		app.dbService.testConnection();
	}

	/**
	 * 
	 */
	private void readJson() {
		checkNotNull(deviceObjectDataJsonService);
		List<DeviceObjectDataJson> dataJsonList = deviceObjectDataJsonService.selectDeviceObjectDataJson
				(DEVICE_OBJECT_ID, TimeDetailKey.TYPE_24H, PAGE_LIMIT_1);

		logger.info("Found Data: {}", dataJsonList.size());

		if (dataJsonList.size() == 1) {

			String inJson = dataJsonList.get(0).getDataJson();

			System.out.println("Data Json: " + inJson);
			try {
				String resultJson = JsonCleaner.cleanJsonStringValue(inJson, "null");
				System.out.println("Data Result Json: " + resultJson);

			} catch (IOException e) {
				// XXX Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	


	
	
}
