package ru.excbt.datafuse.nmk.data.model.modelmapper;

import com.github.jmnarloch.spring.boot.modelmapper.PropertyMapConfigurerSupport;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

/**
 * Created by kovtonyk on 29.03.2017.
 */
@Component
@Slf4j
public class DeviceObjectToDTOMapping extends PropertyMapConfigurerSupport<DeviceObject, DeviceObjectDTO> {

    @Override
    public PropertyMap<DeviceObject, DeviceObjectDTO> mapping() {
        return new PropertyMap<DeviceObject, DeviceObjectDTO>() {
            @Override
            protected void configure() {
                map().setDeviceModelId(source.getDeviceModelId());
                map().setEditDataSourceInfo(source.getEditDataSourceInfo());
            }
        };
    }
}
