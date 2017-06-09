package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.CabinetOutMeterDataQRepository;
import ru.excbt.datafuse.nmk.data.repository.CabinetOutMeterDataRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 08.06.2017.
 */
@Service
@Transactional
public class CabinetOutService {

    private static final Logger log = LoggerFactory.getLogger(CabinetOutService.class);

    public static final TimeDetailKey CABINET_OUT_TIME_DETAIL = TimeDetailKey.TYPE_1MON_ABS;

    private final CabinetOutMeterDataQRepository cabinetOutMeterDataQRepository;
    private final CabinetOutMeterDataRepository cabinetOutMeterDataRepository;

    private final DeviceObjectService deviceObjectService;

    private final ContZPointRepository contZPointRepository;

    private final ContServiceDataImpulseRepository contServiceDataImpulseRepository;

    public CabinetOutService(CabinetOutMeterDataQRepository cabinetOutMeterDataQRepository,
                             CabinetOutMeterDataRepository cabinetOutMeterDataRepository, DeviceObjectService deviceObjectService, ContZPointRepository contZPointRepository, ContServiceDataImpulseRepository contServiceDataImpulseRepository) {
        this.cabinetOutMeterDataQRepository = cabinetOutMeterDataQRepository;
        this.cabinetOutMeterDataRepository = cabinetOutMeterDataRepository;
        this.deviceObjectService = deviceObjectService;
        this.contZPointRepository = contZPointRepository;
        this.contServiceDataImpulseRepository = contServiceDataImpulseRepository;
    }


    public void importCabinetOut() {

        List<CabinetOutMeterDataQ> qList = cabinetOutMeterDataQRepository.findAllOrOrderDateTime();


        for (CabinetOutMeterDataQ q : qList) {
            if (q.getQId() == null || q.getQId().getId() == null) continue;

            log.debug("Import CabinetOutMeterData:{}", q.getQId().getId());

            CabinetOutMeterData meterData = cabinetOutMeterDataRepository.findOne(q.getQId().getId());
            if (meterData == null || meterData.getDeviceObjectId() == null) {
                log.error("meterData check failed");
                continue;
            }

            List<ContZPoint> contZPoints = contZPointRepository.selectContZPointsByDeviceObjectId(meterData.getDeviceObjectId());
            Optional<ContZPoint> destZP = contZPoints.stream().filter((zp) -> zp.getContServiceType().keyName().equals(meterData.getContServiceType())).findFirst();
            if (!destZP.isPresent()) {
                log.error("contZPoint check failed. DeviceObjectId:{}", meterData.getDeviceObjectId());
                continue;
            }

            if (destZP.get().getDeviceObjects().size() == 0) {
                log.error("deviceObject size check failed");
                continue;
            }

            Optional<DeviceObject> deviceObject = destZP.get().getDeviceObjects().stream()
                .filter((d) -> d.getId().equals(meterData.getDeviceObjectId())).findFirst();

            if (!deviceObject.isPresent()) {
                log.error("deviceObject presence check failed");
                continue;
            }

            if (!Boolean.TRUE.equals(deviceObject.get().getDeviceModel().getIsImpulse())) {
                log.error("Only impulse data is supported yet");
                continue;
            }
            importImpulseData (destZP.get(), meterData, q.getQId().getQDateTime());
            meterData.setProcessed(true);
            meterData.setProcessedDateTime(ZonedDateTime.now());
            cabinetOutMeterDataRepository.save(meterData);
            cabinetOutMeterDataQRepository.delete(q);

            log.debug("SUCCESS Import CabinetOutMeterData:{} is ", q.getQId().getId());
        }
    }


    private void importImpulseData(ContZPoint contZPoint, CabinetOutMeterData meterData, ZonedDateTime dataDate){
        ContServiceDataImpulse dataImpulse = new ContServiceDataImpulse();
        dataImpulse.setContZpointId(contZPoint.getId());
        dataImpulse.setDataDate(LocalDateUtils.asDate(dataDate.toLocalDate()));
        dataImpulse.setDeviceObjectId(meterData.getDeviceObjectId());
        dataImpulse.setTimeDetailType(CABINET_OUT_TIME_DETAIL.keyName());
        dataImpulse.setDataValue(meterData.getMeterValue());
        contServiceDataImpulseRepository.save(dataImpulse);
    }

}
