package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.excbt.datafuse.nmk.data.service.support.BenchmarkService;

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
