package ru.excbt.datafuse.nmk.service.widget;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

@RunWith(SpringRunner.class)
public class ContEventMonitorWidgetServiceTest extends PortalDataTest {

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
