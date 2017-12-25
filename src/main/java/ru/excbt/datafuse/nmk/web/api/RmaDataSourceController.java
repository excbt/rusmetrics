package ru.excbt.datafuse.nmk.web.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.excbt.datafuse.nmk.web.rest.SubscrDataSourceController;

/**
 * Контроллер для работы с источниками данных для РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.10.2015
 *
 */
@Controller
@RequestMapping(value = "/api/rma")
public class RmaDataSourceController extends SubscrDataSourceController {

}
