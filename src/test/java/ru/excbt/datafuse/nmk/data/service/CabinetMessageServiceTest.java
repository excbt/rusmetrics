package ru.excbt.datafuse.nmk.data.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageDirection;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.CabinetMessageRepository;
import ru.excbt.datafuse.nmk.service.mapper.CabinetMessageMapper;
import ru.excbt.datafuse.nmk.utils.ExcbtSubscriberMock;


@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
//@Profile("CabinetMessage")
public class CabinetMessageServiceTest extends JpaSupportTest {

    private static final Logger log = LoggerFactory.getLogger(CabinetMessageServiceTest.class);

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
    }

    @Test
    public void findAllToSubscriber() throws Exception {

        Pageable request = new PageRequest(0,10);

        ExcbtSubscriberMock.setupRma(portalUserIds);

        Page<CabinetMessageDTO> list = cabinetMessageService.findAllRequestToSubscriber(portalUserIds, request);

        log.info("SibscriberId:{}. Size of cabinetMessages: {}", portalUserIds.getSubscriberId(), list.getContent().size());

    }

    @Test
    public void createResponseToSubscriber() throws Exception {

        Pageable request = new PageRequest(0,10);

        ExcbtSubscriberMock.setupRma(portalUserIds);

        int databaseSizeBeforeCreate = cabinetMessageRepository.findAll().size();

        log.info("SibscriberId:{}. Size of cabinetMessages before: {}", portalUserIds.getSubscriberId(), databaseSizeBeforeCreate);

        Page<CabinetMessageDTO> list = cabinetMessageService.findAllRequestToSubscriber(portalUserIds, request);
        list.getContent().stream().filter(i -> i.getFromPortalSubscriberId() != null).limit(1).map(i -> {
            CabinetMessageDTO cabinetMessageResponseDTO = new CabinetMessageDTO();
            cabinetMessageResponseDTO.setMessageDirection(CabinetMessageDirection.OUT.name());
            cabinetMessageResponseDTO.setResponseToId(i.getId());
            CabinetMessageDTO result = cabinetMessageService.saveResponse(cabinetMessageResponseDTO, portalUserIds);
            return result;
        }).findFirst().ifPresent(c -> {
            log.info("Created CabinetMessage:{}", c.toString());
        });

        int databaseSizeAfterCreate = cabinetMessageRepository.findAll().size();


        log.info("SibscriberId:{}. Size of cabinetMessages after: {}", portalUserIds.getSubscriberId(), databaseSizeAfterCreate);

        Assert.assertEquals(databaseSizeBeforeCreate, databaseSizeAfterCreate - 1);

        //log.info("SibscriberId:{}. Size of cabinetMessages: {}", portalUserIds.getSubscriberId(), list.getContent().size());

    }


    @Test
    public void findAllRequest() throws Exception {
    }

    @Test
    public void findMessageChain() throws Exception {
    }

    @Override
    public long getSubscriberId() {
        return EXCBT_RMA_SUBSCRIBER_ID;
    }

    /*

     */
    @Override
    public long getSubscrUserId() {
        return EXCBT_RMA_SUBSCRIBER_USER_ID;
    }



}
