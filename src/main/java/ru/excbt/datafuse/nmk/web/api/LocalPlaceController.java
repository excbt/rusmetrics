package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateParser;
import ru.excbt.datafuse.nmk.data.service.LocalPlaceService;
import ru.excbt.datafuse.nmk.data.service.LocalPlaceTemperatureSstService;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma")
public class LocalPlaceController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceController.class);

	@Autowired
	private LocalPlaceService localPlaceService;

	@Autowired
	private LocalPlaceTemperatureSstService localPlaceTemperatureSstService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/localPlaces/all", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getLocalPlace() {
		List<LocalPlace> resultList = localPlaceService.selectLocalPlaces();
		return responseOK(resultList);
	}

	/**
	 * 
	 * @param localPlaceId
	 * @param sstDateStr
	 * @return
	 */
	@RequestMapping(value = "/localPlaces/{localPlaceId}/sst", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getLocalPlaceSst(@PathVariable("localPlaceId") Long localPlaceId,
			@RequestParam("sstDateStr") String sstDateStr) {

		LocalDateParser parser = LocalDateParser.parse(sstDateStr);

		if (parser.isEmpty()) {
			return responseBadRequest(ApiResult.badRequest("sstDateStr is not valud"));
		}

		List<LocalPlaceTemperatureSst> resultList = localPlaceTemperatureSstService.selectByLocalPlace(localPlaceId,
				parser.getDateValue().toLocalDate());

		return responseOK(resultList);
	}

}
