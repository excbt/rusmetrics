/**
 *
 */
package ru.excbt.datafuse.nmk.data.service;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.data.model.support.FileImportInfo;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.service.support.CsvUtils;
import ru.excbt.datafuse.nmk.data.service.support.SLogSessionUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.slog.service.SLogWriterService;
import ru.excbt.datafuse.slogwriter.service.SLogSessionStatuses;
import ru.excbt.datafuse.slogwriter.service.SLogSessionT1;

import javax.persistence.PersistenceException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
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


	private final ContServiceDataImpulseRepository contServiceDataImpulseRepository;

	private final SubscriberExecutorService subscriberExecutorService;

    private final SLogWriterService sLogWriterService;

    private final SubscrDataSourceService subscrDataSourceService;

    private final ImpulseCsvService impulseCsvService;


    public ContServiceDataImpulseService(ContServiceDataImpulseRepository contServiceDataImpulseRepository,
                                         SubscriberExecutorService subscriberExecutorService,
                                         SLogWriterService sLogWriterService,
                                         SubscrDataSourceService subscrDataSourceService,
                                         ImpulseCsvService impulseCsvService) {
        this.contServiceDataImpulseRepository = contServiceDataImpulseRepository;
        this.subscriberExecutorService = subscriberExecutorService;
        this.sLogWriterService = sLogWriterService;
        this.subscrDataSourceService = subscrDataSourceService;
        this.impulseCsvService = impulseCsvService;
    }

    /**
	 *
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @param pageRequest
	 * @return
	 */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public Page<ContServiceDataImpulse> selectImpulseByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
                                                                  LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
        checkArgument(contZPointId > 0);
        checkNotNull(timeDetail);
        checkNotNull(localDatePeriod);
        checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

        return contServiceDataImpulseRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
            localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

    }


    /**
     *
     * @param subscriberId aka authorId
     * @param fileImportInfos
     */
    @Transactional(value = TxConst.TX_DEFAULT)
    @Secured({ ROLE_ADMIN, ROLE_SUBSCR_ADMIN })
    public void importData(final long subscriberId, final List<FileImportInfo> fileImportInfos) {


        Long csvDataSourceId = subscrDataSourceService.csvFileId();

        if (csvDataSourceId == null) {
            throw new PersistenceException("CSV_FILE datasource is not found");
        }

        for (FileImportInfo importInfo : fileImportInfos) {

            String msg = String.format("Пользователь ID %d Загрузка файла %s ",
                subscriberId, FilenameUtils.getName(importInfo.getInternalFileName()));

            String errorMessage = String.format(FileImportInfo.IMPORT_ERROR_TEMPLATE, importInfo.getUserFileName());
            String completeMessage = String.format(FileImportInfo.IMPORT_COMPLETE_TEMPLATE, importInfo.getUserFileName());

            log.debug("Log message", msg);

            SLogSessionT1 session = sLogWriterService.newSessionWebT1(csvDataSourceId,
                null, msg, subscriberId);

            session.status(SLogSessionStatuses.GENERATING.getKeyname(),
                "Загрузка файла: " + importInfo.getUserFileName());


            SLogSessionUtils.checkCsvSeparators(session, importInfo);

            SLogSessionUtils.completeSession(session, completeMessage);

        }

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
