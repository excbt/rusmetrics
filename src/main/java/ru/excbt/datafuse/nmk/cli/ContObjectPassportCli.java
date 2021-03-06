package ru.excbt.datafuse.nmk.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import ru.excbt.datafuse.nmk.config.DefaultProfileUtil;
import ru.excbt.datafuse.nmk.data.energypassport.EnergyPassport401_2014_Add;
import ru.excbt.datafuse.nmk.data.energypassport.EnergyPassport_X;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportService;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportTemplateService;
import ru.excbt.datafuse.nmk.data.service.MockUserService;

import java.io.IOException;

/**
 * Created by kovtonyk on 11.04.2017.
 */
@ComponentScan(basePackageClasses = {ReportMasterTemplateCli.class})
    public class ContObjectPassportCli extends PortalToolCli {

    private static final Logger log = LoggerFactory.getLogger(ContObjectPassportCli.class);

    @Autowired
    private EnergyPassportService energyPassportService;

    @Autowired
    private MockUserService mockUserService;

    @Autowired
    private EnergyPassportTemplateService energyPassportTemplateService;

    private final EnergyPassport401_2014_Add energyPassport401_2014_Add;

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(ContObjectPassportCli.class);
        DefaultProfileUtil.addDefaultProfile(app);
        app.setWebEnvironment(false);
        Environment env = app.run(args).getEnvironment();

    }


    public ContObjectPassportCli() {
        energyPassport401_2014_Add = new EnergyPassport401_2014_Add();
    }



    @Override
    protected void doWork(){
        mockUserService.setMockUserId(1000L); // User "SYSTEM"
        EnergyPassportTemplateDTO energyPassportTemplateDTO = energyPassportTemplateService.createNewDTO_XXX();
        energyPassportTemplateDTO.setKeyname(EnergyPassport_X.ENERGY_PASSPORT_X);
        EnergyPassportTemplateDTO resultPassportTemplateDTO = energyPassportTemplateService.saveEnergyPassportTemplate(energyPassportTemplateDTO);
        log.info("PassportTemplateId = {}", resultPassportTemplateDTO.getId());
        energyPassportService.updateExistingEnergyPassportsFromTemplate(resultPassportTemplateDTO.getId());
    }

    @Override
    protected String beginMessage() {
        return "UPDATING ENERGY PASSPORT";
    }

    @Override
    protected String completeMessage() {
        return "ENERGY PASSPORT UPDATED";
    }


}
