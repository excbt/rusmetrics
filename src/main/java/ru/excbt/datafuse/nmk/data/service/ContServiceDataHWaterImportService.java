/**
 *
 */
package ru.excbt.datafuse.nmk.data.service;

import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
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
import ru.excbt.datafuse.nmk.data.model.support.FileImportInfo;
import ru.excbt.datafuse.nmk.data.model.support.ServiceDataImportInfo;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterImportRepository;
import ru.excbt.datafuse.nmk.data.service.support.CsvUtils;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvService;
import ru.excbt.datafuse.nmk.data.service.support.SLogSessionUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.slog.service.SLogWriterService;
import ru.excbt.datafuse.slogwriter.service.SLogSessionStatuses;
import ru.excbt.datafuse.slogwriter.service.SLogSessionT1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.12.2016
 *
 */
@Service
public class ContServiceDataHWaterImportService implements SecuredRoles {

	private static final Logger log = LoggerFactory.getLogger(ContServiceDataHWaterImportService.class);

//	private final static String IMPORT_ERROR_TEMPLATE = "Ошибка импорта данных. Файл %s";
//	private final static String IMPORT_COMPLETE_TEMPLATE = "Данные из файла %s успешно загружены";
//	private final static String IMPORT_EXCEPTION_TEMPLATE = "Data Import Error. %s. File: %s";

	private final ContServiceDataHWaterImportRepository contServiceDataHWaterImportRepository;

	private final ContZPointService contZPointService;

	private final HWatersCsvService hWatersCsvService;


	private final SLogWriterService sLogWriterService;

    private final SubscriberExecutorService subscriberExecutorService;

    public ContServiceDataHWaterImportService(ContServiceDataHWaterImportRepository contServiceDataHWaterImportRepository,
                                              ContZPointService contZPointService,
                                              HWatersCsvService hWatersCsvService,
                                              SLogWriterService sLogWriterService,
                                              SubscriberExecutorService subscriberExecutorService) {
        this.contServiceDataHWaterImportRepository = contServiceDataHWaterImportRepository;
        this.contZPointService = contZPointService;
        this.hWatersCsvService = hWatersCsvService;
        this.sLogWriterService = sLogWriterService;
        this.subscriberExecutorService = subscriberExecutorService;
    }

