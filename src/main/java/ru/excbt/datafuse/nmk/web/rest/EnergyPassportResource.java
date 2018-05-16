package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Metric;
import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.energypassport.EnergyPassport401_2014_Add;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionEntryDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportShortDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@RestController
@RequestMapping(value = "/api/subscr/energy-passports")
public class EnergyPassportResource {

    private final EnergyPassportService energyPassportService;

    private final PortalUserIdsService portalUserIdsService;

    @Autowired
    public EnergyPassportResource(EnergyPassportService energyPassportService, PortalUserIdsService portalUserIdsService) {
        this.energyPassportService = energyPassportService;
        this.portalUserIdsService = portalUserIdsService;
    }

    private Subscriber getCurrentSubscriber() {
        return new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId());
    }

    private Long getSubscriberId() {
        return portalUserIdsService.getCurrentIds().getSubscriberId();
    }


    @PostMapping
    @ApiOperation(value = "Create Energy Passport")
    @Timed
//    @RequestMapping(value = "", method = RequestMethod.POST,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> createEnergyPassport(@ApiParam @RequestParam(name = "templateKeyname", required = false) String templateKeyname,
                                                  @ApiParam @RequestBody(required = false) EnergyPassportVM energyPassportVM) {

        if (templateKeyname == null) {
            templateKeyname = energyPassportVM.getTemplateKeyname();
        }
        String keyname = templateKeyname != null ? templateKeyname : EnergyPassport401_2014_Add.ENERGY_DECLARATION_1;
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.createPassport(keyname, energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseOK(action);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Update Energy passport by id")
    @Timed
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updateEnergyPassport(@ApiParam(value = "id of energy passport") @PathVariable("id") Long id, @RequestBody EnergyPassportVM energyPassportVM) {
        if (!id.equals(energyPassportVM.getId())) {
            return ApiResponse.responseBadRequest();
        }
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.updatePassport(energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseOK(action);
    }

    @PutMapping
    @ApiOperation(value = "Update Energy Passport by VM")
    @Timed
//    @RequestMapping(method = RequestMethod.PUT,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updateEnergyPassport(@ApiParam @RequestBody EnergyPassportVM energyPassportVM) {
        if (energyPassportVM.getId() == null) {
            return ApiResponse.responseBadRequest();
        }
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.updatePassport(energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseOK(action);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get one Energy Passport")
    @Timed
//    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassport(@ApiParam @PathVariable("id") Long id) {
        EnergyPassportDTO result = energyPassportService.find(id);
        return ApiResponse.responseOK(result);
    }

    @GetMapping()
    @ApiOperation(value = "Get all Energy Passports")
    @Timed
//    @RequestMapping(value = "", method = RequestMethod.GET,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassports() {
        List<EnergyPassportShortDTO> result = energyPassportService.findShortBySubscriberId(getSubscriberId());
        return ApiResponse.responseOK(result);
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete Energy Passport")
    @Timed
//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> deleteEnergyPassport(@ApiParam @PathVariable("id") Long id) {
        ApiActionVoidProcess process = () -> energyPassportService.delete(id, getCurrentSubscriber());
        return ApiResponse.responseOK(process);
    }

    @GetMapping("/{id}/data")
    @ApiOperation(value = "Get Energy Passport data")
    @Timed
//    @RequestMapping(value = "/{id}/data", method = RequestMethod.GET,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getPassportSectionsData(@ApiParam @PathVariable("id") Long passportId,
                                                     @ApiParam (value = "id of section") @RequestParam(name = "sectionId", required = false) Long sectionId,
                                                     @ApiParam @RequestParam(name = "entry id of section", required = false) Long sectionEntryId) {
        List<EnergyPassportDataDTO> result;
        if (sectionId == null) {
            result = energyPassportService.findPassportData(passportId);
        } else {
            result = energyPassportService.findPassportData(passportId, sectionId, sectionEntryId);
        }
        return ApiResponse.responseOK(result);
    }

    @PutMapping("/{id}/data")
    @ApiOperation(value = "Update Energy Passport data")
    @Timed
//    @RequestMapping(value = "/{id}/data", method = RequestMethod.PUT,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updatePassportSectionData(@ApiParam @PathVariable("id") Long passportId,
                                                       @ApiParam @RequestBody @Valid EnergyPassportDataDTO energyPassportDataDTO) {
        energyPassportDataDTO.setPassportId(passportId);

        if (!energyPassportService.validatePassportData(energyPassportDataDTO)) {
            return ApiResponse.responseBadRequest();
        }

        ApiActionProcess<EnergyPassportDataDTO> process = () -> energyPassportService.savePassportData(energyPassportDataDTO);

        return ApiResponse.responseOK(process);
    }

    @GetMapping("/{id}/section/{sectionId}/entries")
    @ApiOperation(value = "Get Energy Passport Entries")
    @Timed
