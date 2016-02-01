package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.UDirectoryNodeService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с универсальным справочником
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.03.2015
 *
 */
@Controller
@RequestMapping(value = "/api/u_directory")
public class UDirectoryController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryController.class);

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private UDirectoryNodeService directoryNodeService;

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{id}/nodes", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDirectoryNodes(@PathVariable("id") long directoryId) {
		UDirectory dir = directoryService.findOne(getCurrentSubscriberId(), directoryId);
		checkNotNull(dir);

		UDirectoryNode result = dir.getDirectoryNode();
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOne(@PathVariable("id") long directoryId) {

		if (directoryService.checkAvailableDirectory(getCurrentSubscriberId(), directoryId)) {

			UDirectory result = directoryService.findOne(getCurrentSubscriberId(), directoryId);
			checkNotNull(result);

			return ResponseEntity.ok(result);
		}

		return ResponseEntity.notFound().build();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll() {
		List<UDirectory> result = directoryService.findAll(getCurrentSubscriberId());
		checkNotNull(result);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @param uDirectory
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(@PathVariable("directoryId") long id, @RequestBody UDirectory uDirectory) {

		checkNotNull(uDirectory);
		checkNotNull(uDirectory.getId());
		checkArgument(uDirectory.getId().longValue() == id);

		ApiAction action = new AbstractEntityApiAction<UDirectory>(uDirectory) {

			@Override
			public void process() {
				setResultEntity(directoryService.save(getCurrentSubscriberId(), entity));
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param uDirectory
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(@RequestBody UDirectory uDirectory, HttpServletRequest request) {

		checkNotNull(uDirectory);
		checkArgument(uDirectory.getId() == null);

		ApiActionLocation action = new AbstractEntityApiActionLocation<UDirectory, Long>(uDirectory, request) {

			@Override
			public void process() {
				setResultEntity(directoryService.save(getCurrentSubscriberId(), entity));
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(@PathVariable("directoryId") final long directoryId) {

		ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				directoryService.delete(getCurrentSubscriberId(), directoryId);

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

}
