package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.energypassport.EnergyPassport401_2014_Add;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionEntryDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportShortDTO;
import ru.excbt.datafuse.nmk.data.model.vm.EnergyPassportVM;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportService;
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
@Controller
@RequestMapping(value = "/api/subscr/energy-passports")
public class EnergyPassportResource extends AbstractSubscrApiResource {

    private final EnergyPassportService energyPassportService;

    public EnergyPassportResource(EnergyPassportService energyPassportService) {
        this.energyPassportService = energyPassportService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> createEnergyPassport(@RequestParam(name = "templateKeyname", required = false) String templateKeyname,
                                                  @RequestBody(required = false) EnergyPassportVM energyPassportVM) {

        if (templateKeyname == null) {
            templateKeyname = energyPassportVM.getTemplateKeyname();
        }
        String keyname = templateKeyname != null ? templateKeyname : EnergyPassport401_2014_Add.ENERGY_DECLARATION;
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.createPassport(keyname, energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseOK(action);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updateEnergyPassport(@PathVariable("id") Long id, @RequestBody EnergyPassportVM energyPassportVM) {
        if (!id.equals(energyPassportVM.getId())) {
            return ApiResponse.responseBadRequest();
        }
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.updatePassport(energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseOK(action);
    }

    @RequestMapping(method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updateEnergyPassport(@RequestBody EnergyPassportVM energyPassportVM) {
        if (energyPassportVM.getId() == null) {
            return ApiResponse.responseBadRequest();
        }
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.updatePassport(energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseOK(action);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassport(@PathVariable("id") Long id) {
        EnergyPassportDTO result = energyPassportService.find(id);
        return ApiResponse.responseOK(result);
    }

    @RequestMapping(value = "", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getEnergyPassports() {
        List<EnergyPassportShortDTO> result = energyPassportService.findShortBySubscriberId(getSubscriberId());
        return ApiResponse.responseOK(result);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> deleteEnergyPassport(@PathVariable("id") Long id) {
        ApiActionVoidProcess process = () -> energyPassportService.delete(id, getCurrentSubscriber());
        return ApiResponse.responseOK(process);
    }

    @RequestMapping(value = "/{id}/data", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getPassportSectionsData(@PathVariable("id") Long passportId,
                                                     @RequestParam(name = "sectionId", required = false) Long sectionId,
                                                     @RequestParam(name = "sectionEntryId", required = false) Long sectionEntryId) {
        List<EnergyPassportDataDTO> result;
        if (sectionId == null) {
            result = energyPassportService.findPassportData(passportId);
        } else {
            result = energyPassportService.findPassportData(passportId, sectionId, sectionEntryId);
        }
        return ApiResponse.responseOK(result);
    }


    @RequestMapping(value = "/{id}/data", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updatePassportSectionData(@PathVariable("id") Long passportId,
                                                       @RequestBody @Valid EnergyPassportDataDTO energyPassportDataDTO) {
        energyPassportDataDTO.setPassportId(passportId);

        if (!energyPassportService.validatePassportData(energyPassportDataDTO)) {
            return ApiResponse.responseBadRequest();
        }

        ApiActionProcess<EnergyPassportDataDTO> process = () -> energyPassportService.savePassportData(energyPassportDataDTO);

        return ApiResponse.responseOK(process);
    }


    @RequestMapping(value = "/{id}/section/{sectionId}/entries", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getPassportSectionsEntries(@PathVariable("id") Long passportId,
                                                        @PathVariable(name = "sectionId") Long sectionId) {
        List<EnergyPassportSectionEntryDTO> entries = energyPassportService.findSectionEntries(sectionId);
        return ApiResponse.responseOK(entries);
    }

    @RequestMapping(value = "/{id}/section/{sectionId}/entries", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updatePassportSectionsEntries(@PathVariable("id") Long passportId,
                                                           @PathVariable(name = "sectionId") Long sectionId,
                                                           @RequestBody @Valid EnergyPassportSectionEntryDTO entryDTO) {

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
    @RequestMapping(value = "/{id}/section/{sectionId}/entries/{entryId}", method = RequestMethod.DELETE,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> deletePassportSectionsEntry(@PathVariable("id") Long passportId,
                                                          @PathVariable(name = "sectionId") Long sectionId,
                                                          @PathVariable(name = "entryId") Long entryId) {
        ApiActionVoidProcess process = () -> energyPassportService.deleteSectionEntry(passportId, sectionId, entryId, getCurrentSubscriber());
        return ApiResponse.responseOK(process);
    }


    /**
     *
     * @param passportId
     * @return
     */
    @RequestMapping(value = "/{id}/cont-object-ids", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getPassportContObjects(@PathVariable("id") Long passportId) {

        return ApiResponse.responseOK(() -> energyPassportService.findEnergyPassportContObjectIds(passportId));
    }

    /**
     *
     * @param passportId
     * @param contObjectIds
     * @return
     */
    @RequestMapping(value = "/{id}/cont-object-ids", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> savePassportContObjects(@PathVariable("id") Long passportId, @RequestBody List<Long> contObjectIds) {
        return ApiResponse.responseUpdate(() ->
            energyPassportService.linkEnergyPassportToContObjects(passportId, contObjectIds, getCurrentSubscriber()));
    }


    /**
     *
     * @param contObjectId
     * @return
     */
    @RequestMapping(value = "/cont-objects/{contObjectId}", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getContObjectPassport(@PathVariable("contObjectId") Long contObjectId) {

        return ApiResponse.responseOK(() -> energyPassportService.findContObjectEnergyPassport(contObjectId));
    }


    /**
     *
     * @param contObjectId
     * @param energyPassportVM
     * @return
     */
    @RequestMapping(value = "/cont-objects/{contObjectId}", method = RequestMethod.POST,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> createContObjectEnergyPassport(@PathVariable("contObjectId") Long contObjectId,
        @RequestBody(required = false) EnergyPassportVM energyPassportVM) {
        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.createContObjectPassport(energyPassportVM, Arrays.asList(contObjectId), getCurrentSubscriber());
        return ApiResponse.responseUpdate(action);
    }


    /**
     *
     * @param contObjectId
     * @param energyPassportVM
     * @return
     */
    @RequestMapping(value = "/cont-objects/{contObjectId}", method = RequestMethod.PUT,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> updateContObjectEnergyPassport(@PathVariable("contObjectId") Long contObjectId,
        @RequestBody(required = false) EnergyPassportVM energyPassportVM) {

        if (energyPassportVM.getId() == null) {
            return createContObjectEnergyPassport(contObjectId, energyPassportVM);
        }

        ApiActionProcess<EnergyPassportDTO> action = () -> energyPassportService.updatePassport(energyPassportVM, getCurrentSubscriber());
        return ApiResponse.responseUpdate(action);
    }



}
