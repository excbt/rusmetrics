package ru.excbt.datafuse.nmk.data.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.CabinetMessage;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageDirection;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageType;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.CabinetMessageRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.service.mapper.CabinetMessageMapper;
import ru.excbt.datafuse.nmk.utils.ExcbtSubscriberMock;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertFalse;


@RunWith(SpringRunner.class)
public class CabinetMessageServiceTest extends PortalDataTest {

    private static final Logger log = LoggerFactory.getLogger(CabinetMessageServiceTest.class);

    private static final CabinetMessageType CABINET_REQUEST = CabinetMessageType.REQUEST;
    private static final Pageable PAGE = new PageRequest(0,10);


    @Mock
    private PortalUserIdsService portalUserIdsService;


    @Autowired
    private CabinetMessageService cabinetMessageService;

    @Autowired
    private CabinetMessageRepository cabinetMessageRepository;

    @Autowired
    private CabinetMessageMapper cabinetMessageMapper;

    @Mock
    private PortalUserIds portalUserIds;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
    }

    @Test
    public void findAllToSubscriber() throws Exception {

        ExcbtSubscriberMock.setupRma(portalUserIds);

        Page<CabinetMessageDTO> list = cabinetMessageService.findAllRequestToSubscriber(portalUserIds, CABINET_REQUEST, PAGE);

        log.info("SibscriberId:{}. Size of cabinetMessages: {}", portalUserIds.getSubscriberId(), list.getContent().size());

    }

    @Test
    public void createResponseToSubscriber() throws Exception {

        ExcbtSubscriberMock.setupRma(portalUserIds);

        int databaseSizeBeforeCreate = cabinetMessageRepository.findAll().size();

        log.info("SibscriberId:{}. Size of cabinetMessages before: {}", portalUserIds.getSubscriberId(), databaseSizeBeforeCreate);

        if (databaseSizeBeforeCreate == 0) {
            return;
        }

        Page<CabinetMessageDTO> list = cabinetMessageService.findAllRequestToSubscriber(portalUserIds, CABINET_REQUEST, PAGE);
        list.getContent().stream().filter(i -> i.getFromPortalSubscriberId() != null).limit(1).map(i -> {
            CabinetMessageDTO cabinetMessageResponseDTO = new CabinetMessageDTO();
            cabinetMessageResponseDTO.setMessageDirection(CabinetMessageDirection.OUT.name());
            cabinetMessageResponseDTO.setResponseToId(i.getId());
            return cabinetMessageService.saveResponse(cabinetMessageResponseDTO, portalUserIds);
        }).filter(Objects::nonNull).findFirst().ifPresent(c -> {
            log.info("Created CabinetMessage:{}", c.toString());
        });

        int databaseSizeAfterCreate = cabinetMessageRepository.findAll().size();


        log.info("SibscriberId:{}. Size of cabinetMessages after: {}", portalUserIds.getSubscriberId(), databaseSizeAfterCreate);

        Assert.assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate - 1);

        //log.info("SibscriberId:{}. Size of cabinetMessages: {}", portalUserIds.getSubscriberId(), list.getContent().size());

    }


    @Test
    public void updateReviewDate() throws Exception {
        ExcbtSubscriberMock.setupRma(portalUserIds);

        Page<CabinetMessageDTO> list = cabinetMessageService.findAllRequestToSubscriber(portalUserIds, CABINET_REQUEST, PAGE);
        list.getContent().stream().filter(i -> i.getFromPortalSubscriberId() != null)
            .filter(i -> Long.valueOf(getSubscriberId()).equals(i.getToPortalSubscriberId())).limit(1)
            .flatMap(i -> {
                CabinetMessageDTO cabinetMessageResponseDTO = new CabinetMessageDTO();
                cabinetMessageResponseDTO.setMessageDirection(CabinetMessageDirection.OUT.name());
                cabinetMessageResponseDTO.setResponseToId(i.getId());
                return cabinetMessageService.updateMessageChainReview(i.getMasterId() != null ? i.getMasterId() : i.getId(), portalUserIds,false).stream();
            }).forEach(c ->log.info("Updated Review Date id:{}", c.toString()));

    }

    @Test
    public void findAllRequest() throws Exception {
    }

    @Test
    public void findMessageChain() throws Exception {
    }

    public long getSubscriberId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
    }

    /*

     */
    public long getSubscrUserId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
    }


    @Test
    public void testSentNotification() throws Exception {
        UUID masterUuid = cabinetMessageService.sendNotificationToCabinets(portalUserIdsService.getCurrentIds(), "Test Notification", "Test body", null);

        List<CabinetMessage> cabinetMessages = cabinetMessageRepository.findMessageByMasterUuid(masterUuid);
        assertFalse(cabinetMessages.isEmpty());

    }
}
