package ru.excbt.datafuse.nmk.data.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EditDataSourceDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityAutomation {

    private static final Logger log = LoggerFactory.getLogger(EntityAutomation.class);

    public static final long DEFAULT_MODEL_ID = 1;
    public static final long DEFAULT_SUBSCR_DATASOURCE_ID = 67366624L;

    public static final long DEVICE_OBJECT_VERIFICATION_PLUS_YEARS = 1;


    /**
     *
     * @param contObject
     * @param deviceObjectService
     * @return
     */
    public static DeviceObject createDeviceObject(ContObject contObject, DeviceObjectService deviceObjectService, String deviceSerial) {
        assertThat(deviceObjectService).isNotNull();
        assertThat(contObject).isNotNull();
        DeviceObjectDTO deviceObjectDTO = new DeviceObjectDTO();
        deviceObjectDTO.setContObjectId(contObject.getId());
        deviceObjectDTO.setDeviceModelId(DEFAULT_MODEL_ID);
        deviceObjectDTO.setEditDataSourceInfo(new EditDataSourceDTO());
        deviceObjectDTO.getEditDataSourceInfo().setSubscrDataSourceId(DEFAULT_SUBSCR_DATASOURCE_ID);
        deviceObjectDTO.setNumber(deviceSerial);
        deviceObjectDTO.setVerificationDate(LocalDateUtils.asDate(LocalDate.now().plusYears(DEVICE_OBJECT_VERIFICATION_PLUS_YEARS)));
        DeviceObject deviceObject = deviceObjectService.automationCreate(deviceObjectDTO);

        assertThat(deviceObject).isNotNull();
        assertThat(deviceObject.getContObject()).isNotNull();
        assertThat(deviceObject.getContObject().getId()).isNotNull();
        return deviceObject;
    }


    public static Consumer<DeviceObjectDTO> defaultDeviceObjectInitializer = (d) -> {
        d.setDeviceModelId(DEFAULT_MODEL_ID);
        if (d.getEditDataSourceInfo() == null) {
            d.setEditDataSourceInfo(new EditDataSourceDTO());
        }
        d.getEditDataSourceInfo().setSubscrDataSourceId(DEFAULT_SUBSCR_DATASOURCE_ID);
        d.setVerificationDate(LocalDateUtils.asDate(LocalDate.now().plusYears(DEVICE_OBJECT_VERIFICATION_PLUS_YEARS)));
    };


    /**
     *
     * @param initializer
     * @param deviceObjectService
     * @return
     */
    public static DeviceObject createDeviceObject(Consumer<DeviceObjectDTO> initializer, DeviceObjectService deviceObjectService) {
        assertThat(initializer).isNotNull();
        assertThat(deviceObjectService).isNotNull();
        DeviceObjectDTO deviceObjectDTO = new DeviceObjectDTO();

        defaultDeviceObjectInitializer.accept(deviceObjectDTO);
        initializer.accept(deviceObjectDTO);

        assertThat(deviceObjectDTO.getContObjectId()).isNotNull();
        assertThat(deviceObjectDTO.getDeviceModelId()).isNotNull();
        assertThat(deviceObjectDTO.getEditDataSourceInfo()).isNotNull();
        assertThat(deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceId()).isNotNull();
        assertThat(deviceObjectDTO.getVerificationDate()).isNotNull();

        DeviceObject deviceObject = deviceObjectService.automationCreate(deviceObjectDTO);

        assertThat(deviceObject).isNotNull();
        assertThat(deviceObject.getContObject()).isNotNull();
        assertThat(deviceObject.getContObject().getId()).isNotNull();
        return deviceObject;
    }


    /**
     *
     * @param contObject
     * @param deviceObjectService
     * @param deviceObectsNumbers
     * @return
     */
    public static List<DeviceObject> createDeviceObjects (ContObject contObject, DeviceObjectService deviceObjectService, String ... deviceObectsNumbers) {

        assertThat(contObject).isNotNull();
        assertThat(contObject.getId()).isNotNull();

        List<DeviceObject> createdDeviceObjects = new ArrayList<>();

        for (String number: deviceObectsNumbers) {
            DeviceObject deviceObject = EntityAutomation.createDeviceObject(
                contObject,
                deviceObjectService,
                number);

            createdDeviceObjects.add(deviceObject);
        }

        assertThat(createdDeviceObjects).hasSize(deviceObectsNumbers.length);

        return createdDeviceObjects;
    }


    /**
     *
     * @param contObjectName
     * @param contObjectService
     * @param portalUserIds
     * @return
     */
    public static ContObject createContObject(String contObjectName, ContObjectService contObjectService, PortalUserIds portalUserIds) {
        return createContObject((co) -> {
            co.setName(contObjectName);
            co.setFullName(contObjectName);
        }, contObjectService, portalUserIds);
    }


    private static Consumer<ContObjectDTO> defaultContObjectInitializer = (co) -> {
        co.setComment("Created by Test");
        co.setTimezoneDefKeyname("MSK");
    };


    /**
     *
     * @param initializer
     * @param contObjectService
     * @param portalUserIds
     * @return
     */
    public static ContObject createContObject(Consumer<ContObjectDTO> initializer, ContObjectService contObjectService, PortalUserIds portalUserIds) {
        Objects.requireNonNull(initializer);

        ContObjectDTO contObjectDTO = new ContObjectDTO();

        defaultContObjectInitializer.accept(contObjectDTO);
        initializer.accept(contObjectDTO);

        assertThat(contObjectDTO.getId()).isNull();
        assertThat(contObjectDTO.getName()).isNotBlank();
        ContObject contObject = contObjectService.automationCreate(contObjectDTO, portalUserIds.getSubscriberId(), LocalDate.now(), null);

        assertThat(contObject).isNotNull();
        assertThat(contObject.getId()).isNotNull();

        return contObject;
    }


    /**
     *
     * @param contServiceTypeKey
     * @param contObject
     * @param deviceObject
     * @param contZPointService
     * @return
     */
    public static ContZPoint createContZPoint (ContServiceTypeKey contServiceTypeKey, ContObject contObject, DeviceObject deviceObject, ContZPointService contZPointService) {
        ContZPointDTO contZPointDTO = new ContZPointDTO();
        contZPointDTO.setContObjectId(contObject.getId());
        contZPointDTO.setContServiceTypeKeyname(contServiceTypeKey.getKeyname());
        contZPointDTO.setStartDate(new Date());
        contZPointDTO.setDeviceObjectId(deviceObject.getId());

        ContZPoint contZPoint = contZPointService.createZPoint(contZPointDTO);

        assertThat(contZPoint).isNotNull();
        assertThat(contZPoint.getId()).isNotNull();
        return contZPoint;
    }

    private static Consumer<ContZPointDTO> defaultContZPointInitializer = (zp) -> {
        zp.setContServiceTypeKeyname(ContServiceTypeKey.CW.keyName());
        zp.setStartDate(new Date());
    };


    /**
     *
     * @param initializer
     * @param contZPointService
     * @return
     */
    public static ContZPoint createContZPoint (Consumer<ContZPointDTO> initializer, ContZPointService contZPointService) {
        ContZPointDTO contZPointDTO = new ContZPointDTO();

        defaultContZPointInitializer.accept(contZPointDTO);
        initializer.accept(contZPointDTO);
        assertThat(contZPointDTO.getContObjectId()).isNotNull();
        assertThat(contZPointDTO.getContServiceTypeKeyname()).isNotNull();
        assertThat(contZPointDTO.getDeviceObjectId()).isNotNull();

        ContZPoint contZPoint = contZPointService.createZPoint(contZPointDTO);

        assertThat(contZPoint).isNotNull();
        assertThat(contZPoint.getId()).isNotNull();
        return contZPoint;
    }

}
