package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.excbt.datafuse.nmk.data.service.BenchmarkService;

/**
 * Контроллер для замера отклика системы по уведомлениям
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 03.11.2015
 *
 */
@Controller
@RequestMapping("/api/benchmark/contEvent")
public class BenchmarkContEventNotificationController extends SubscrContEventNotificationController {

	@Autowired
	private BenchmarkService benchmarkService;

	/**
	 *
	 * @return
	 */
	@Override
	protected long getCurrentSubscriberId() {
		Long subscriberId = benchmarkService.getBenchmarkSubscriberId();
		return subscriberId == null ? 0 : subscriberId;
	}

}
