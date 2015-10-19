package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;

// TODO make RMA actions
@Controller
@RequestMapping(value = "/api/rma")
public class RmaContObjectController extends SubscrContObjectController {

	private static final Logger logger = LoggerFactory.getLogger(RmaContObjectController.class);

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContObject(@RequestBody ContObject contObject, HttpServletRequest request) {

		checkNotNull(contObject);

		if (!contObject.isNew()) {
			return ResponseEntity.badRequest().build();
		}

		ApiActionLocation action = new EntityApiActionLocationAdapter<ContObject, Long>(contObject, request) {

			@Override
			public ContObject processAndReturnResult() {
				return contObjectService.createOne(entity, getSubscriberId());
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
	 * @param contObjectId
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObject(@PathVariable("contObjectId") Long contObjectId) {

		checkNotNull(contObjectId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		ApiAction action = new AbstractApiAction() {

			@Override
			public void process() {
				contObjectService.deleteOne(contObjectId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjects() {
		List<ContObject> resultList = subscriberService
				.selectRmaSubscriberContObjects(currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok().body(ObjectFilters.deletedFilter(resultList));
	}

}
