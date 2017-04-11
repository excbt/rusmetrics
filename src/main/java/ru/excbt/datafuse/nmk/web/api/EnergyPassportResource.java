package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportService;
import ru.excbt.datafuse.nmk.data.service.energypassport.EnergyPassport401_2014;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Controller
@RequestMapping(value = "/api/subscr/energy-passport")
public class EnergyPassportResource extends SubscrApiController {

    private final EnergyPassportService energyPassportService;

    public EnergyPassportResource(EnergyPassportService energyPassportService) {
        this.energyPassportService = energyPassportService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> createEnergyPassport(@RequestParam(name = "templateKeyname", required = false) String templateKeyname,
                                                  @RequestBody(required = false) EnergyPassportVM energyPassportVM) {

        String keyname = templateKeyname != null ? templateKeyname : EnergyPassport401_2014.ENERGY_PASSPORT;
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.createPassport(keyname, energyPassportVM, getCurrentSubscriber());
        return responseOK(action);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> createEnergyPassport(@PathVariable("id") Long id) {
        EnergyPassportDTO result = energyPassportService.find(id);
        return responseOK(result);
    }
}
