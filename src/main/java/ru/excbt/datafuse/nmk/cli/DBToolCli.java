package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataJson;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectDataJsonRepository;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.utils.JsonCleaner;

public class DBToolCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(DBToolCli.class);

	private static final PageRequest PAGE_LIMIT_1 = new PageRequest(0, 1);
	private static final long DEVICE_OBJECT_ID = 28071562;

	@Autowired
	private DeviceObjectDataJsonRepository deviceObjectDataJsonRepository;

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
		logger.info("Application Started");
		logger.info("Status: {}",
				app.entityManager != null ? "entityManager inititalized"
						: "entityManager NOT inititalized");

		app.readJson();
		app.dbService.testConnection();
	}

	/**
	 * 
	 */
	private void readJson() {
		checkNotNull(deviceObjectDataJsonRepository);
		List<DeviceObjectDataJson> dataJsonList = deviceObjectDataJsonRepository
				.selectByDeviceObject(DEVICE_OBJECT_ID, PAGE_LIMIT_1);

		logger.info("Found Data: {}", dataJsonList.size());

		if (dataJsonList.size() == 1) {

			String inJson = dataJsonList.get(0).getDataJson();

			System.out.println("Data Json: " + inJson);
			try {
				String resultJson = JsonCleaner.cleanJsonStringValue(inJson, "null");
				System.out.println("Data Result Json: " + resultJson);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	


	
	
}
