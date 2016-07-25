package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.keyname.TimeDetailType;
import ru.excbt.datafuse.nmk.data.service.TimeDetailTypeService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с типами детализации по времени
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.01.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrTimeDetailTypeController extends SubscrApiController {

	@Autowired
	private TimeDetailTypeService timeDetailTypeService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/timeDetailType/1h24h", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTimeDetailType() {
		List<TimeDetailType> xList = timeDetailTypeService.select1h24h();
		return responseOK(ObjectFilters.deletedFilter(xList));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/timeDetailType/24h24hAbs", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getTimeDetailType1h24h() {
		List<TimeDetailType> xList = timeDetailTypeService.select24h24hAbs();
		return responseOK(ObjectFilters.deletedFilter(xList));
	}

}
