package ru.excbt.datafuse.nmk.service.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigTest;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

/**
 * Created by kovtonyk on 23.05.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
public class DeviceObjectMapperTest {

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
