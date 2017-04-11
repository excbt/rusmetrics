package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportTemplateService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

import java.util.List;

/**
 * Created by kovtonyk on 30.03.2017.
 */
@Controller
@RequestMapping(value = "/api/energy-passport-templates")
public class EnergyPassportTemplateResource extends SubscrApiController {

    private final EnergyPassportTemplateService energyPassportTemplateService;

    public EnergyPassportTemplateResource(EnergyPassportTemplateService energyPassportTemplateService) {
        this.energyPassportTemplateService = energyPassportTemplateService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET,
    			produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassportTemplates() {
    	List<EnergyPassportTemplateDTO> resultList = energyPassportTemplateService.findAllTemplates();
    		return responseOK(resultList);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
    			produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassportTemplate(@PathVariable("id") Long id) {
    	EnergyPassportTemplateDTO result = energyPassportTemplateService.findOneTemplate(id);
    	return result != null ? responseOK(result) : responseNoContent();
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassportTemplateNew() {
        EnergyPassportTemplateDTO templateDTO = energyPassportTemplateService.createNewDTO_401();
        return responseOK(templateDTO);
    }

    @RequestMapping(value = "/newData", method = RequestMethod.GET,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassportTemplateValues() {
        List<EnergyPassportDataDTO> dataDTOs = energyPassportTemplateService.createNewData();
        return responseOK(dataDTOs);
    }


}
