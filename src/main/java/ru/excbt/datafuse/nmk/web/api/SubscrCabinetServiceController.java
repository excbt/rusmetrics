package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.service.SubscrCabinetService;
import ru.excbt.datafuse.nmk.data.service.SubscrCabinetService.ContObjectCabinetInfo;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * 
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.04.2016
 *
 */
@Controller
@RequestMapping(value = "/api/subscr/subscrCabinetService")
public class SubscrCabinetServiceController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetServiceController.class);

	@Autowired
	private SubscrCabinetService subscrCabinetService;

	/**
	 * 
	 * @param xId
	 * @return
	 */
	@RequestMapping(value = "/contObjectCabinetInfo", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectCabinetInfo() {
		List<ContObjectCabinetInfo> resultList = subscrCabinetService
				.selectSubscrContObjectCabinetInfoList(getSubscriberId());
		checkNotNull(resultList);
		return responseOK(resultList);
	}

}
