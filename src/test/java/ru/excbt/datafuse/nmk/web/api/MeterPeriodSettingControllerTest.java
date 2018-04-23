/**
 *
 */
package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;
import ru.excbt.datafuse.nmk.data.service.MeterPeriodSettingService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for MeterPeriodSettingController
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.02.2017
 *
 */
@RunWith(SpringRunner.class)
@Transactional
public class MeterPeriodSettingControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private MeterPeriodSettingRepository meterPeriodSettingRepository;

    @Autowired
    private ModelMapper modelMapper;

    private MeterPeriodSettingService meterPeriodSettingService;

    private MeterPeriodSettingController meterPeriodSettingController;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        meterPeriodSettingService = new MeterPeriodSettingService(meterPeriodSettingRepository, modelMapper, portalUserIdsService);
        meterPeriodSettingController = new MeterPeriodSettingController(meterPeriodSettingService, portalUserIdsService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(meterPeriodSettingController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        this.mockMvcRestWrapper = new MockMvcRestWrapper(this.restPortalContObjectMockMvc);
    }



	@Test
	@Transactional
	public void testCreate() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
//		Long id = _testCreateJson("/api/subscr/meter-period-settings", setting);
        Long id = mockMvcRestWrapper
            .restRequest("/api/subscr/meter-period-settings").testPost(setting).getLastId();
        assertNotNull(id);
	}

	@Test
	@Transactional
	public void testGetOne() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);


        String content =
            mockMvcRestWrapper.restRequest("/api/subscr/meter-period-settings/{id}", setting.getId())
            .testGet()
            .getStringContent();
		MeterPeriodSettingDTO result = TestUtils.fromJSON(new TypeReference<MeterPeriodSettingDTO>() {
		}, content);
		assertEquals(setting.getId(), result.getId());
	}

	@Test
	@Transactional
	public void testGet() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);
		final MeterPeriodSettingDTO checkSetting = new MeterPeriodSettingDTO(setting);
		mockMvcRestWrapper.restRequest("/api/subscr/meter-period-settings")
            .testGetAndReturn()
            .andDo((result) -> {
			List<MeterPeriodSettingDTO> resultDTOs = TestUtils.fromJSON(new TypeReference<List<MeterPeriodSettingDTO>>() {
			}, result.getResponse().getContentAsString());
			assertTrue(resultDTOs.stream().filter(i -> i.getId().equals(checkSetting.getId())).findAny().isPresent());

		});
	}

	@Test
	@Transactional
	public void testUpdate() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);
		MeterPeriodSettingDTO newSetting = new MeterPeriodSettingDTO(setting);
		newSetting.setName("New Name");
        mockMvcRestWrapper.restRequest("/api/subscr/meter-period-settings")
            .testPutAndReturn(newSetting)
            .andExpect(status().isOk()).andDo((result) -> {
    			MeterPeriodSettingDTO resultDTO = TestUtils.fromJSON(new TypeReference<MeterPeriodSettingDTO>() {
			}, result.getResponse().getContentAsString());
			assertEquals(resultDTO.getId(), newSetting.getId());
			assertEquals(resultDTO.getName(), newSetting.getName());
		});
	}

	@Test
	@Transactional
	public void testDelete() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);
		mockMvcRestWrapper.restRequest("/api/subscr/meter-period-settings/{id}", setting.getId()).testDelete();
	}

}
