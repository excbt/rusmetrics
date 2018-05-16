package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.service.TimezoneDefService;
import ru.excbt.datafuse.nmk.service.dto.TimezoneDefDTO;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;

/**
 * Контроллер для работы с часовыми поясами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
@RestController
@RequestMapping(value = "/api/timezoneDef")
public class TimezoneDefResource {

	@Autowired
	private TimezoneDefService timezoneDefService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<?> getTimezoneDefAll() {
		List<TimezoneDefDTO> resultList = timezoneDefService.findTimeZoneDefs();
		return ResponseEntity.ok(resultList);
	}

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getTimezoneDef() {
        List<TimezoneDefDTO> resultList = timezoneDefService.findTimeZoneDefs();
        return ResponseEntity.ok(resultList);
    }

}
