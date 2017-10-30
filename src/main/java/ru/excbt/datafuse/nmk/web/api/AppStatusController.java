package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.service.AppVersionService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/appStatus")
public class AppStatusController {

	@Autowired
	private AppVersionService appVersionService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/version", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAppVersion(HttpSession httpSession) {

		ApiActionObjectProcess action = () -> {
			httpSession.invalidate();
			return appVersionService.getAppVersion();
		};

		return ApiResponse.responseOK(action);
	}

	/**
	 *
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getStatus(HttpSession httpSession) {

		ApiActionObjectProcess action = () -> {
			httpSession.invalidate();
			return "OK";

		};

		return ApiResponse.responseOK(action);
	}

    /**
     *
     * @return
     */
	@RequestMapping(value = "/appModulesVersions", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAppVersionProps() {

		ApiActionObjectProcess action = () -> {
			return appVersionService.getAppModulesInfo();
		};

		return ApiResponse.responseOK(action);
	}

}
