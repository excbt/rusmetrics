/**
 *
 */
package ru.excbt.datafuse.nmk.data.service;

import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataImpulseUCsv;
import ru.excbt.datafuse.nmk.data.model.support.FileImportInfo;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SystemUserRepository;
import ru.excbt.datafuse.nmk.data.service.support.CsvUtils;
import ru.excbt.datafuse.nmk.data.service.support.SLogSessionUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.slog.service.SLogWriterService;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.slogwriter.service.SLogSession;
import ru.excbt.datafuse.slogwriter.service.SLogSessionStatuses;
import ru.excbt.datafuse.slogwriter.service.SLogSessionT1;
import sun.plugin.dom.exception.InvalidStateException;

import javax.persistence.PersistenceException;
import java.io.CharConversionException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 *
 */
@Service
public class ContServiceDataImpulseService implements SecuredRoles {

    private static final Logger log = LoggerFactory.getLogger(ContServiceDataImpulseService.class);

    public static final int SERIAL_LEN = 4;


	private final ContServiceDataImpulseRepository contServiceDataImpulseRepository;

	private final SubscriberExecutorService subscriberExecutorService;

    private final SLogWriterService sLogWriterService;

    private final SubscrDataSourceService subscrDataSourceService;

    private final ImpulseCsvService impulseCsvService;

    private final SubscrUserRepository subscrUserRepository;
    private final SystemUserRepository systemUserRepository;
    private final DeviceObjectService deviceObjectService;


    @Autowired
    public ContServiceDataImpulseService(ContServiceDataImpulseRepository contServiceDataImpulseRepository,
                                         SubscriberExecutorService subscriberExecutorService,
                                         SLogWriterService sLogWriterService,
                                         SubscrDataSourceService subscrDataSourceService,
                                         ImpulseCsvService impulseCsvService,
                                         SubscrUserRepository subscrUserRepository,
                                         SystemUserRepository systemUserRepository,
                                         DeviceObjectService deviceObjectService) {
        this.contServiceDataImpulseRepository = contServiceDataImpulseRepository;
        this.subscriberExecutorService = subscriberExecutorService;
        this.sLogWriterService = sLogWriterService;
        this.subscrDataSourceService = subscrDataSourceService;
        this.impulseCsvService = impulseCsvService;
        this.subscrUserRepository = subscrUserRepository;
        this.systemUserRepository = systemUserRepository;
        this.deviceObjectService = deviceObjectService;
    }

