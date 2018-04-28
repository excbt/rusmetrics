package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryNodeService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с узлами универрсального справочника
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.03.2015
 *
 */
@Controller
@RequestMapping(value = "/api/u_directory")
public class UDirectoryNodeController  {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryNodeController.class);

	private final UDirectoryNodeService directoryNodeService;

	private final UDirectoryService directoryService;

    private final PortalUserIdsService portalUserIdsService;

    public UDirectoryNodeController(UDirectoryNodeService directoryNodeService, UDirectoryService directoryService, PortalUserIdsService portalUserIdsService) {
        this.directoryNodeService = directoryNodeService;
        this.directoryService = directoryService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/node", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll(@PathVariable("directoryId") long directoryId) {

		logger.trace("getAll DirectoryNode ID {}", directoryId);

		UDirectory directory = directoryService.findOne(portalUserIdsService.getCurrentIds().getSubscriberId(), directoryId);
		if (directory == null) {
			return ResponseEntity.notFound().build();
		}

		logger.trace("getAll DirectoryNode ID {}. getDirectoryNode", directoryId);
		UDirectoryNode directoryNode = directory.getDirectoryNode();

		if (directoryNode == null) {
			return ResponseEntity.notFound().build();
		}

		logger.trace("DirectoryNode ID {}", directoryNode.getId());

		UDirectoryNode result = directoryNodeService.getRootNode(directoryNode.getId());
		return ResponseEntity.ok().body(result);
	}

	/**
	 *
	 * @param directoryId
	 * @param uDirectoryNode
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/node/{id}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(@PathVariable("directoryId") final long directoryId, @PathVariable("id") long id,
			@RequestBody UDirectoryNode uDirectoryNode) {

		checkNotNull(uDirectoryNode, "UDirectoryNode is empty");
		checkArgument(directoryId > 0, "directoryId is not set");

		if (uDirectoryNode.getId() != id) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.body(String.format("Can't change root node for Directory (id=%d)", directoryId));
		}

		ApiAction action = new AbstractEntityApiAction<UDirectoryNode>(uDirectoryNode) {

			@Override
			public void process() {
				setResultEntity(directoryNodeService.saveWithDictionary(portalUserIdsService.getCurrentIds().getSubscriberId(), entity, directoryId));
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

	/**
	 *
	 * @param directoryId
	 * @param uDirectoryNode
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/node", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(@PathVariable("directoryId") final long directoryId,
			@RequestBody UDirectoryNode uDirectoryNode, HttpServletRequest request) {

		checkNotNull(uDirectoryNode, "UDirectoryNode is empty");
		checkArgument(directoryId > 0, "directoryId is not set");

		ApiActionLocation action = new ApiActionEntityLocationAdapter<UDirectoryNode, Long>(uDirectoryNode, request) {

			@Override
			protected Long getLocationId() {

				return getResultEntity().getId();
			}

			@Override
			public UDirectoryNode processAndReturnResult() {
				return directoryNodeService.saveWithDictionary(portalUserIdsService.getCurrentIds().getSubscriberId(), entity, directoryId);
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);
	}

}
