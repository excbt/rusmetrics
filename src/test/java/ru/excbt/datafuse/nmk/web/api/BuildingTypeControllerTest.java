/**
 *
 */
package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.service.BuildingTypeService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;


/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 *
 */
@RunWith(SpringRunner.class)
public class BuildingTypeControllerTest extends PortalApiTest {


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private BuildingTypeController buildingTypeController;

    @Autowired
    private BuildingTypeService buildingTypeService;

    @Autowired
    private ModelMapper modelMapper;

    private MockMvcRestWrapper restWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        buildingTypeController = new BuildingTypeController(buildingTypeService, modelMapper);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(buildingTypeController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        restWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }


	/*

	 */
	@Test

	public void testBuildingType() throws Exception {
        restWrapper.restRequest("/api/subscr/service/buildingType/list").testGet();
	}

	/*

	 */
	@Test
	public void testBuildingTypeCategory() throws Exception {
        restWrapper.restRequest("/api/subscr/service/buildingType/category/list").testGet();
	}

}