    /**
     *
     * @param contZPointId
     * @param timeDetail
     * @param dateInterval
     * @param pageRequest
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public Page<ContServiceDataImpulse> selectImpulseByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
                                                                  DateInterval dateInterval, PageRequest pageRequest) {
        checkArgument(contZPointId > 0);
        checkNotNull(timeDetail);
        checkNotNull(dateInterval);
        checkArgument(dateInterval.isValidEq(), "DateInterval is invalid");

        return contServiceDataImpulseRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
            dateInterval.getFromDate(), dateInterval.getToDate(), pageRequest);

    }


    /**
     *
     * @param subscrUserId aka authorId
     * @param fileImportInfos
     */
    @Transactional(value = TxConst.TX_DEFAULT)
    @Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
    public void importData(final long subscrUserId, final List<FileImportInfo> fileImportInfos) {


        Long rmaSubscriberId;
        {
            SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
            if (subscrUser == null) {

                SystemUser systemUser = systemUserRepository.findOne(subscrUserId);
                if (systemUser == null) {
                    log.error("subscrUser is not found");
                    throw new InvalidStateException("subscrUserId=" + subscrUserId + " is not found");
                } else {
                    rmaSubscriberId = systemUser.getSubscriber().getIsRma() ? systemUser.getSubscriber().getId() :
                        systemUser.getSubscriber().getRmaSubscriberId();
                }
            } else {
                rmaSubscriberId = subscrUser.getSubscriber().getIsRma() ? subscrUser.getSubscriber().getId() :
                    subscrUser.getSubscriber().getRmaSubscriberId();
            }
        }

        Long csvDataSourceId = subscrDataSourceService.csvFileId();

        if (csvDataSourceId == null) {
            throw new PersistenceException("CSV_FILE datasource is not found");
        }

        for (FileImportInfo importInfo : fileImportInfos) {

            String msg = String.format("Пользователь ID %d Загрузка файла %s ",
                subscrUserId, FilenameUtils.getName(importInfo.getInternalFileName()));

            String errorStatusMessage = String.format(FileImportInfo.IMPORT_ERROR_TEMPLATE, importInfo.getUserFileName());
            String completeStatusMessage = String.format(FileImportInfo.IMPORT_COMPLETE_TEMPLATE, importInfo.getUserFileName());
            String errorMessage = "Ошибка. Данные из файла не могут быть обработаны";

            log.debug("Log message", msg);

            SLogSessionT1 session = sLogWriterService.newSessionWebT1(csvDataSourceId,
                null, msg, subscrUserId);

            session.status(SLogSessionStatuses.GENERATING.getKeyname(),
                "Загрузка файла: " + importInfo.getUserFileName());


            session.web().trace("Проверка целостности CSV файла");
            SLogSessionUtils.checkCsvSeparators(session, importInfo);
            session.web().trace("Проверка целостности CSV пройдена");
            session.web().trace("Преобразование данных файла");

            // Reading CSV from FILE
            List<ContServiceDataImpulseUCsv> inDataImport;
            try (FileInputStream fio = new FileInputStream(importInfo.getInternalFileName())) {
                inDataImport = impulseCsvService.parseDataImpulseUCsvImport(fio);
            } catch (Exception e) {
                if (e instanceof RuntimeJsonMappingException) {
                    session.web().error("Ошибка преобразования данных файла. Некорректные данные: " + e.getMessage());
                } else if (e instanceof CharConversionException) {
                    session.web().error("Некорректная кодировка файла. Ожидается файл в кодировке UTF-8 ");
                } else {
                    session.web().error("Общая ошибка: " + e.getMessage());
                }

                SLogSessionUtils.failSession(session,
                    "Ошибка. Данные из файла не могут быть обработаны", errorStatusMessage);

                log.error("Data Import. Exception: IOException. sessionUUID({}). Exception message: {}",
                    session.getSessionUUID(), e.getMessage());
                throw new IllegalArgumentException(
                    String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "Parsing error", importInfo.getUserFileName()));
            }

            session.web().trace("Преобразование данных файла завершено");

            session.web().trace("Проверка пользователей");


            List<String> invalidLoginNames = new ArrayList<>();
            List<String> accessDeniedLoginNames = new ArrayList<>();

            inDataImport.forEach((i) -> {
                Optional<SubscrUser> user = subscrUserRepository.findOneByUserNameIgnoreCase(i.getLogin());
                if (!user.isPresent()) {
                    invalidLoginNames.add(i.getLogin());
                }

                Long loginRmaSubscriberId = headerSubscriberId(user.get());

                if (!Boolean.TRUE.equals(user.get().getSubscriber().getIsRma()) &&
                    !rmaSubscriberId.equals(loginRmaSubscriberId)) {
                    accessDeniedLoginNames.add(i.getLogin());
                }
            });


            if (invalidLoginNames.size() > 0) {

                StringBuilder sb = new StringBuilder();
                invalidLoginNames.forEach((i) -> sb.append("\n").append(i));

                session.web().error("Не найденные пользователи:" + sb.toString());

                SLogSessionUtils.failSession(session,
                    errorMessage, errorStatusMessage);
                return;
            }

            if (accessDeniedLoginNames.size() > 0) {
                StringBuilder sb = new StringBuilder();
                accessDeniedLoginNames.forEach((i) -> sb.append("\n").append(i));
                session.web().error("Отказано в доступе к пользователям:" + sb.toString());
                SLogSessionUtils.failSession(session,
                    errorMessage, errorStatusMessage);
                return;
            }

            session.web().trace("Проверка пользователей прошла успешно");

            session.web().trace("Проверка приборов");

            List<DeviceObject> deviceObjects = deviceObjectService.selectDeviceObjectsBySubscriber(rmaSubscriberId);
            Map<String, DeviceObject> deviceObjectMap = new HashMap<>();
            deviceObjects.forEach((d) -> {
                String serial = null;
                if (d.getNumber() != null) {
                    if (d.getNumber().length() < SERIAL_LEN) {
                        serial = d.getNumber();
                    } else {
                        serial = d.getNumber().substring(d.getNumber().length() - SERIAL_LEN);
                    }
                    deviceObjectMap.put(serial, d);
                }
            });


            List<String> invalidDeviceObjects = new ArrayList<>();
            inDataImport.forEach((i) -> {
                DeviceObject deviceObject = deviceObjectMap.get(i.getSerial());
                if (deviceObject == null) {
                    invalidDeviceObjects.add(i.getSerial());
                }
            });


            if (invalidDeviceObjects.size() > 0) {
                StringBuilder sb = new StringBuilder();
                invalidDeviceObjects.forEach((i) -> sb.append("\n").append(i));
                session.web().error("Не найдены приборы:" + sb.toString());
                SLogSessionUtils.failSession(session,
                    errorMessage, errorStatusMessage);
                return;
            }

            session.web().trace("Проверка приборов прошла успешно");


            SLogSessionUtils.completeSession(session, completeStatusMessage);
        }

    }



    private Long headerSubscriberId (SubscrUser subscrUser) {
        boolean isCabinet = subscrUser.getSubscriber().getParentSubscriberId() != null;
        return isCabinet ? subscrUser.getSubscriber().getParentSubscriberId() : subscrUser.getSubscriber().getId();
    }

    /**
     *
     * @param authorId
     * @param fileImportInfos
     * @return
     */
    public Future<Boolean> submitImportTask(final long authorId, final List<FileImportInfo> fileImportInfos) {
        return subscriberExecutorService.submit(authorId, () -> {
            try {
                log.debug("Start ImpulseImportTask");
                if (fileImportInfos.isEmpty()) {
                    log.warn("import list is empty");
                    return Boolean.FALSE;
                }
                try {
                    importData(authorId, fileImportInfos);
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            } finally {
                log.debug("Finish ImpulseImportTask");
            }
        });
    }


}
