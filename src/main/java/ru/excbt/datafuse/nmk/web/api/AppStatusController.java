package ru.excbt.datafuse.nmk.web.api;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.service.support.AppVersionService;

@Controller
@RequestMapping("/api/appStatus")
public class AppStatusController extends WebApiController {

	@Autowired
	private AppVersionService appVersionService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/version", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAppVersion(HttpSession httpSession) {
		httpSession.invalidate();
		return responseOK(appVersionService.getAppVersion());
	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getStatus(HttpSession httpSession) {
		httpSession.invalidate();
		return responseOK("OK");
	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value = "/appModulesVersions", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAppVersionProps() {

		//		
		//		
		//		Properties props = appVersionService.getAppVersionProps();
		//
		//		final Map<String, String> mp = new HashMap<>();
		//
		//		props.forEach((k, v) -> {
		//			mp.put(DBRowUtils.asString(k), DBRowUtils.asString(v));
		//		});

		return responseOK(appVersionService.getAppModulesInfo());
	}

}
