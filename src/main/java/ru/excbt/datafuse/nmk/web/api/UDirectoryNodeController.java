package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.UDirectoryNodeService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;

@Controller
@RequestMapping(value = "/api/u_directory")
public class UDirectoryNodeController {

	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryNodeController.class);

	@Autowired
	private UDirectoryNodeService directoryNodeService;

	@Autowired
	private UDirectoryService directoryService;

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/node", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll(
			@PathVariable("directoryId") long directoryId) {

		UDirectory directory = directoryService.findOne(directoryId);
		if (directory == null) {
			return ResponseEntity.notFound().build();
		}
		UDirectoryNode directoryNode = directory.getDirectoryNode();

		if (directoryNode == null) {
			return ResponseEntity.notFound().build();
		}

		logger.trace("DirectoryNode ID {}", directoryNode.getId());

		UDirectoryNode result = directoryNodeService.getRootNode(directoryNode
				.getId());
		return ResponseEntity.ok().body(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/node/{id}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(
			@PathVariable("directoryId") long directoryId,
			@PathVariable("id") long id, @RequestBody UDirectoryNode entity) {

		checkNotNull(entity, "UDirectoryNode is empty");
		checkArgument(directoryId > 0, "directoryId is not set");

		try {
			directoryNodeService.save(entity, directoryId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (PersistenceException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().build();
	}

	/**
	 * 
	 * @param directoryId
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/node", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(
			@PathVariable("directoryId") long directoryId,
			@RequestBody UDirectoryNode entity, HttpServletRequest request) {

		checkNotNull(entity, "UDirectoryNode is empty");
		checkArgument(directoryId > 0, "directoryId is not set");

		UDirectoryNode resultEntity = null;
		try {
			resultEntity = directoryNodeService.save(entity, directoryId);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (PersistenceException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}
		
		URI location = URI.create(request.getRequestURI() + "/"
				+ resultEntity.getId());
		return ResponseEntity.created(location).body(resultEntity);

	}

}
