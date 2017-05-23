package ru.excbt.datafuse.nmk.data.model.modelmapper;

import com.github.jmnarloch.spring.boot.modelmapper.PropertyMapConfigurerSupport;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dmo.DeviceObjectDMO;

/**
 * Created by kovtonyk on 29.03.2017.
 */
@Deprecated
@Component
@Slf4j
public class DeviceObjectFromDMOMapping extends PropertyMapConfigurerSupport<DeviceObjectDMO, DeviceObject> {

    @Override
    public PropertyMap<DeviceObjectDMO, DeviceObject> mapping() {
        return new PropertyMap<DeviceObjectDMO, DeviceObject>() {
            @Override
            protected void configure() {
                skip(destination.getDeviceLogin());
                skip(destination.getDevicePassword());
            }
        };
    }
}
