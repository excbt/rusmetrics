package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.ContEventTypeService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;

/**
 * Контроллер для работы с объектом учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.06.2015
 *
 */
@RestController
@RequestMapping(value = "/api/contEvent")
public class ContEventController {

	private final ContEventService contEventService;

	private final ContEventTypeService contEventTypeService;

    public ContEventController(ContEventService contEventService, ContEventTypeService contEventTypeService) {
        this.contEventService = contEventService;
        this.contEventTypeService = contEventTypeService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/types", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContEventTypes() {
		ApiActionObjectProcess action = () -> {
			List<ContEventType> vList = contEventTypeService.selectBaseContEventTypes();
			return ObjectFilters.disabledFilter(vList);
		};
		return ApiResponse.responseOK(action);
	}

}
