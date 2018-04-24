package ru.excbt.datafuse.nmk.service.widget;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
@Transactional
public class ContEventMonitorWidgetServiceTest {

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
