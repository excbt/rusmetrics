package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.service.UDirectoryParamService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/u_directory")
public class UDirectoryParamController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryParamController.class);

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll(@PathVariable("directoryId") long directoryId) {
		List<UDirectoryParam> result = directoryParamService.selectDirectoryParams(directoryId);
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
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
	@RequestMapping(value = "/{directoryId}/param/{id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(@PathVariable("directoryId") long directoryId, @PathVariable("id") long id,
			@RequestBody UDirectoryParam uDirectoryParam) {

		checkNotNull(uDirectoryParam);
		checkArgument(uDirectoryParam.getId().longValue() == id);

		UDirectory directory = directoryService.findOne(getSubscriberId(), directoryId);
		checkNotNull(directory);

		uDirectoryParam.setDirectory(directory);

		ApiAction action = new AbstractEntityApiAction<UDirectoryParam>(uDirectoryParam) {

			@Override
			public void process() {
				setResultEntity(directoryParamService.save(entity));
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 * 
	 * @param directoryId
	 * @param uDirectoryParam
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(@PathVariable("directoryId") long directoryId,
			@RequestBody UDirectoryParam uDirectoryParam, HttpServletRequest request) {

		checkNotNull(uDirectoryParam);

		UDirectory directory = directoryService.findOne(getSubscriberId(), directoryId);
		checkNotNull(directory);

		uDirectoryParam.setDirectory(directory);

		ApiActionLocation action = new AbstractEntityApiActionLocation<UDirectoryParam, Long>(uDirectoryParam,
				request) {

			@Override
			public void process() {
				setResultEntity(directoryParamService.save(entity));
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
	 * @param directoryId
	 * @param paramId
	 * @return
	 */
	@RequestMapping(value = "/{directoryId}/param/{paramId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(@PathVariable("directoryId") long directoryId,
			@PathVariable("paramId") final long paramId) {

		ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				directoryParamService.delete(paramId);

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

}
