package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointDeviceHistory;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.ContZPointDeviceHistoryRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class ContZPointDeviceHistoryServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ContZPointDeviceHistoryServiceTest.class);

    @Autowired
    private ContZPointDeviceHistoryService contZPointDeviceHistoryService;

    @Autowired
    private ContZPointDeviceHistoryRepository repository;

    @Autowired
    private ContZPointRepository contZPointRepository;

    @Autowired
    private ObjectAccessService objectAccessService;

    @Mock
    private PortalUserIdsService portalUserIdsService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
    }


    @Test
    @Transactional
    public void saveHistoryTest() {
        PortalUserIds portalUserIds = portalUserIdsService.getCurrentIds();
        List<Long> contZPointIds = objectAccessService.findAllContZPointIds(portalUserIds);

        ContZPoint contZPoint = contZPointRepository.findOne(contZPointIds.get(0));

        contZPointDeviceHistoryService.saveHistory(contZPoint);
        contZPointDeviceHistoryService.saveHistory(contZPoint);
        contZPointDeviceHistoryService.saveHistory(contZPoint);

//
        List<ContZPointDeviceHistory> history = repository.findLastByContZPoint(contZPoint, new PageRequest(0, 1));
        //Stream<ContZPointDeviceHistory> history2 = repository.findTop1ByContZPointAndEndDateIsNullOrderByStartDateDesc(contZPoint);

        assertFalse(history.isEmpty());
        //assertNotNull(history2);


        List<ContZPointDeviceHistory> allHistory = repository.findAllByContZPoint(contZPoint);
        assertTrue(allHistory.size() == 1);

    }

    @Test
    @Transactional
    public void finishHistoryTest() {
        PortalUserIds portalUserIds = portalUserIdsService.getCurrentIds();
        List<Long> contZPointIds = objectAccessService.findAllContZPointIds(portalUserIds);

        ContZPoint contZPoint = contZPointRepository.findOne(contZPointIds.get(0));

        contZPointDeviceHistoryService.saveHistory(contZPoint);
        List<ContZPointDeviceHistory> history = repository.findLastByContZPoint(contZPoint, new PageRequest(0, 1));

        assertFalse(history.isEmpty());


        log.info("History: {}", history.get(0));

        contZPointDeviceHistoryService.finishHistory(contZPoint);

        history = repository.findLastByContZPoint(contZPoint, new PageRequest(0, 1));

        assertTrue(history.isEmpty());
    }
}
