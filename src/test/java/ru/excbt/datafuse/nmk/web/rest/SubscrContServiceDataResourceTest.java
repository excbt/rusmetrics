package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.ImpulseCsvService;
import ru.excbt.datafuse.nmk.data.service.support.HWatersCsvService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.api.SubscrContServiceDataHWaterControllerTest;
import ru.excbt.datafuse.nmk.web.api.SubscrContServiceDataImpulseController;
import ru.excbt.datafuse.nmk.web.api.SubscrContServiceDataImpulseControllerTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by kovtonyk on 01.06.2017.
 */
@Transactional
public class SubscrContServiceDataResourceTest extends AnyControllerTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrContServiceDataResourceTest.class);


    @Autowired
    private ContServiceDataHWaterService hwaterService;

    @Autowired
    private HWatersCsvService hWatersCsvService;

    @Autowired
    private ImpulseCsvService impulseCsvService;


    @Test
    public void testManualLoadDataMultipleFilesHWater() throws Exception {

        // Prepare File
        MockMultipartFile[] mockMFiles = SubscrContServiceDataHWaterControllerTest.makeMultipartFileCsv(hwaterService, hWatersCsvService,
            "AK-SERIAL-777_1_abracadabra.csv", "AK-SERIAL-777_1_abracadabra2.csv");

        // Processing POST

        String url = "/api/subscr/service-data/cont-objects/import";

        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.fileUpload(url).file(mockMFiles[0]).file(mockMFiles[1]).with(testSecurityContext()));

        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(status().is2xxSuccessful());
        String resultContent = resultActions.andReturn().getResponse().getContentAsString();

        log.info("Uploaded FileInfoMD5:{}", resultContent);

    }

    @Test
    public void testManualLoadDataMultipleFilesImpulse() throws Exception {

        // Prepare File
        MockMultipartFile[] mockMFiles = SubscrContServiceDataImpulseControllerTest.makeMultipartFileCsv(impulseCsvService,
            "clients1.csv", "clients2.csv");
        // Processing POST

        String url = "/api/subscr/service-data/cont-objects/import";

        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.fileUpload(url).file(mockMFiles[0]).file(mockMFiles[1]).with(testSecurityContext()));

        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(status().is2xxSuccessful());
        String resultContent = resultActions.andReturn().getResponse().getContentAsString();

        log.info("Uploaded FileInfoMD5:{}", resultContent);
    }

    @Override
    public long getSubscriberId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
    }

    @Override
    public long getSubscrUserId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
    }


}
