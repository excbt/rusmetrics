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

import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataImpulseUCsv;
import ru.excbt.datafuse.nmk.data.model.support.FileImportInfo;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseImportRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SystemUserRepository;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.slog.service.SLogSessionUtil;
import ru.excbt.datafuse.nmk.slog.service.SLogWriterService;
import ru.excbt.datafuse.nmk.utils.DateInterval;
import ru.excbt.datafuse.slogwriter.service.SLogSessionStatuses;
import ru.excbt.datafuse.slogwriter.service.SLogSessionT1;

import javax.persistence.PersistenceException;
import java.io.CharConversionException;
import java.io.File;
import java.time.LocalDateTime;
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
    public static final TimeDetailKey CSV_TIME_DETAIL = TimeDetailKey.TYPE_1MON_ABS;


	private final ContServiceDataImpulseRepository contServiceDataImpulseRepository;

	private final SubscriberExecutorService subscriberExecutorService;

    private final SLogWriterService sLogWriterService;

    private final SubscrDataSourceService subscrDataSourceService;

    private final ImpulseCsvService impulseCsvService;

    private final SubscrUserRepository subscrUserRepository;
    private final SystemUserRepository systemUserRepository;
    private final DeviceObjectService deviceObjectService;
    private final ContZPointService contZPointService;

    private final ContServiceDataImpulseImportRepository impulseImportRepository;


    @Autowired
    public ContServiceDataImpulseService(ContServiceDataImpulseRepository contServiceDataImpulseRepository,
                                         SubscriberExecutorService subscriberExecutorService,
                                         SLogWriterService sLogWriterService,
                                         SubscrDataSourceService subscrDataSourceService,
                                         ImpulseCsvService impulseCsvService,
                                         SubscrUserRepository subscrUserRepository,
                                         SystemUserRepository systemUserRepository,
                                         DeviceObjectService deviceObjectService,
                                         ContZPointService contZPointService,
                                         ContServiceDataImpulseImportRepository contServiceDataImpulseImportRepository) {
        this.contServiceDataImpulseRepository = contServiceDataImpulseRepository;
        this.subscriberExecutorService = subscriberExecutorService;
        this.sLogWriterService = sLogWriterService;
        this.subscrDataSourceService = subscrDataSourceService;
        this.impulseCsvService = impulseCsvService;
        this.subscrUserRepository = subscrUserRepository;
        this.systemUserRepository = systemUserRepository;
        this.deviceObjectService = deviceObjectService;
        this.contZPointService = contZPointService;
        this.impulseImportRepository = contServiceDataImpulseImportRepository;
    }

    /**
     *
     * @param contZPointId
     * @param timeDetail
     * @param dateInterval
     * @param pageRequest
     * @return
     */
    @Transactional( readOnly = true)
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
    @Transactional
    @Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
    public void importData(final long subscrUserId, final List<FileImportInfo> fileImportInfos) {


        Long rmaSubscriberId;
        {
            SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
            if (subscrUser == null) {

                SystemUser systemUser = systemUserRepository.findOne(subscrUserId);
                if (systemUser == null) {
                    log.error("subscrUser is not found");
                    throw new IllegalStateException("subscrUserId=" + subscrUserId + " is not found");
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
            SLogSessionUtil.checkCsvSeparators(session, importInfo);
            session.web().trace("Проверка целостности CSV пройдена");
            session.web().trace("Преобразование данных файла");

            // Reading CSV from FILE
            List<ContServiceDataImpulseUCsv> inDataImport;

            try {
                inDataImport = impulseCsvService.parseDataImpulseUCsvImport(new File(importInfo.getInternalFileName()));
            } catch (Exception e) {
                if (e instanceof RuntimeJsonMappingException) {
                    session.web().error("Ошибка преобразования данных файла. Некорректные данные: " + e.getMessage());
                } else if (e instanceof CharConversionException) {
                    session.web().error("Некорректная кодировка файла. Ожидается файл в кодировке UTF-8 ");
                } else {
                    session.web().error("Общая ошибка: " + e.getMessage());
                }

                SLogSessionUtil.failSession(session,
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

                SLogSessionUtil.failSession(session,
                    errorMessage, errorStatusMessage);
                return;
            }

            if (accessDeniedLoginNames.size() > 0) {
                StringBuilder sb = new StringBuilder();
                accessDeniedLoginNames.forEach((i) -> sb.append("\n").append(i));
                session.web().error("Отказано в доступе к пользователям:" + sb.toString());
                SLogSessionUtil.failSession(session,
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
                SLogSessionUtil.failSession(session,
                    errorMessage, errorStatusMessage);
                return;
            }

            session.web().trace("Проверка приборов прошла успешно");


            session.web().trace("Поиск точек учета");


            List<ContServiceDataImpulseImport> impulseImportList = new ArrayList<>();

            for (ContServiceDataImpulseUCsv data : inDataImport) {
                DeviceObject deviceObject = deviceObjectMap.get(data.getSerial());

                if (deviceObject == null) {
                    session.web().error("Внутренняя ошибка. Не найден прибор " + data.getSerial());
                    SLogSessionUtil.failSession(session,
                        errorMessage, errorStatusMessage);
                    return;

                }

                List<ContZPoint> contZPoints = contZPointService.selectContPointsByDeviceObject(deviceObject.getId());
                if (contZPoints.size() == 0) {
                    session.web().error("Не найдена точка учета для прибора:" + data.getSerial());
                    SLogSessionUtil.failSession(session,
                        errorMessage, errorStatusMessage);
                    return;
                }
                if (contZPoints.size() > 1) {
                    session.web().error("Точка учета для прибора:" + data.getSerial() + " не уникальна");
                    SLogSessionUtil.failSession(session,
                        errorMessage, errorStatusMessage);
                    return;
                }

                ContZPoint contZPoint = contZPoints.get(0);
                if (!Boolean.TRUE.equals(contZPoint.getIsManualLoading())) {
                    session.web().error("Точка учета для прибора: " + data.getSerial() + " не поддерживает прямую загрузку.");
                    SLogSessionUtil.failSession(session,
                        errorMessage, errorStatusMessage);
                    return;
                }


                ContServiceDataImpulseImport impulseImport = new ContServiceDataImpulseImport();
                impulseImport.setContZpointId(contZPoint.getId());
                impulseImport.setDeviceObjectId(deviceObject.getId());
                impulseImport.setDataValue(data.getDataValue());
                if (data.getDataDate() != null) {
                    impulseImport.setDataDate(data.getDataDate().atTime(0, 0));
                }
                impulseImport.setTimeDetailType(CSV_TIME_DETAIL.keyName());
                impulseImport.setCreatedBy(subscrUserId);
                impulseImport.setCreatedDate(LocalDateTime.now());
                impulseImport.setLastModifiedBy(subscrUserId);
                impulseImport.setLastModifiedDate(LocalDateTime.now());
                impulseImport.setTrxId(session.getSessionUUID());
                impulseImportList.add(impulseImport);
            }

            try {

                impulseImportRepository.save(impulseImportList);
            } catch (IllegalStateException e) {
                session.web().error("Ошибка при загрузке данных");
                SLogSessionUtil.failSession(session,
                    errorMessage, errorStatusMessage);
                return;
            }

            session.web().trace("Все точки учета найдены");

            session.web().trace("Обновление данных запущено");
            try {
                log.debug("processImport.Calling Stored proc portal.process_service_data_impulse_import");
                impulseImportRepository.processImport(session.getSessionUUID().toString());
                log.debug("processImport.Calling Stored proc portal.process_service_data_impulse_import SUCCESS");
            } catch (Exception e) {
                String sqlExceptiomMessage =  DBExceptionUtil.getPSQLExceptionMessage(e);
                log.error("Impulse Data Import. Exception: {}. sessionUUID({}). Exception : {}",
                    e.getClass().getSimpleName(), session.getSessionUUID(), sqlExceptiomMessage);

                session.web().trace("Ошибка при обновлении данных");

                SLogSessionUtil.failSession(session,
                    sqlExceptiomMessage, errorMessage);

                throw new IllegalArgumentException(
                    String.format(FileImportInfo.IMPORT_EXCEPTION_TEMPLATE, "DB save error", importInfo.getUserFileName()));
            }

            session.web().trace("Обновление данных успешно завершено");

            SLogSessionUtil.completeSession(session, completeStatusMessage);
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
