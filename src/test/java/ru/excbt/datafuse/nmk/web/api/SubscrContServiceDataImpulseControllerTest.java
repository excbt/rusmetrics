package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataImpulseUCsv;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataImpulseService;
import ru.excbt.datafuse.nmk.data.service.ImpulseCsvService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.SubscrContServiceDataHWaterResourceTest;
import ru.excbt.datafuse.nmk.web.rest.SubscrContServiceDataImpulseResource;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by kovtonyk on 01.06.2017.
 */
@RunWith(SpringRunner.class)
public class SubscrContServiceDataImpulseControllerTest extends PortalApiTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrContServiceDataHWaterResourceTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private ImpulseCsvService impulseCsvService;

    private SubscrContServiceDataImpulseResource subscrContServiceDataImpulseResource;
    @Autowired
    private ContServiceDataImpulseService contServiceDataImpulseService;
    @Autowired
    private WebAppPropsService webAppPropsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContServiceDataImpulseResource = new SubscrContServiceDataImpulseResource(contServiceDataImpulseService, webAppPropsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrContServiceDataImpulseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

    }


    /**
     *
     * @param impulseCsvService
     * @param fileNames
     * @return
     * @throws JsonProcessingException
     */
    public static MockMultipartFile[] makeMultipartFileCsv(ImpulseCsvService impulseCsvService,
                                                           String ... fileNames) throws JsonProcessingException {


        List<ContServiceDataImpulseUCsv> impulseList = new ArrayList<>();

        ContServiceDataImpulseUCsv impulse = new ContServiceDataImpulseUCsv();
        impulse.setDataDate(LocalDate.now());
        impulse.setDataValue(123.0);
        impulseList.add(impulse);

        byte[] fileBytes = impulseCsvService.writeDataToUCsv(impulseList);

        MockMultipartFile[] result = new MockMultipartFile[fileNames.length];
        int idx = 0;
        for (String fileName : fileNames) {
            MockMultipartFile mmFile = new MockMultipartFile("files", fileName, "text/plain", fileBytes);
            result[idx++] = mmFile;
        }

        return result;

    }

    @Test
    @Ignore
    public void testImportCsv() throws Exception {

        // Prepare File
        MockMultipartFile[] mockMFiles = makeMultipartFileCsv(impulseCsvService,
            "clients1.csv", "clients2.csv");

        // Processing POST
        String url = "/service/serviceImpulse/contObjects/importData-cl";

        ResultActions resultActions = restPortalMockMvc.perform(
            MockMvcRequestBuilders.fileUpload(url).file(mockMFiles[0]).file(mockMFiles[1]).with(testSecurityContext()));

        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(status().is2xxSuccessful());
        String resultContent = resultActions.andReturn().getResponse().getContentAsString();

        log.info("Uploaded FileInfoMD5:{}", resultContent);
    }

}
