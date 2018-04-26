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
import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryParamService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Контроллер для работы параметрами унивесального справочника
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.03.2015
 *
 */
@Controller
@RequestMapping(value = "/api/u_directory")
public class UDirectoryParamController {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryParamController.class);

	private final UDirectoryParamService directoryParamService;

	private final UDirectoryService directoryService;

    private final PortalUserIdsService portalUserIdsService;

    public UDirectoryParamController(UDirectoryParamService directoryParamService, UDirectoryService directoryService, PortalUserIdsService portalUserIdsService) {
        this.directoryParamService = directoryParamService;
        this.directoryService = directoryService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll(@PathVariable("directoryId") long directoryId) {
		List<UDirectoryParam> result = directoryParamService.selectDirectoryParams(directoryId);
		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param/{id}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOne(@PathVariable("directoryId") long directoryId, @PathVariable("id") long id) {

		UDirectoryParam result = directoryParamService.findOne(id);

		checkState(directoryId == result.getDirectory().getId());

		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @param directoryId
	 * @param id
	 * @param uDirectoryParam
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param/{id}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(@PathVariable("directoryId") long directoryId, @PathVariable("id") long id,
			@RequestBody UDirectoryParam uDirectoryParam) {

		checkNotNull(uDirectoryParam);
		checkArgument(uDirectoryParam.getId().longValue() == id);

		UDirectory directory = directoryService.findOne(portalUserIdsService.getCurrentIds().getSubscriberId(), directoryId);
		checkNotNull(directory);

		uDirectoryParam.setDirectory(directory);

		ApiAction action = new AbstractEntityApiAction<UDirectoryParam>(uDirectoryParam) {

			@Override
			public void process() {
				setResultEntity(directoryParamService.save(entity));
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);

	}

	/**
	 *
	 * @param directoryId
	 * @param uDirectoryParam
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(@PathVariable("directoryId") long directoryId,
			@RequestBody UDirectoryParam uDirectoryParam, HttpServletRequest request) {

		checkNotNull(uDirectoryParam);

		UDirectory directory = directoryService.findOne(portalUserIdsService.getCurrentIds().getSubscriberId(), directoryId);
		checkNotNull(directory);

		uDirectoryParam.setDirectory(directory);

		ApiActionLocation action = new ApiActionEntityLocationAdapter<UDirectoryParam, Long>(uDirectoryParam, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public UDirectoryParam processAndReturnResult() {
				return directoryParamService.save(entity);
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);

	}

	/**
	 *
	 * @param directoryId
	 * @param paramId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param/{paramId}", method = RequestMethod.DELETE,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(@PathVariable("directoryId") long directoryId,
			@PathVariable("paramId") final long paramId) {

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				directoryParamService.delete(paramId);

			}
		};

		return ApiActionTool.processResponceApiActionDelete(action);
	}

}
