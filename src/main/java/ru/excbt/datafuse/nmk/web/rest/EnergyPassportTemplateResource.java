package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportTemplateService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;

/**
 * Created by kovtonyk on 30.03.2017.
 */
@RestController
@RequestMapping(value = "/api/energy-passport-templates")
public class EnergyPassportTemplateResource extends AbstractSubscrApiResource {

    private final EnergyPassportTemplateService energyPassportTemplateService;

    public EnergyPassportTemplateResource(EnergyPassportTemplateService energyPassportTemplateService) {
        this.energyPassportTemplateService = energyPassportTemplateService;
    }

    @GetMapping
//    @RequestMapping(value = "", method = RequestMethod.GET,
//    			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getEnergyPassportTemplates() {
    	List<EnergyPassportTemplateDTO> resultList = energyPassportTemplateService.findAllTemplates();
    		return ApiResponse.responseOK(resultList);
    }

    @GetMapping("/{id}")
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
//    			produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getEnergyPassportTemplate(@PathVariable("id") Long id) {
    	EnergyPassportTemplateDTO result = energyPassportTemplateService.findOneTemplate(id);
    	return result != null ? ApiResponse.responseOK(result) : ApiResponse.responseNoContent();
    }

    @GetMapping("/new")
//    @RequestMapping(value = "/new", method = RequestMethod.GET,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getEnergyPassportTemplateNew() {
        EnergyPassportTemplateDTO templateDTO = energyPassportTemplateService.createNewDTO_401();
        return ApiResponse.responseOK(templateDTO);
    }

    @GetMapping("/newData")
//    @RequestMapping(value = "/newData", method = RequestMethod.GET,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    @Timed
    public ResponseEntity<?> getEnergyPassportTemplateValues() {
        List<EnergyPassportDataDTO> dataDTOs = energyPassportTemplateService.createNewData();
        return ApiResponse.responseOK(dataDTOs);
    }


}
