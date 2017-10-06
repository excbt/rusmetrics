package ru.excbt.datafuse.nmk.web.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.support.BenchmarkService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.HWatersCsvService;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

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

	private final BenchmarkService benchmarkService;

    public BenchmarkContServiceDataHWaterController(ContZPointService contZPointService, HWatersCsvService hWatersCsvService, WebAppPropsService webAppPropsService, CurrentSubscriberService currentSubscriberService, ContServiceDataHWaterService contServiceDataHWaterService, ContServiceDataHWaterDeltaService contObjectHWaterDeltaService, ContServiceDataHWaterImportService contServiceDataHWaterImportService, SubscrDataSourceService subscrDataSourceService, ObjectAccessService objectAccessService, BenchmarkService benchmarkService) {
        super(contZPointService, hWatersCsvService, webAppPropsService, currentSubscriberService, contServiceDataHWaterService, contObjectHWaterDeltaService, contServiceDataHWaterImportService, subscrDataSourceService, objectAccessService);
        this.benchmarkService = benchmarkService;
    }


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
