package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

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
public class UDirectoryController {

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private UDirectoryNodeService directoryNodeService;

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{id}/nodes", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDirectoryNodes(
			@PathVariable("id") long directoryId) {
		UDirectory dir = directoryService.findOne(directoryId);
		checkNotNull(dir);

		UDirectoryNode result = dir.getDirectoryNode();
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOne(@PathVariable("id") long directoryId) {

		if (directoryService.checkAvailableDirectory(directoryId)) {

			UDirectory result = directoryService.findOne(directoryId);
			checkNotNull(result);

			return ResponseEntity.ok(result);
		}

		return ResponseEntity.notFound().build();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll() {
		List<UDirectory> result = directoryService.findAll();
		checkNotNull(result);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(
			@PathVariable("directoryId") long directoryId,
			@RequestBody UDirectory entity) {

		checkNotNull(entity);
		checkNotNull(entity.getId());
		checkArgument(entity.getId().longValue() == directoryId);

		try {
			directoryService.save(entity);
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
	 * @param entity
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(
			@RequestBody UDirectory entity, HttpServletRequest request) {

		checkNotNull(entity);
		checkArgument(entity.getId() == null);
		
		UDirectory resultEntity = null;
		
		try {
			resultEntity = directoryService.save(entity);
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
	
	
	@RequestMapping(value = "/{directoryId}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(
			@PathVariable("directoryId") long id) {
		
		try {
			directoryService.delete(id);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (PersistenceException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}
		return ResponseEntity.ok().build();
	}
	
	
	
}
