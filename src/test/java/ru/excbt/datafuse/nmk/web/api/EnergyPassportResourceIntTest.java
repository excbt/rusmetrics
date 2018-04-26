package ru.excbt.datafuse.nmk.web.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.energypassport.EnergyPassport401_2014_Add;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionEntryDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportDataRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportDataValueRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.EnergyPassportResource;
import ru.excbt.datafuse.nmk.web.rest.TestUtil;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@RunWith(SpringRunner.class)
public class EnergyPassportResourceIntTest extends PortalApiTest {

    public static final String DUMMY_JSON = "{\"a\":123}";
    private static final Logger log = LoggerFactory.getLogger(EnergyPassportResourceIntTest.class);

    ResultHandler jsonLogger = (i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i));

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private EnergyPassportResource energyPassportResource;

    @Autowired
    private EnergyPassportService energyPassportService;

    @Autowired
    private EnergyPassportRepository energyPassportRepository;

    @Autowired
    private EnergyPassportTemplateRepository energyPassportTemplateRepository;

    @Autowired
    private EnergyPassportDataValueRepository energyPassportDataValueRepository;

    @Autowired
    private EnergyPassportDataRepository energyPassportDataRepository;

    @Autowired
    private ObjectAccessService objectAccessService;

    private MockMvcRestWrapper mockMvcWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        energyPassportResource = new EnergyPassportResource(energyPassportService, portalUserIdsService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(energyPassportResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        this.mockMvcWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc).logger(log);
    }


    private Long getSubscriberId() {
        return portalUserIdsService.getCurrentIds().getSubscriberId();
    }

    @Test
    @Transactional
    public void testCreatePassport() throws Exception {

        EnergyPassportTemplate energyPassportTemplate = EnergyPassportTemplateResourceIntTest.createEnergyPassportTemplate();
        energyPassportTemplate.createSection((s) -> s.sectionKey("P_1.1").sectionOrder(1).sectionJson(DUMMY_JSON));
        energyPassportTemplateRepository.saveAndFlush(energyPassportTemplate);

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").templateKeyname(energyPassportTemplate.getKeyname()).build();


        restPortalContObjectMockMvc.perform(
            post("/api/subscr/energy-passports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(vm)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo(jsonLogger);
    }

    @Test
    @Transactional
    public void testCreatePassport_1() throws Exception {
        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").templateKeyname("ENERGY_DECLARATION_1").build();
        mockMvcWrapper.restRequest("/api/subscr/energy-passports").testPost(vm);
    }

    @Test
    @Transactional
    public void testCreatePassport_2() throws Exception {
        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").templateKeyname("ENERGY_DECLARATION_2").build();
        mockMvcWrapper.restRequest("/api/subscr/energy-passports").testPost(vm);
    }

    @Test
    @Transactional
    public void testCreatePassportExisting() throws Exception {

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").build();

        mockMvcWrapper.restRequest("/api/subscr/energy-passports")
            .requestBuilder(b -> b.param("templateKeyname", EnergyPassport401_2014_Add.ENERGY_DECLARATION_1))
            .testPost(vm);
    }



    @Test
    @Transactional
    public void testGetPassport() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}", passportDTO.getId()).testGet();

    }

    @Test
    @Transactional
    public void testUpdatePassport() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        EnergyPassportVM vm = new EnergyPassportVM();
        vm.setId(passportDTO.getId());
        vm.setPassportName("Passport");
        vm.setDescription("Description");
        vm.setPassportDate(LocalDate.now());

        mockMvcWrapper.restRequest("/api/subscr/energy-passports").testPut(vm);

        EnergyPassport energyPassport = energyPassportRepository.findOne(passportDTO.getId());
        Assert.assertEquals(vm.getDescription(), energyPassport.getDescription());
        Assert.assertEquals(vm.getPassportName(), energyPassport.getPassportName());
        Assert.assertEquals(vm.getPassportDate(), energyPassport.getPassportDate());

    }

    @Test
    @Transactional
    public void testGetPassportData() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();
        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}/data", passportDTO.getId()).testGet();
    }

    @Test
    @Transactional
    public void testGetPassportDataEnhanced() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());
        energyPassportService.savePassportData(passportDataDTOList.get(0));

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}/data", passportDTO.getId()).testGet();
    }

    @Test
    @Transactional
    public void testGetPassportDataSection() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());
        energyPassportService.savePassportData(passportDataDTOList.get(0));

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}/data", passportDTO.getId())
            .requestBuilder((b) -> b.param("sectionId", passportDTO.getSections().get(0).getId().toString()))
            .testGet();


    }

    @Test
    @Transactional
    public void testUpdatePassportData() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());

        passportDataDTOList.forEach((i) -> {
            try {
                mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}/data", passportDTO.getId())
                    .testPut(i);
                List<EnergyPassportDataValue> sectionValues = energyPassportDataValueRepository.findByPassportIdAndSectionKey(passportDTO.getId(), i.getSectionKey());
                Assert.assertTrue(sectionValues.size() > 0);
            } catch (Exception e) {
                //Assert.fail();
                log.error("Error: {}",e);
            }
        });

        List<EnergyPassportDataValue> values = energyPassportDataValueRepository.findByPassportId(passportDTO.getId());
        Assert.assertTrue(values.size() > 0);
    }

    @Test
    @Transactional
    public void testGetPassports() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1,
            EnergyPassportVM.builder().passportName("passport # 1").build(), new Subscriber().id(getSubscriberId()));

        EnergyPassportDTO passportDTOs = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1,
            EnergyPassportVM.builder().passportName("passport # 2").build(), new Subscriber().id(getSubscriberId()));

        energyPassportRepository.flush();

        List<?> checkList = energyPassportService.findBySubscriberId(getSubscriberId());
        Assert.assertTrue(checkList.size() > 0);

        mockMvcWrapper.restRequest("/api/subscr/energy-passports").testGet();

    }

    @Test
    @Transactional
    public void testDeletePassport() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();
        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}", passportDTO.getId()).testDelete();
    }


    private EnergyPassportSectionEntryDTO createSectionEntryDTO(Long sectionId, int entryIdx) {
        EnergyPassportSectionEntryDTO entryDto = new EnergyPassportSectionEntryDTO();
        entryDto.setSectionId(sectionId);
        entryDto.setEntryName("New Entry " + entryIdx);
        entryDto.setEntryDescription("MyDescription " + entryIdx);
        return entryDto;
    }


    @Test
    @Transactional
    public void testPassportSectionEntriesGet() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        passportDTO.getSections().forEach((i) -> log.debug("section:{}", i));

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());

        for (int i = 0; i < 2; i++) {
            energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), i));
        }

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id1}/section/{id2}/entries",
            passportDTO.getId(), sectionDTO.get().getId())
            .testGet();

    }


    @Test
    @Transactional
    public void testPassportSectionEntryUpdate() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        passportDTO.getSections().forEach((i) -> log.debug("section:{}", i));

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());

        EnergyPassportSectionEntryDTO entryDto = null;
        for (int i = 0; i < 3; i++) {
            entryDto = energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), i));
        }

        entryDto.setEntryOrder(1);
        entryDto.setEntryDescription("Modified Description");

        Assert.assertTrue(entryDto.getId() != null);

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id1}/section/{id2}/entries", passportDTO.getId(),
            sectionDTO.get().getId())
            .testPut(entryDto)
            .testGet();

        int checkSize = energyPassportService.findSectionEntries(sectionDTO.get().getId()).size();
        Assert.assertTrue(checkSize == 3);
    }

    @Test
    @Transactional
    public void testPassportWithSectionEntriesGet() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());

        for (int i = 0; i < 2; i++) {
            energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), i));
        }
        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}",passportDTO.getId()).testGet();
    }

    @Test
    @Transactional
    public void testPassportWithSectionEntriesData() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());
        EnergyPassportSectionEntryDTO entryDto = energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), 1));
        Assert.assertNotNull(entryDto.getId());

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());
        Optional<EnergyPassportDataDTO> sectionEntryDataDto = passportDataDTOList.stream().filter((i) -> i.getSectionKey().equals(sectionDTO.get().getSectionKey())).findFirst();
        Assert.assertTrue(sectionEntryDataDto.isPresent());
        sectionEntryDataDto.get().setSectionEntryId(entryDto.getId());

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}/data",passportDTO.getId()).testPut(sectionEntryDataDto.get());

        List<EnergyPassportData> data = energyPassportDataRepository.findByPassportIdAndSectionEntry(passportDTO.getId(), sectionDTO.get().getSectionKey(), entryDto.getId());
        Assert.assertTrue(data.size() == 1);
        Assert.assertTrue(data.get(0).getSectionEntryId() != null);

        List<EnergyPassportDataValue> dataValues = energyPassportDataValueRepository.findByPassportIdAndSectionKey(passportDTO.getId(), sectionDTO.get().getSectionKey(), entryDto.getId());
        dataValues.forEach((i) -> Assert.assertTrue(i.getEnergyPassportDataId() != null));

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id}/data",passportDTO.getId())
            .requestBuilder((b) -> {
                b.param("sectionId", data.get(0).getSectionId().toString());
                b.param("sectionEntryId", data.get(0).getSectionEntryId().toString());
            }).testPut(sectionEntryDataDto.get());


    }

    @Test
    @Transactional
    public void testPassportSectionEntryDelete() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        passportDTO.getSections().forEach((i) -> log.debug("section:{}", i));

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());

        EnergyPassportSectionEntryDTO entryDto = null;
        entryDto = energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), 1));

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id1}/section/{id2}/entries/{id3}",
            passportDTO.getId(), sectionDTO.get().getId(), entryDto.getId()).testDelete();

    }


    @Test
    @Transactional
    public void testPassportContObjects() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();


        List<Long> contObjectIds = objectAccessService.findContObjectIds(getSubscriberId());
        energyPassportService.linkEnergyPassportToContObjects(passportDTO.getId(), contObjectIds, new Subscriber().id(getSubscriberId()));

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/{id1}/cont-object-ids", passportDTO.getId()).testGet();

        energyPassportService.linkEnergyPassportToContObjects(passportDTO.getId(), contObjectIds, new Subscriber().id(getSubscriberId()));
        List<Long> linkedContObjectIds = energyPassportService.findEnergyPassportContObjectIds(passportDTO.getId());

        contObjectIds.sort(Comparator.comparingLong(Long::longValue));

        Assert.assertEquals(contObjectIds, linkedContObjectIds);
    }

    @Test
    @Transactional
    public void testGetContObjectEnergyPassport() throws Exception {

        List<Long> contObjectIds = objectAccessService.findContObjectIds(getSubscriberId());

        EnergyPassportVM vm = new EnergyPassportVM();
        vm.setPassportName("Hallo - 1");

        energyPassportService.createContObjectPassport(vm, contObjectIds.subList(0,1), new Subscriber().id(getSubscriberId()));

        energyPassportDataRepository.flush();

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/cont-objects/{id}", contObjectIds.get(0)).testGet();
    }

    @Test
    @Transactional
    public void testCreateContObjectEnergyPassport() throws Exception {

        List<Long> contObjectIds = objectAccessService.findContObjectIds(getSubscriberId());

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").build();

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/cont-objects/{id1}", contObjectIds.get(0))
            .noJsonOutput()
            .testPost(vm)
            .doWithLastId((i) -> log.info("Added id: {}", i));

    }

    @Test
    @Transactional
    public void testUpdateContObjectEnergyPassport() throws Exception {

        List<Long> contObjectIds = objectAccessService.findContObjectIds(getSubscriberId());

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").passportDate(LocalDate.now()).build();
        EnergyPassportDTO dto = energyPassportService.createContObjectPassport(vm, contObjectIds.subList(0,1), new Subscriber().id(getSubscriberId()));

        energyPassportDataRepository.flush();

        vm = EnergyPassportVM.builder().passportName("bla-bla-bla").passportDate(LocalDate.now()).id(dto.getId()).build();

        mockMvcWrapper.restRequest("/api/subscr/energy-passports/cont-objects/{id1}", contObjectIds.get(0)).testPut(vm);


    }

}
