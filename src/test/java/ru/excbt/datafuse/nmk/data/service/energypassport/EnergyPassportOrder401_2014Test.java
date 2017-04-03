package ru.excbt.datafuse.nmk.data.service.energypassport;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;

/**
 * Created by kovtonyk on 03.04.2017.
 */
public class EnergyPassportOrder401_2014Test {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportOrder401_2014Test.class);

    private EnergyPassportOrder401_2014 energyPassportOrder401_2014;

    @Before
    public void setUp() throws Exception {
        energyPassportOrder401_2014 = new EnergyPassportOrder401_2014();
    }


    @Test
    public void testS_M1() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_S_M1();
        EnergyPassportSectionTemplate template = factory.createSection();
        log.info("Json:\n{}", template.getSectionJson());

    }

    @Test
    public void testS_M2() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_S_M2();
        EnergyPassportSectionTemplate template = factory.createSection();
        log.info("Json:\n{}", template.getSectionJson());
    }

    @Test
    public void testS_M3() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_S_M3();
        EnergyPassportSectionTemplate template = factory.createSection();

        log.info("Json:\n{}", template.getSectionJson());
    }

    @Test
    public void testS_Main() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_Main();
        EnergyPassportSectionTemplate template = factory.createSection();

        log.info("Json:\n{}", template.getSectionJson());
    }
}
