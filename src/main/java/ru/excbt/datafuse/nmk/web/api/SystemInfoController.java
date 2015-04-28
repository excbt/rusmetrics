package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.FullUserInfo;
import ru.excbt.datafuse.nmk.data.repository.FullUserInfoRepository;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/systemInfo")
public class SystemInfoController extends WebApiController {

	@Autowired
	private FullUserInfoRepository fullUserInfoRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@RequestMapping(value = "/fullUserInfo", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getFullUserInfo() {
		FullUserInfo result = currentSubscriberService.getFullUserInfo();
		if (result == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(result);
	}

}
