package ru.excbt.datafuse.nmk.web.api;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.rest.SubscrContZPointResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

/**
 * Контроллер для работы с точками учета для РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
@Controller
@RequestMapping(value = "/api/rma")
public class RmaContZPointResource extends SubscrContZPointResource {

	private static final Logger logger = LoggerFactory.getLogger(RmaContZPointResource.class);

	private final SubscriberAccessService subscriberAccessService;

    public RmaContZPointResource(ContZPointService contZPointService, ContServiceDataHWaterService contServiceDataHWaterService, ContServiceDataElService contServiceDataElService, ContZPointMetadataService contZPointMetadataService, MeasureUnitService measureUnitService, OrganizationService organizationService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService, SubscriberAccessService subscriberAccessService) {
        super(contZPointService, contServiceDataHWaterService, contServiceDataElService, contZPointMetadataService, measureUnitService, organizationService, objectAccessService, portalUserIdsService);
        this.subscriberAccessService = subscriberAccessService;
    }

    /**
	 * // TODO change ContZPointFullVM to ContZPointDTO
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody ContZPointFullVM contZPointFullVM) {

        Objects.requireNonNull(contObjectId);
        Objects.requireNonNull(contZPointId);
        Objects.requireNonNull(contZPointFullVM);

		contZPointFullVM.setContObjectId(contObjectId);

        if (!objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds())) {
            ApiResponse.responseForbidden();
        }

		return ApiResponse.responseUpdate(() -> {
            ContZPointFullVM result = contZPointService.updateVM(contZPointFullVM, contZPointTagSaver(contZPointFullVM.getTagNames()));
            return result;
        });

	}


	/**
	 *
	 * @param contObjectId
	 * @param contZPointDTO
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.POST,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContZPoint(@PathVariable("contObjectId") Long contObjectId,
                                              @RequestBody ContZPointDTO contZPointDTO, HttpServletRequest request) {

        Objects.requireNonNull(contObjectId);
        Objects.requireNonNull(contZPointDTO);
        contZPointDTO.setContObjectId(contObjectId);

        Consumer<ContZPointFullVM> contZPointGrantor = (vm) -> subscriberAccessService.grantContZPointAccess(
            new ContZPoint().id(vm.getId()),
            new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));

//        Consumer<ContZPointFullVM> contZPointTagSaver = (vm) -> {
//            Set<String> tagNames = contZPointService.saveContZPointTags(
//                new ContZPoint().id(vm.getId()),
//                contZPointDTO.getTagNames(),
//                portalUserIdsService.getCurrentIds());
//            vm.setTagNames(tagNames);
//            };


        return ApiResponse.responseCreate(() -> {
                ContZPointFullVM contZPointFullVM = contZPointService.createZPointDTO2FullVM(contZPointDTO,
                    contZPointGrantor.andThen(contZPointTagSaver(contZPointDTO.getTagNames())));
                return contZPointFullVM;
            },
            () -> request.getRequestURI());
	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

        Objects.requireNonNull(contObjectId);
        Objects.requireNonNull(contZPointId);

        if (!objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds())) {
            ApiResponse.responseForbidden();
        }
		ApiActionVoidProcess actionProcess = () -> {
			contZPointService.deleteOne(portalUserIdsService.getCurrentIds(), contZPointId);
		};
		return ApiResponse.responseDelete(actionProcess);
	}

	/**
	 *
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putContZPointMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody List<ContZPointMetadata> requestEntity) {

        Objects.requireNonNull(contObjectId);
        Objects.requireNonNull(contZPointId);


        if (!objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds())) {
            ApiResponse.responseForbidden();
        }

        if (!objectAccessService.checkContZPointId(contZPointId, portalUserIdsService.getCurrentIds())) {
            ApiResponse.responseForbidden();
        }

		ApiActionObjectProcess actionProcess = () -> contZPointMetadataService.saveContZPointMetadata(requestEntity, contZPointId);
		return ApiResponse.responseUpdate(actionProcess);

	}

}
