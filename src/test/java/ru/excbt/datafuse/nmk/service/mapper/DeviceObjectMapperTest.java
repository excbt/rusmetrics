package ru.excbt.datafuse.nmk.service.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigTest;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

/**
 * Created by kovtonyk on 23.05.2017.
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
public class DeviceObjectMapperTest extends JpaConfigTest {

    @Autowired
    private DeviceObjectMapper deviceObjectMapper;

    @Test
    public void testDeviceObjectMapping() throws Exception {
        DeviceObject object = new DeviceObject();
        object.setId(100L);
        object.setExCode("MY_CODE");
        object.setDeviceObjectName("MyName");
        DeviceObjectDTO dto = deviceObjectMapper.toDto(object);
        Assert.assertEquals(object.getId(), dto.getId());
        Assert.assertEquals(object.getExCode(), dto.getExCode());
        Assert.assertEquals(object.getDeviceObjectName(), dto.getDeviceObjectName());
    }
}
