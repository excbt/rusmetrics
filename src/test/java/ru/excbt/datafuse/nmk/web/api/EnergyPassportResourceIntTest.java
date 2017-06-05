package ru.excbt.datafuse.nmk.web.api;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
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
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 10.04.2017.
 */
public class EnergyPassportResourceIntTest extends AnyControllerTest {

    public static final String DUMMY_JSON = "{\"a\":123}";
    private static final Logger log = LoggerFactory.getLogger(EnergyPassportResourceIntTest.class);
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
    private SubscrContObjectService subscrContObjectService;


    @Test
    @Transactional
    public void testCreatePassport() throws Exception {

        EnergyPassportTemplate energyPassportTemplate = EnergyPassportTemplateResourceIntTest.createEnergyPassportTemplate();
        energyPassportTemplate.createSection((s) -> s.sectionKey("P_1.1").sectionOrder(1).sectionJson(DUMMY_JSON));
        energyPassportTemplateRepository.saveAndFlush(energyPassportTemplate);

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").templateKeyname(energyPassportTemplate.getKeyname()).build();

        RequestExtraInitializer param = (b) -> {
//            b.param("templateKeyname", energyPassportTemplate.getKeyname());
        };

        ResultActions resultActions = _testPostJson("/api/subscr/energy-passports", vm, param, ResultActionsTester.TEST_SUCCESSFULL);
    }

    @Test
    @Transactional
    public void testCreatePassport_1() throws Exception {

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").templateKeyname("ENERGY_DECLARATION_1").build();

        RequestExtraInitializer param = (b) -> {
//            b.param("templateKeyname", energyPassportTemplate.getKeyname());
        };

        ResultActions resultActions = _testPostJson("/api/subscr/energy-passports", vm, param, ResultActionsTester.TEST_SUCCESSFULL);
    }

    @Test
    @Transactional
    public void testCreatePassport_2() throws Exception {

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").templateKeyname("ENERGY_DECLARATION_2").build();

        RequestExtraInitializer param = (b) -> {
//            b.param("templateKeyname", energyPassportTemplate.getKeyname());
        };

        ResultActions resultActions = _testPostJson("/api/subscr/energy-passports", vm, param, ResultActionsTester.TEST_SUCCESSFULL);
    }

    @Test
    @Transactional
    public void testCreatePassportExisting() throws Exception {

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").build();

        RequestExtraInitializer param = (b) -> {
            b.param("templateKeyname", EnergyPassport401_2014_Add.ENERGY_DECLARATION);
        };
        ResultActions resultActions = _testPostJson("/api/subscr/energy-passports", vm, param, ResultActionsTester.TEST_SUCCESSFULL);
    }

