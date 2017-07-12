package ru.excbt.datafuse.nmk.web.api;

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
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
public class UDirectoryController extends AbstractSubscrApiResource {

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
	@RequestMapping(value = "/{id}/nodes", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
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
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
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
	@RequestMapping(method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll() {
		List<UDirectory> result = directoryService.findAll(getCurrentSubscriberId());
		checkNotNull(result);
		return ResponseEntity.ok(result);
	}

    /**
     *
     * @param id
     * @param uDirectory
     * @return
     */
	@RequestMapping(value = "/{directoryId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
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

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

	/**
	 *
	 * @param uDirectory
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(@RequestBody UDirectory uDirectory, HttpServletRequest request) {

		checkNotNull(uDirectory);
		checkArgument(uDirectory.getId() == null);

		ApiActionLocation action = new ApiActionEntityLocationAdapter<UDirectory, Long>(uDirectory, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public UDirectory processAndReturnResult() {
				return directoryService.save(getCurrentSubscriberId(), entity);
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);

	}

    /**
     *
     * @param directoryId
     * @return
     */
	@RequestMapping(value = "/{directoryId}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(@PathVariable("directoryId") final long directoryId) {

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				directoryService.delete(getCurrentSubscriberId(), directoryId);

			}
		};

		return ApiActionTool.processResponceApiActionDelete(action);
	}

}
