package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.keyname.DeviceDataType;
import ru.excbt.datafuse.nmk.data.repository.keyname.DeviceDataTypeRepository;

import java.util.List;

/**
 * Created by kovtonyk on 30.05.2017.
 */
@Service
public class DeviceDataTypeService {

    private DeviceDataTypeRepository deviceDataTypeRepository;

    public DeviceDataTypeService(DeviceDataTypeRepository deviceDataTypeRepository) {
        this.deviceDataTypeRepository = deviceDataTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<DeviceDataType> findDeviceDataTypes() {
        return deviceDataTypeRepository.findAll();
    }

}
