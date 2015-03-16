package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.service.UDirectoryParamService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;

@Controller
@RequestMapping(value = "/api/u_directory")
public class UDirectoryParamController {

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll(
			@PathVariable("directoryId") long directoryId) {
		List<UDirectoryParam> result = directoryParamService
				.selectDirectoryParams(directoryId);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param/{id}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOne(
			@PathVariable("directoryId") long directoryId,
			@PathVariable("id") long id) {
		UDirectoryParam result = directoryParamService
				.findOne(id);
		
		checkState(directoryId == result.getDirectory().getId());
		
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @param id
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param/{id}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(
			@PathVariable("directoryId") long directoryId,
			@PathVariable("id") long id, @RequestBody UDirectoryParam entity) {

		checkNotNull(entity);
		checkArgument(entity.getId().longValue() == id);

		UDirectory directory = directoryService.findOne(directoryId);
		checkNotNull(directory);

		entity.setDirectory(directory);

		try {
			directoryParamService.save(entity);
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
	@RequestMapping(value = "/{directoryId}/param", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(
			@PathVariable("directoryId") long directoryId,
			@RequestBody UDirectoryParam entity, HttpServletRequest request) {

		checkNotNull(entity);

		UDirectory directory = directoryService.findOne(directoryId);
		checkNotNull(directory);

		entity.setDirectory(directory);

		UDirectoryParam resultEntity = null;
		try {
			resultEntity = directoryParamService.save(entity);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (PersistenceException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		checkNotNull(resultEntity);

		URI location = URI.create(request.getRequestURI() + "/"
				+ resultEntity.getId());
		return ResponseEntity.created(location).body(resultEntity);

	}

	@RequestMapping(value = "/{directoryId}/param/{id}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(
			@PathVariable("directoryId") long directoryId,
			@PathVariable("id") long id) {
		
		try {
			directoryParamService.delete(id);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (PersistenceException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}
		return ResponseEntity.ok().build();
	}

}
