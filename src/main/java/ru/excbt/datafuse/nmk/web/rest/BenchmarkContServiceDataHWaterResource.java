package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.web.service.WebAppPropsService;

/**
 * Контроллер для замера отклика системы по данным по воде
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.mm.2015
 *
 */
@RestController
@RequestMapping(value = "/api/benchmark")
public class BenchmarkContServiceDataHWaterResource extends SubscrContServiceDataHWaterResource
{

	private final BenchmarkService benchmarkService;

    public BenchmarkContServiceDataHWaterResource(ContZPointService contZPointService, HWatersCsvService hWatersCsvService, WebAppPropsService webAppPropsService, CurrentSubscriberService currentSubscriberService, ContServiceDataHWaterService contServiceDataHWaterService, ContServiceDataHWaterDeltaService contObjectHWaterDeltaService, ContServiceDataHWaterImportService contServiceDataHWaterImportService, SubscrDataSourceService subscrDataSourceService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService, BenchmarkService benchmarkService) {
        super(contZPointService, hWatersCsvService, webAppPropsService, currentSubscriberService, contServiceDataHWaterService, contObjectHWaterDeltaService, contServiceDataHWaterImportService, subscrDataSourceService, objectAccessService, portalUserIdsService);
        this.benchmarkService = benchmarkService;
    }

	@Override
    protected PortalUserIds getCurrentPortalUserIds() {
        return portalUserIdsService.getCurrentIds();
    }


}
