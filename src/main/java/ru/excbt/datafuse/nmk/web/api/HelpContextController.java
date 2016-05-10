package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.HelpContext;
import ru.excbt.datafuse.nmk.data.service.HelpContextService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/help")
public class HelpContextController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(HelpContextController.class);

	private final static String REDIRECT = "redirect:";

	@Autowired
	private HelpContextService helpContextService;

	/**
	 * 
	 * @return
	 */
	private boolean isHelpContextSetupEnable() {
		if (currentSubscriberService.isSystemUser()) {
			if (helpContextService.isHelpContextSetup()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	private String httpPrefix(String url) {
		if (url.contains("http://")) {
			return url;
		}
		return "http://" + url;
	}

	/**
	 * 
	 * @param anchorId
	 * @return
	 */
	@RequestMapping(value = "/jmp/{anchorId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public String getHelpContextJmp(@PathVariable("anchorId") String anchorId) {

		HelpContext helpContext = helpContextService.findByAnchorId(anchorId);
		if (helpContext == null || helpContext.getHelpUrl() == null) {
			return REDIRECT + httpPrefix(helpContextService.getHelpContextDefault());
		}
		return REDIRECT + httpPrefix(helpContext.getHelpUrl());
	}

	/**
	 * 
	 * @param anchorId
	 * @return
	 */
	@RequestMapping(value = "/setup/{anchorId}", method = RequestMethod.GET)
	public String getHelpContextSetup(@PathVariable("anchorId") String anchorId, Model model) {
		HelpContext helpContext = helpContextService.getHelpContext(anchorId);
		model.addAttribute("helpContext", helpContext);
		model.addAttribute("anchorId", anchorId);

		return "helpContext/setupHelpContext.jsp";
	}

	/**
	 * 
	 * @param anchorId
	 * @return
	 */
	@RequestMapping(value = "/anchor/{anchorId}", method = RequestMethod.GET)
	public String getHelpContextAhchor(@PathVariable("anchorId") String anchorId) {

		if (isHelpContextSetupEnable()) {
			return REDIRECT + "/api/help/setup/" + anchorId;
		}
		return REDIRECT + "/api/help/jmp/" + anchorId;

	}

	/**
	 * 
	 * @param anchorId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/setup/{anchorId}", method = RequestMethod.POST)
	public String postHelpContextSetup(@PathVariable("anchorId") String anchorId,
			@ModelAttribute("helpContextForm") HelpContext helpContext, Model model) {

		HelpContext savedHelpContext = helpContextService.saveHelpContext(helpContext);

		model.addAttribute("helpContext", savedHelpContext);
		model.addAttribute("anchorId", anchorId);

		return "helpContext/setupHelpContext.jsp";

	}

	/**
	 * 
	 * @param anchorId
	 * @return
	 */
	@RequestMapping(value = "/info/{anchorId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getHelpContextInfo(@PathVariable("anchorId") String anchorId) {

		if (!helpContextService.isHelpContextSetup()) {
			responseForbidden();
		}

		HelpContext helpContext = helpContextService.findByAnchorId(anchorId);
		if (helpContext == null) {
			return responseBadRequest();
		}
		return responseOK(helpContext);
	}

}
