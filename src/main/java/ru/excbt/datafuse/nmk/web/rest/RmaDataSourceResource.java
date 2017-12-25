package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.excbt.datafuse.nmk.data.repository.keyname.DataSourceTypeRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.RawModemService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;

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
public class RmaDataSourceResource extends SubscrDataSourceResource {

    @Autowired
    public RmaDataSourceResource(SubscrDataSourceService subscrDataSourceService, DataSourceTypeRepository dataSourceTypeRepository, RawModemService rawModemService, PortalUserIdsService portalUserIdsService) {
        super(subscrDataSourceService, dataSourceTypeRepository, rawModemService, portalUserIdsService);
    }
}
