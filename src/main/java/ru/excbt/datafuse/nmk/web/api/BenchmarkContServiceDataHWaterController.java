package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.excbt.datafuse.nmk.data.service.support.BenchmarkService;

/**
 * Контроллер для замера отклика системы по данным по воде
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.mm.2015
 *
 */
@Controller
@RequestMapping(value = "/api/benchmark")
public class BenchmarkContServiceDataHWaterController extends SubscrContServiceDataHWaterController {

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
