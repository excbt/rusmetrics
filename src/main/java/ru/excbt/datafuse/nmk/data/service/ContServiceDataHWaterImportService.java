/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWaterImport;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ServiceDataImportInfo;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterImportRepository;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.slog.service.SLogWriterService;
import ru.excbt.datafuse.slogwriter.service.SLogSessionStatuses;
import ru.excbt.datafuse.slogwriter.service.SLogSessionT1;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.12.2016
 * 
 */
@Service
public class ContServiceDataHWaterImportService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterImportService.class);

	private final static String IMPORT_ERROR_TEMPLATE = "Ошибка импорта данных. Файл %s";
	private final static String IMPORT_COMPLETE_TEMPLATE = "Данные из файла %s успешно загружены";
	private final static String IMPORT_EXCEPTION_TEMPLATE = "Data Import Error. %s. File: %s";

	@Autowired
	private ContServiceDataHWaterImportRepository contServiceDataHWaterImportRepository;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private HWatersCsvService hWatersCsvService;

	@Autowired
	private SLogWriterService sLogWriterService;

	public class Task implements Callable<Boolean> {

		private final List<ServiceDataImportInfo> serviceDataImportInfos;

		/**
		 * 
		 */
		public Task(List<ServiceDataImportInfo> serviceDataImportInfos) {
			checkNotNull(serviceDataImportInfos);

			this.serviceDataImportInfos = new ArrayList<>(serviceDataImportInfos);
		}

		/* (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Boolean call() throws Exception {
			logger.debug("Start Task");
			if (serviceDataImportInfos.isEmpty()) {
				return Boolean.FALSE;
			}
			logger.debug("Import Data");
			try {
				importData(serviceDataImportInfos);
			} catch (Exception e) {
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}

	}

	/**
	 * 
	 * @param serviceDataImportInfos
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public void importData(final List<ServiceDataImportInfo> serviceDataImportInfos) {

		Date createdDate = new Date();

		for (ServiceDataImportInfo importInfo : serviceDataImportInfos) {

			SLogSessionT1 session = sLogWriterService.newSessionWebT1(importInfo.getDataSourceId(),
					importInfo.getDeviceObjectId(), String.format("Пользователь ID %d Загрузка файла %s ",
							importInfo.getAuthorId(), FilenameUtils.getName(importInfo.getInternalFileName())),
					importInfo.getAuthorId());

			session.status(SLogSessionStatuses.GENERATING.getKeyname(),
					"Загрузка файла: " + importInfo.getUserFileName());

			session.web().trace("Проверка файла на валидность");

			boolean checkCsvSeparators = false;
			try {
				checkCsvSeparators = hWatersCsvService.checkCsvSeparators(importInfo.getInternalFileName());
			} catch (FileNotFoundException e1) {
				failSession(session, importInfo, "Ошибка. Файл не может быть проверен");
				throw new IllegalArgumentException(String.format(IMPORT_EXCEPTION_TEMPLATE,
						"Check CSV separators error", importInfo.getUserFileName()));
			} catch (IOException e1) {
				failSession(session, importInfo, "Ошибка. Файл не может быть проверен");
				throw new IllegalArgumentException(String.format(IMPORT_EXCEPTION_TEMPLATE,
						"Check CSV separators error", importInfo.getUserFileName()));
			}

			if (!checkCsvSeparators) {
				failSession(session, importInfo, "Ошибка. Файл не содержит полных данных");
				throw new IllegalArgumentException(String.format(IMPORT_EXCEPTION_TEMPLATE,
						"Check CSV separators error", importInfo.getUserFileName()));
			}

			session.web().trace("Проверка файла на валидность пройдена");

			session.web().trace("Обработка данных файла");

			List<ContServiceDataHWaterImport> inDataHWaterImport;
			try (FileInputStream fio = new FileInputStream(importInfo.getInternalFileName())) {
				inDataHWaterImport = hWatersCsvService.parseDataHWaterImportCsv(fio);
			} catch (IOException e) {
				failSession(session, importInfo, "Ошибка. Данные из файла не могут быть обработаны");
				logger.error("Data Import. Exception: IOException. sessionUUID({}). Exception message: {}",
						session.getSessionUUID(), e.getMessage());
				throw new IllegalArgumentException(
						String.format(IMPORT_EXCEPTION_TEMPLATE, "Parsing error", importInfo.getUserFileName()));
			}

			session.web().trace("Данные считаны. Проверка данных на валидность");

			if (inDataHWaterImport.stream().map(i -> i.getTimeDetailType()).distinct().filter(s -> s == null)
					.count() > 0) {
				failSession(session, importInfo, "Ошибка. Не задано значение detail_type");
				throw new IllegalArgumentException(
						String.format(IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			List<String> timeDetailTypes = inDataHWaterImport.stream().map(i -> i.getTimeDetailType()).distinct()
					.collect(Collectors.toList());
			if (timeDetailTypes.size() > 1 || timeDetailTypes.get(0) == null) {
				failSession(session, importInfo, "Ошибка. В файле задано более 2-х типов detail_type");
				throw new IllegalArgumentException(
						String.format(IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			if (inDataHWaterImport.stream().map(i -> i.getDataDate()).distinct().filter(s -> s == null).count() > 0) {
				failSession(session, importInfo, "Ошибка. Найдено пустое значение date");
				throw new IllegalArgumentException(
						String.format(IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			TimeDetailKey timeDetailKey = TimeDetailKey.searchKeyname(timeDetailTypes.get(0));

			if (timeDetailKey == null) {
				failSession(session, importInfo, "Ошибка. Не найдено значение detail_type=" + timeDetailTypes.get(0));
				throw new IllegalArgumentException(
						String.format(IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			session.web().trace("Поиск точки учета");
			ContZPoint zpoint = contZPointService.findOne(importInfo.getContZPointId());

			if (zpoint == null) {
				failSession(session, importInfo, "Ошибка. Точка учета не найдена");
				throw new IllegalArgumentException(String.format(IMPORT_EXCEPTION_TEMPLATE, "ContZPoint is not found",
						importInfo.getUserFileName()));
			}

			session.web().trace(String.format("Найдена точка учета: %s. Номер ТС: %d", zpoint.getCustomServiceName(),
					zpoint.getTsNumber()));

			if (!BooleanUtils.isTrue(zpoint.getIsManualLoading())) {
				failSession(session, importInfo, "Ошибка. Точка учета не поддерживает импорт данных");
				throw new IllegalArgumentException(String.format(IMPORT_EXCEPTION_TEMPLATE,
						"ContZPoint is support import", importInfo.getUserFileName()));
			}

			inDataHWaterImport.forEach(i -> {
				i.setContZPointId(importInfo.getContZPointId());
				i.setDeviceObjectId(importInfo.getDeviceObjectId());
				i.setCreatedBy(importInfo.getAuthorId());
				i.setCreatedDate(createdDate);
				i.setTrxId(session.getSessionUUID());
			});

			Comparator<ContServiceDataHWaterImport> comparator = Comparator
					.comparing(ContServiceDataHWaterImport::getDataDate);

			ContServiceDataHWaterImport minDateData = inDataHWaterImport.stream().min(comparator).get();
			ContServiceDataHWaterImport maxDateData = inDataHWaterImport.stream().max(comparator).get();

			final SimpleDateFormat dateFormat = timeDetailKey.isTruncDate() ? new SimpleDateFormat("dd-MM-yyyy")
					: new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			session.web()
					.trace(String.format("Данные проверены. detail_type = %s, min(dataDate) = %s, max(dataDate) = %s",
							timeDetailKey.getKeyname(), dateFormat.format(minDateData.getDataDate()),
							dateFormat.format(maxDateData.getDataDate())));

			session.web().trace("Загрузка данных в БД");

			try {
				contServiceDataHWaterImportRepository.save(inDataHWaterImport);
			} catch (Exception e) {
				failSession(session, importInfo, "Ошибка. Загрузка в БД временно не возможна");
				logger.error("Data Import. Exception: {}. sessionUUID({}). Exception : {}",
						e.getClass().getSimpleName(), session.getSessionUUID(), e);
				throw new IllegalArgumentException(
						String.format(IMPORT_EXCEPTION_TEMPLATE, "DB save error", importInfo.getUserFileName()));
			}
			session.web().trace("Загрузка данных в БД успешно завершена");

			session.web().trace("Обновление данных запущено");

			// Call Stored proc
			try {
				logger.debug("processImport.Calling Stored proc portal.process_service_data_hwater_import");
				contServiceDataHWaterImportRepository.processImport(session.getSessionUUID().toString());
				logger.debug("processImport.Calling Stored proc portal.process_service_data_hwater_import SUCCESS");
			} catch (Exception e) {

				PSQLException pe = DBExceptionUtils.getPSQLException(e);
				String sqlExceptiomMessage = pe != null ? pe.getMessage() : e.getMessage();
				logger.error("Data Import. Exception: {}. sessionUUID({}). Exception : {}",
						e.getClass().getSimpleName(), session.getSessionUUID(), sqlExceptiomMessage);

				session.web().trace("Ошибка при обновлении данных");
				failSession(session, importInfo, sqlExceptiomMessage);

				throw new IllegalArgumentException(
						String.format(IMPORT_EXCEPTION_TEMPLATE, "DB save error", importInfo.getUserFileName()));
			}

			session.web().trace("Обновление данных успешно завершено");
			completeSession(session, importInfo);
		}
	}

	/**
	 * 
	 * @param session
	 * @param message
	 * @param importInfo
	 */
	private void failSession(SLogSessionT1 session, ServiceDataImportInfo importInfo, String message) {
		session.web().trace(message);
		session.status(SLogSessionStatuses.FAILURE.getKeyname(),
				String.format(IMPORT_ERROR_TEMPLATE, importInfo.getUserFileName()));
	}

	/**
	 * 
	 * @param session
	 * @param importInfo
	 */
	private void completeSession(SLogSessionT1 session, ServiceDataImportInfo importInfo) {
		session.status(SLogSessionStatuses.COMPLETE.getKeyname(),
				String.format(IMPORT_COMPLETE_TEMPLATE, importInfo.getUserFileName()));
	}

	/**
	 * 
	 * @param serviceDataImportInfos
	 * @return
	 */
	public Callable<Boolean> newTask(final List<ServiceDataImportInfo> serviceDataImportInfos) {
		return new Task(serviceDataImportInfos);
	}

}
