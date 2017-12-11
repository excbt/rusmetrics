package ru.excbt.datafuse.nmk.data.model.modelmapper;

import com.github.jmnarloch.spring.boot.modelmapper.PropertyMapConfigurerSupport;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

/**
 * Created by kovtonyk on 29.03.2017.
 */
@Component
@Slf4j
public class DeviceObjectFromDTOMapping //extends PropertyMapConfigurerSupport<DeviceObjectDTO, DeviceObject>
{

//    @Override
//    public PropertyMap<DeviceObjectDTO, DeviceObject> mapping() {
//        return new PropertyMap<DeviceObjectDTO, DeviceObject>() {
//            @Override
//            protected void configure() {
//                skip(destination.getDeviceLogin());
//                skip(destination.getDevicePassword());
//            }
//        };
//    }
}
