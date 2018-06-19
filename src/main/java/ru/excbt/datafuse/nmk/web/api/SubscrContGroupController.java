package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.dto.SubscrContGroupDTO;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с группой объектов учета
 *
 * @author STA.Kuzovoy
 * @version 1.0
 * @since 29.05.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr/contGroup")
public class SubscrContGroupController {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetController.class);

	private final ContGroupService contGroupService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrContGroupController(ContGroupService contGroupService, PortalUserIdsService portalUserIdsService) {
        this.contGroupService = contGroupService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/{contGroupId}/contObject", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getGroupContObjects(@PathVariable(value = "contGroupId") Long contGroupId) {

		checkNotNull(contGroupId);

        List<ContObjectDTO> contObjectDTOList = contGroupService.selectContGroupObjectsDTO(portalUserIdsService.getCurrentIds(), contGroupId);

		return ResponseEntity.ok(contObjectDTOList);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/{contGroupId}/contObject/available", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableContObjectItems(@PathVariable(value = "contGroupId") Long contGroupId) {

		checkNotNull(contGroupId);

        List<ContObjectDTO> contObjectDTOList = contGroupService.selectAvailableContGroupObjects(portalUserIdsService.getCurrentIds(), contGroupId);

		return ResponseEntity.ok(contObjectDTOList);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscriberGroups() {

		List<SubscrContGroupDTO> subscrContGroupDTOList = contGroupService.selectSubscriberGroups(portalUserIdsService.getCurrentIds());

		return ResponseEntity.ok(subscrContGroupDTOList);

	}

	/**
	 *
	 * @param contObjectIds
	 * @param contGroup
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createGroup(
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestBody final SubscrContGroupDTO contGroup, final HttpServletRequest request) {

		if (contGroup.getId() == null) {
		    return ResponseEntity.badRequest().build();
        }

		ApiActionProcess<SubscrContGroupDTO> process = () -> {
			contGroup.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
			return contGroupService.createOne(contGroup, contObjectIds);
		};

		return ApiResponse.responseCreate(process, () -> request.getRequestURI());

	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@RequestMapping(value = "{contGroupId}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteGroup(@PathVariable(value = "contGroupId") final Long contGroupId) {

		ApiActionVoidProcess action = () -> {
			contGroupService.deleteOne(contGroupId);
		};

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@RequestMapping(value = "{contGroupId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateGroup(@PathVariable(value = "contGroupId") final Long contGroupId,
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestBody final SubscrContGroupDTO contGroupDTO) {

		checkNotNull(contGroupId);
		checkNotNull(contGroupDTO);
		checkNotNull(contGroupDTO.getId());
		checkArgument(contGroupDTO.getId().equals(contGroupId));

		ApiActionObjectProcess action = () -> {
			contGroupDTO.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
			return contGroupService.updateOne(contGroupDTO, contObjectIds);
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

}
