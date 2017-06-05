package ru.excbt.datafuse.nmk.web.api;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.model.support.JodaTimeParser;
import ru.excbt.datafuse.nmk.data.service.LocalPlaceService;
import ru.excbt.datafuse.nmk.data.service.LocalPlaceTemperatureSstService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

import java.util.List;

@Controller
@RequestMapping(value = "/api/rma")
public class LocalPlaceController extends AbstractSubscrApiResource {

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

		ApiActionObjectProcess actionProcess = () -> {
			return localPlaceService.selectLocalPlaces();
		};

		return responseOK(actionProcess);
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

		JodaTimeParser<LocalDate> parser = JodaTimeParser.parseLocalDate(sstDateStr);

		if (parser.isEmpty()) {
			return responseBadRequest(ApiResult.badRequest("sstDateStr is not valud"));
		}

		ApiActionObjectProcess action = () -> {
			List<LocalPlaceTemperatureSst> resultList = localPlaceTemperatureSstService
					.selectSstByLocalPlace(localPlaceId, parser.getDateValue());

			if (resultList.isEmpty()) {
				try {
					localPlaceTemperatureSstService.initMonthNoCheck(localPlaceId, parser.getDateValue());
				} catch (Exception e) {
				}

				resultList = localPlaceTemperatureSstService.selectSstByLocalPlace(localPlaceId, parser.getDateValue());
			}
			return resultList;
		};

		return responseOK(action);
	}

	/**
	 *
	 * @param localPlaceId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/localPlaces/{localPlaceId}/sst/{sstId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putLocalPlaceTemperatureSst(@PathVariable("localPlaceId") Long localPlaceId,
			@PathVariable("sstId") Long sstId, @RequestBody LocalPlaceTemperatureSst requestEntity) {

		if (!sstId.equals(requestEntity.getId())) {
			return responseBadRequest();
		}

		ApiActionObjectProcess actionProcess = () -> {
			return localPlaceTemperatureSstService.saveSst(requestEntity);
		};

		return responseUpdate(actionProcess);
	}

	/**
	 *
	 * @param localPlaceId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/localPlaces/{localPlaceId}/sst/array", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putLocalPlaceTemperatureSstList(@PathVariable("localPlaceId") Long localPlaceId,
			@RequestParam("sstDateStr") String sstDateStr, @RequestBody List<LocalPlaceTemperatureSst> requestEntity) {

		JodaTimeParser<LocalDate> parser = JodaTimeParser.parseLocalDate(sstDateStr);

		if (parser.isEmpty()) {
			return responseBadRequest(ApiResult.badRequest("sstDateStr is not valud"));
		}

		ApiActionObjectProcess actionProcess = () -> {
			return localPlaceTemperatureSstService.saveSstList(requestEntity);
		};

		return responseUpdate(actionProcess);
	}

}