//    @RequestMapping(value = "/{id}/section/{sectionId}/entries", method = RequestMethod.GET,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getPassportSectionsEntries(@ApiParam @PathVariable("id") Long passportId,
                                                        @ApiParam @PathVariable(name = "sectionId") Long sectionId) {
        List<EnergyPassportSectionEntryDTO> entries = energyPassportService.findSectionEntries(sectionId);
        return ApiResponse.responseOK(entries);
    }

    @PutMapping("/{id}/section/{sectionId}/entries")
    @ApiOperation(value = "Update Energy Passport Entries")
    @Timed
//    @RequestMapping(value = "/{id}/section/{sectionId}/entries", method = RequestMethod.PUT,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updatePassportSectionsEntries(@ApiParam @PathVariable("id") Long passportId,
                                                           @ApiParam @PathVariable(name = "sectionId") Long sectionId,
                                                           @ApiParam @RequestBody @Valid EnergyPassportSectionEntryDTO entryDTO) {

        if (!sectionId.equals(entryDTO.getSectionId())) {
            return ApiResponse.responseBadRequest();
        }

        ApiActionProcess<EnergyPassportSectionEntryDTO> process = () -> energyPassportService.saveSectionEntry(entryDTO);
        return ApiResponse.responseUpdate(process);
    }


    /**
     *
     * @param passportId
     * @param sectionId
     * @param entryId
     * @return
     */
    @DeleteMapping("/{id}/section/{sectionId}/entries/{entryId}")
    @ApiOperation(value = "Delete Energy Passport Entry")
    @Timed
//    @RequestMapping(value = "/{id}/section/{sectionId}/entries/{entryId}", method = RequestMethod.DELETE,
//        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> deletePassportSectionsEntry(@ApiParam @PathVariable("id") Long passportId,
                                                         @ApiParam("id of section") @PathVariable(name = "sectionId") Long sectionId,
                                                         @ApiParam("id of entry") @PathVariable(name = "entryId") Long entryId) {
        ApiActionVoidProcess process = () -> energyPassportService.deleteSectionEntry(passportId, sectionId, entryId, getCurrentSubscriber());
        return ApiResponse.responseOK(process);
    }


    /**
     *
     * @param passportId
     * @return
     */
    @Timed
    @ApiOperation(value = "Get ids of Cont Objects that linked with passport")
    @RequestMapping(value = "/{id}/cont-object-ids", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getPassportContObjects(@ApiParam("id of passport") @PathVariable("id") Long passportId) {

        return ApiResponse.responseOK(() -> energyPassportService.findEnergyPassportContObjectIds(passportId));
    }

    /**
     *
     * @param passportId
     * @param contObjectIds
     * @return
     */
    @Timed
    @ApiOperation(value = "Update ids of Cont Objects that linked with passport")
    @RequestMapping(value = "/{id}/cont-object-ids", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> savePassportContObjects(@ApiParam("id of Energy Passport") @PathVariable("id") Long passportId,
                                                     @ApiParam("list of contObject ids") @RequestBody List<Long> contObjectIds) {
        return ApiResponse.responseUpdate(() ->
            energyPassportService.linkEnergyPassportToContObjects(passportId, contObjectIds, getCurrentSubscriber()));
    }


    /**
     *
     * @param contObjectId
     * @return
     */
    @Timed
    @ApiOperation(value = "Get Energy Passport of Cont Object")
    @RequestMapping(value = "/cont-objects/{contObjectId}", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getContObjectPassport(@ApiParam("id of Cont Object") @PathVariable("contObjectId") Long contObjectId) {

        return ApiResponse.responseOK(() -> energyPassportService.findContObjectEnergyPassport(contObjectId));
    }


    /**
     *
     * @param contObjectId
     * @param energyPassportVM
     * @return
     */
    @Timed
    @ApiOperation(value = "Create Energy Passport of Cont Object")
    @RequestMapping(value = "/cont-objects/{contObjectId}", method = RequestMethod.POST,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> createContObjectEnergyPassport(@ApiParam("id of Cont Object") @PathVariable("contObjectId") Long contObjectId,
                                                            @ApiParam @RequestBody(required = false) EnergyPassportVM energyPassportVM) {
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.createContObjectPassport(energyPassportVM, Arrays.asList(contObjectId), getCurrentSubscriber());
        return ApiResponse.responseUpdate(action);
    }


    /**
     *
     * @param contObjectId
     * @param energyPassportVM
     * @return
     */
    @Timed
    @ApiOperation(value = "Update Energy Passport of Cont Object")
    @RequestMapping(value = "/cont-objects/{contObjectId}", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updateContObjectEnergyPassport(@ApiParam("id of ContObject") @PathVariable("contObjectId") Long contObjectId,
                                                            @ApiParam @RequestBody(required = false) EnergyPassportVM energyPassportVM) {

        if (energyPassportVM.getId() == null) {
            return createContObjectEnergyPassport(contObjectId, energyPassportVM);
        }

        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.updatePassport(energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseUpdate(action);
    }



}
