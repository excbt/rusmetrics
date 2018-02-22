package ru.excbt.datafuse.nmk.web.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.security.SecurityUtils;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/app/home", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/app", method = RequestMethod.GET)
	public String app() {

	    if (!SecurityUtils.isAuthenticated()) {
            return "redirect:/auth/localLogin";
        }

		return "redirect:/app/";
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/app/", method = RequestMethod.GET)
	public String app1() {

        if (!SecurityUtils.isAuthenticated()) {
            return "redirect:/auth/localLogin";
        }

		return "/../../app/index.html";
	}


    @RequestMapping(value = "/v2", method = RequestMethod.GET)
    public String appV2_redirect() {
        return "redirect:/v2/index.html";
    }

}
