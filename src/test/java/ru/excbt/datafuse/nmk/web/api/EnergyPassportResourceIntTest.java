package ru.excbt.datafuse.nmk.web.api;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportDataValue;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportDataValueRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportService;
import ru.excbt.datafuse.nmk.data.service.energypassport.EnergyPassport401_2014;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.ResultActionsTester;

import java.util.List;

/**
 * Created by kovtonyk on 10.04.2017.
 */
public class EnergyPassportResourceIntTest extends AnyControllerTest {

    public static final String DUMMY_JSON = "{\"a\":123}";

    @Autowired
    private EnergyPassportService energyPassportService;

    @Autowired
    private EnergyPassportRepository energyPassportRepository;

    @Autowired
    private EnergyPassportTemplateRepository energyPassportTemplateRepository;

    @Autowired
    private EnergyPassportDataValueRepository energyPassportDataValueRepository;


    @Test
    @Transactional
    public void testCreatePassport() throws Exception {

        EnergyPassportTemplate energyPassportTemplate = EnergyPassportTemplateResourceIntTest.createEnergyPassportTemplate();
        energyPassportTemplate.createSection((s) -> s.sectionKey("P_1.1").sectionOrder(1).sectionJson(DUMMY_JSON));
        energyPassportTemplateRepository.saveAndFlush(energyPassportTemplate);

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").build();

        RequestExtraInitializer param = (b) -> {
            b.param("templateKeyname", energyPassportTemplate.getKeyname());
        };

        ResultActions resultActions = _testPostJson("/api/subscr/energy-passports", vm, param, ResultActionsTester.TEST_SUCCESSFULL);
    }

    @Test
    @Transactional
    public void testCreatePassportExisting() throws Exception {

        EnergyPassportVM vm = EnergyPassportVM.builder().passportName("bla-bla-bla").build();

        RequestExtraInitializer param = (b) -> {
            b.param("templateKeyname", EnergyPassport401_2014.ENERGY_PASSPORT);
        };
        ResultActions resultActions = _testPostJson("/api/subscr/energy-passports", vm, param, ResultActionsTester.TEST_SUCCESSFULL);
    }

    @Test
    @Transactional
    public void testGetPassport() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId());
    }

    @Test
    @Transactional
    public void testGetPassportData() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data");
    }

    @Test
    @Transactional
    public void testGetPassportDataEnhanced() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());
        energyPassportService.savePassportData(passportDataDTOList.get(0));

        _testGetJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data");
    }

    @Test
    @Transactional
    public void testGetPassportDataSection() throws Exception {

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT, new Subscriber().id(getSubscriberId()));
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

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        List<EnergyPassportDataDTO> passportDataDTOList = energyPassportService.extractEnergyPassportData(passportDTO.getId());

        passportDataDTOList.forEach((i) -> {
            try {
                _testUpdateJson("/api/subscr/energy-passports/" + passportDTO.getId() + "/data",i);
                List<EnergyPassportDataValue> sectionValues = energyPassportDataValueRepository.findByPassportIdAndSectionKey (passportDTO.getId(), i.getSectionKey());
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

        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT,
            EnergyPassportVM.builder().passportName("passport # 1").build(), new Subscriber().id(getSubscriberId()));

        EnergyPassportDTO passportDTOs = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT,
            EnergyPassportVM.builder().passportName("passport # 2").build(), new Subscriber().id(getSubscriberId()));

        energyPassportRepository.flush();

        List<?> checkList = energyPassportService.findBySubscriberId(getSubscriberId());
        Assert.assertTrue(checkList.size() > 0);

        _testGetJson("/api/subscr/energy-passports");
    }


    @Test
    @Transactional
    public void testDeletePassport() throws Exception {
        EnergyPassportDTO passportDTO = energyPassportService.createPassport(EnergyPassport401_2014.ENERGY_PASSPORT, new Subscriber().id(getSubscriberId()));
        energyPassportRepository.flush();

        _testDeleteJson("/api/subscr/energy-passports/" + passportDTO.getId());

    }




}