    @Test
    @Transactional
    public void testGetPassport() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId());
    }

    @Test
    @Transactional
    public void testUpdatePassport() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        EnergyPassportVM vm = new EnergyPassportVM();
        vm.setId(passportDTO.getId());
        vm.setPassportName("Passport");
        vm.setDescription("Description");
        vm.setPassportDate(LocalDate.now());

        _testUpdateJson("/api/subscr/energy-passports/" + passportDTO.getId(), vm);

        EnergyPassport energyPassport = energyPassportRepository.findOne(passportDTO.getId());
        Assert.assertEquals(vm.getDescription(), energyPassport.getDescription());
        Assert.assertEquals(vm.getPassportName(), energyPassport.getPassportName());
        Assert.assertEquals(vm.getPassportDate(), energyPassport.getPassportDate());

    }

    @Test
    @Transactional
    public void testGetPassportData() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data");
    }

    @Test
    @Transactional
    public void testGetPassportDataEnhanced() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());
        energyPassportService.savePassportData(passportDataDTOList.get(0));

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data");
    }

    @Test
    @Transactional
    public void testGetPassportDataSection() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());
        energyPassportService.savePassportData(passportDataDTOList.get(0));

        RequestExtraInitializer params = (b) -> {
            b.param("sectionId", passportDTO.getSections().get(0).getId().toString());
        };

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data", params);
    }

    @Test
    @Transactional
    public void testUpdatePassportData() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());

        passportDataDTOList.forEach((i) -> {
            try {
                _testUpdateJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data", i);
                List<EnergyPassportDataValue> sectionValues = energyPassportDataValueRepository.findByPassportIdAndSectionKey(passportDTO.getId(), i.getSectionKey());
                Assert.assertTrue(sectionValues.size() > 0);
            } catch (Exception e) {
                Assert.fail();
            }
        });

        List<EnergyPassportDataValue> values = energyPassportDataValueRepository.findByPassportId(passportDTO.getId());
        Assert.assertTrue(values.size() > 0);
    }

    @Test
    @Transactional
    public void testGetPassports() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION,
            EnergyPassportVM.builder().passportName("passport # 1").build(), new Subscriber().id(getSubscriberId()));

        EnergyPassportDTO passportDTOs = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION,
            EnergyPassportVM.builder().passportName("passport # 2").build(), new Subscriber().id(getSubscriberId()));

        energyPassportRepository.flush();

        List<?> checkList = energyPassportService.findBySubscriberId(getSubscriberId());
        Assert.assertTrue(checkList.size() > 0);

        _testGetJson("/api/subscr/energy-passports");
    }


    @Test
    @Transactional
    public void testDeletePassport() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        _testDeleteJson("/api/subscr/energy-passports/" + passportDTO.getId());

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
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        passportDTO.getSections().forEach((i) -> log.debug("section:{}", i));

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        //Optional<EnergyPassportSectionDTO> sectionDTO = Optional.of(passportDTO.getSections().get(0));
        Assert.assertTrue(sectionDTO.isPresent());

        for (int i = 0; i < 2; i++) {
            energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), i));
        }

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() +
            "/section/" + sectionDTO.get().getId() + "/entries");
    }


    @Test
    @Transactional
    public void testPassportSectionEntryUpdate() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
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

        _testUpdateJson("/api/subscr/energy-passports/" + passportDTO.getId() +
            "/section/" + sectionDTO.get().getId() + "/entries", entryDto);

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() +
            "/section/" + sectionDTO.get().getId() + "/entries");

        int checkSize = energyPassportService.findSectionEntries(sectionDTO.get().getId()).size();
        Assert.assertTrue(checkSize == 3);
    }

    @Test
    @Transactional
    public void testPassportWithSectionEntriesGet() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());

        for (int i = 0; i < 2; i++) {
            energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), i));
        }

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId());
    }

    @Test
    @Transactional
    public void testPassportWithSectionEntriesData() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());
        EnergyPassportSectionEntryDTO entryDto = energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), 1));
        Assert.assertNotNull(entryDto.getId());

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());
        Optional<EnergyPassportDataDTO> sectionEntryDataDto = passportDataDTOList.stream().filter((i) -> i.getSectionKey().equals(sectionDTO.get().getSectionKey())).findFirst();
        Assert.assertTrue(sectionEntryDataDto.isPresent());
        sectionEntryDataDto.get().setSectionEntryId(entryDto.getId());

        _testUpdateJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data", sectionEntryDataDto.get());


        List<EnergyPassportData> data = energyPassportDataRepository.findByPassportIdAndSectionEntry(passportDTO.getId(), sectionDTO.get().getSectionKey(), entryDto.getId());
        Assert.assertTrue(data.size() == 1);
        Assert.assertTrue(data.get(0).getSectionEntryId() != null);

        List<EnergyPassportDataValue> dataValues = energyPassportDataValueRepository.findByPassportIdAndSectionKey(passportDTO.getId(), sectionDTO.get().getSectionKey(), entryDto.getId());
        dataValues.forEach((i) -> Assert.assertTrue(i.getEnergyPassportDataId() != null));

        RequestExtraInitializer params = (b) -> {
            b.param("sectionId", data.get(0).getSectionId().toString());
            b.param("sectionEntryId", data.get(0).getSectionEntryId().toString());
        };

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data", params);


    }

    @Test
    @Transactional
    public void testPassportSectionEntryDelete() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        passportDTO.getSections().forEach((i) -> log.debug("section:{}", i));

        Optional<EnergyPassportSectionDTO> sectionDTO = passportDTO.getSections().stream().filter(EnergyPassportSectionDTO::isHasEntries).findFirst();
        Assert.assertTrue(sectionDTO.isPresent());

        EnergyPassportSectionEntryDTO entryDto = null;
        entryDto = energyPassportService.saveSectionEntry(createSectionEntryDTO(sectionDTO.get().getId(), 1));

        _testDeleteJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/section/"+ sectionDTO.get().getId() +
            "/entries/" + entryDto.getId());
    }


    @Test
    @Transactional
    public void testPassportContObjects() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014_Add.ENERGY_DECLARATION, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();


        List<Long> contObjectIds = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());
        energyPassportService.linkEnergyPassportToContObjects(passportDTO.getId(), contObjectIds, new Subscriber().id(getSubscriberId()));

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/contObjectIds");

        energyPassportService.linkEnergyPassportToContObjects(passportDTO.getId(), contObjectIds, new Subscriber().id(getSubscriberId()));
        List<Long> linkedContObjectIds = energyPassportService.findEnergyPassportContObjectIds(passportDTO.getId());

        contObjectIds.sort(Comparator.comparingLong(Long::longValue));

        Assert.assertEquals(contObjectIds, linkedContObjectIds);
    }

    @Test
    @Transactional
    public void testGetContObjectEnergyPassport() throws Exception {

        List<Long> contObjectIds = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());

        EnergyPassportVM vm = new EnergyPassportVM();
        vm.setPassportName("Hallo - 1");

        energyPassportService.createContObjectPassport(vm, contObjectIds.subList(0,1), new Subscriber().id(getSubscriberId()));

        energyPassportDataRepository.flush();

        _testGetJson("/api/subscr/energy-passports/cont-objects/" + contObjectIds.get(0));
    }

    @Test
    @Transactional
    public void testCreateContObjectEnergyPassport() throws Exception {

        List<Long> contObjectIds = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").build();

        RequestExtraInitializer param = (b) -> {
        };

        ResultActions resultActions = _testPostJson("/api/subscr/energy-passports/cont-objects/" + contObjectIds.get(0), vm, param, ResultActionsTester.TEST_SUCCESSFULL);

    }

    @Test
    @Transactional
    public void testUpdateContObjectEnergyPassport() throws Exception {

        List<Long> contObjectIds = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").passportDate(LocalDate.now()).build();
        EnergyPassportDTO dto = energyPassportService.createContObjectPassport(vm, contObjectIds.subList(0,1), new Subscriber().id(getSubscriberId()));

        energyPassportDataRepository.flush();

        vm = EnergyPassportVM.builder().passportName("bla-bla-bla").passportDate(LocalDate.now()).id(dto.getId()).build();

        RequestExtraInitializer param = (b) -> {
        };

        _testUpdateJson("/api/subscr/energy-passports/cont-objects/" + contObjectIds.get(0), vm, param, ResultActionsTester.TEST_SUCCESSFULL);

    }

}
