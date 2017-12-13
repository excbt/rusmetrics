package ru.excbt.datafuse.nmk.data.service.util;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityAutomation {


    /**
     *
     * @param contObject
     * @param deviceObjectService
     * @return
     */
    public static DeviceObject createDeviceObject(String deviceSerial, ContObject contObject, DeviceObjectService deviceObjectService) {
        assertThat(deviceObjectService).isNotNull();
        assertThat(contObject).isNotNull();
        DeviceObjectDTO deviceObjectDTO = new DeviceObjectDTO();
        deviceObjectDTO.setContObjectId(contObject.getId());
        deviceObjectDTO.setDeviceModelId(1L);
        deviceObjectDTO.setNumber(deviceSerial);
        deviceObjectDTO.setVerificationDate(LocalDateUtils.asDate(LocalDate.now().plusYears(1)));
        DeviceObject deviceObject = deviceObjectService.automationCreate(deviceObjectDTO);
        assertThat(deviceObject).isNotNull();
        assertThat(deviceObject.getContObject()).isNotNull();
        assertThat(deviceObject.getContObject().getId()).isNotNull();
        return deviceObject;
    }


    /**
     *
     * @param contObjectName
     * @param contObjectService
     * @param portalUserIds
     * @return
     */
    public static ContObject createContObject(String contObjectName, ContObjectService contObjectService, PortalUserIds portalUserIds) {
        ContObjectDTO contObjectDTO = new ContObjectDTO();
        contObjectDTO.setComment("Created by Test");
        contObjectDTO.setTimezoneDefKeyname("MSK");
        contObjectDTO.setName(contObjectName);
        ContObject contObject = contObjectService.automationCreate(contObjectDTO, portalUserIds.getSubscriberId(), LocalDate.now(), null);
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

}