    /**
	 *
	 * @param serviceDataImportInfos
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
	public void importData(final Long subscriberId, final List<ServiceDataImportInfo> serviceDataImportInfos) {

		Date createdDate = new Date();

		for (ServiceDataImportInfo importInfo : serviceDataImportInfos) {

		    String msg = String.format("Пользователь ID %d Загрузка файла %s ",
                subscriberId, FilenameUtils.getName(importInfo.getInternalFileName()));


		    String errorMessage = String.format(FileImportInfo.IMPORT_ERROR_TEMPLATE, importInfo.getUserFileName());
		    String completeMessage = String.format(FileImportInfo.IMPORT_COMPLETE_TEMPLATE, importInfo.getUserFileName());

			SLogSessionT1 session = sLogWriterService.newSessionWebT1(importInfo.getDataSourceId(),
					importInfo.getDeviceObjectId(), msg,
                subscriberId);

			session.status(SLogSessionStatuses.GENERATING.getKeyname(),
					"Загрузка файла: " + importInfo.getUserFileName());

			session.web().trace("Проверка целостности CSV файла");
			SLogSessionUtils.checkCsvSeparators(session, importInfo);
			session.web().trace("Проверка целостности CSV пройдена");
            session.web().trace("Преобразование данных файла");

			// Reading CSV from FILE
			List<ContServiceDataHWaterImport> inDataHWaterImport;
			try (FileInputStream fio = new FileInputStream(importInfo.getInternalFileName())) {
				inDataHWaterImport = hWatersCsvService.parseDataHWaterImportCsv(fio);
			} catch (Exception e) {
			    if (e instanceof RuntimeJsonMappingException) {
                    session.web().trace("Ошибка преобразования данных файла. Некорректные данные: " + e.getMessage());
                }

                SLogSessionUtils.failSession(session,
                    "Ошибка. Данные из файла не могут быть обработаны", errorMessage);

				log.error("Data Import. Exception: IOException. sessionUUID({}). Exception message: {}",
						session.getSessionUUID(), e.getMessage());
				throw new IllegalArgumentException(
						String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "Parsing error", importInfo.getUserFileName()));
			}

            session.web().trace("Преобразование данных файла завершено");

			session.web().trace("Данные преобразованы. Проверка данных на валидность");

			if (inDataHWaterImport.stream().map(i -> i.getTimeDetailType()).distinct().filter(s -> s == null)
					.count() > 0) {

                SLogSessionUtils.failSession(session,
                    "Ошибка. Не задано значение detail_type", errorMessage);

				throw new IllegalArgumentException(
						String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			List<String> timeDetailTypes = inDataHWaterImport.stream().map(i -> i.getTimeDetailType()).distinct()
					.collect(Collectors.toList());
			if (timeDetailTypes.size() > 1 || timeDetailTypes.get(0) == null) {

                SLogSessionUtils.failSession(session,
                    "Ошибка. В файле задано более 2-х типов detail_type", errorMessage);

				throw new IllegalArgumentException(
						String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			if (inDataHWaterImport.stream().map(i -> i.getDataDate()).distinct().filter(s -> s == null).count() > 0) {

                SLogSessionUtils.failSession(session,
                    "Ошибка. Найдено пустое значение date", errorMessage);

				throw new IllegalArgumentException(
						String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			TimeDetailKey timeDetailKey = TimeDetailKey.searchKeyname(timeDetailTypes.get(0));

			if (timeDetailKey == null) {

                SLogSessionUtils.failSession(session,
                    "Ошибка. Не найдено значение detail_type=" + timeDetailTypes.get(0), errorMessage);

				throw new IllegalArgumentException(
						String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "Validate error", importInfo.getUserFileName()));
			}

			session.web().trace("Поиск точки учета");
			ContZPoint zpoint = contZPointService.findOne(importInfo.getContZPointId());

			if (zpoint == null) {

                SLogSessionUtils.failSession(session,
                    "Ошибка. Точка учета не найдена", errorMessage);

				throw new IllegalArgumentException(String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "ContZPoint is not found",
						importInfo.getUserFileName()));
			}

			session.web().trace(String.format("Найдена точка учета: %s. Номер ТС: %d", zpoint.getCustomServiceName(),
					zpoint.getTsNumber()));

			if (!BooleanUtils.isTrue(zpoint.getIsManualLoading())) {

                SLogSessionUtils.failSession(session,
                    "Ошибка. Точка учета не поддерживает импорт данных", errorMessage);

                throw new IllegalArgumentException(String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE,
						"ContZPoint is support import", importInfo.getUserFileName()));
			}

			inDataHWaterImport.forEach(i -> {
				i.setContZPointId(importInfo.getContZPointId());
				i.setDeviceObjectId(importInfo.getDeviceObjectId());
				i.setCreatedBy(subscriberId);
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

                SLogSessionUtils.failSession(session,
                    "Ошибка. Загрузка в БД временно не возможна", errorMessage);

                log.error("Data Import. Exception: {}. sessionUUID({}). Exception : {}",
						e.getClass().getSimpleName(), session.getSessionUUID(), e);
				throw new IllegalArgumentException(
						String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "DB save error", importInfo.getUserFileName()));
			}
			session.web().trace("Загрузка данных в БД успешно завершена");

			session.web().trace("Обновление данных запущено");

			// Call Stored proc
			try {
				log.debug("processImport.Calling Stored proc portal.process_service_data_hwater_import");
				contServiceDataHWaterImportRepository.processImport(session.getSessionUUID().toString());
				log.debug("processImport.Calling Stored proc portal.process_service_data_hwater_import SUCCESS");
			} catch (Exception e) {

				PSQLException pe = DBExceptionUtils.getPSQLException(e);
				String sqlExceptiomMessage = pe != null ? pe.getMessage() : e.getMessage();
				log.error("Data Import. Exception: {}. sessionUUID({}). Exception : {}",
						e.getClass().getSimpleName(), session.getSessionUUID(), sqlExceptiomMessage);

				session.web().trace("Ошибка при обновлении данных");

                SLogSessionUtils.failSession(session,
                    sqlExceptiomMessage, errorMessage);

				throw new IllegalArgumentException(
						String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "DB save error", importInfo.getUserFileName()));
			}

			session.web().trace("Обновление данных успешно завершено");
            SLogSessionUtils.completeSession(session, completeMessage);
		}
	}

	/**
	 *
	 * @param serviceDataImportInfos
	 * @return
	 */
	public Future<Boolean> submitImportTask(final Long subscriberId, final List<ServiceDataImportInfo> serviceDataImportInfos) {
        return subscriberExecutorService.submit(subscriberId, () -> {
            try {
                log.debug("Start HWaterImportTask");
                if (serviceDataImportInfos.isEmpty()) {
                    return Boolean.FALSE;
                }
                log.debug("Import Data");
                try {
                    importData(subscriberId, serviceDataImportInfos);
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            } finally {
                log.debug("Finish HWaterImportTask");
            }
        });
    }

}
