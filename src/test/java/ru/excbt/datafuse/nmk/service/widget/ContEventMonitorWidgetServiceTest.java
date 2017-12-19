package ru.excbt.datafuse.nmk.service.widget;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

import java.util.List;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ContEventMonitorWidgetServiceTest extends JpaSupportTest {

    private static final Logger log = LoggerFactory.getLogger(ContEventMonitorWidgetServiceTest.class);

    @Autowired
    private ContEventMonitorWidgetService monitorWidgetService;


    @Test
    public void testGetMonitor() throws Exception {
        List<ContEventMonitorWidgetService.ContObjectEventInfo> stats = monitorWidgetService.loadMonitorData(false);
        stats.forEach(i -> log.info("{}", i.toString()));
    }

    @Test
    public void testGetMonitorNested() throws Exception {
        List<ContEventMonitorWidgetService.ContObjectEventInfo> stats = monitorWidgetService.loadMonitorData(true);
        stats.forEach(i -> log.info("{}", i.toString()));
    }
}
