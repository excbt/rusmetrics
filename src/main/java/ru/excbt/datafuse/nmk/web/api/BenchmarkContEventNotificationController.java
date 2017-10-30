package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.mapper.SubscrContEventNotificationMapper;

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

	private final BenchmarkService benchmarkService;

    public BenchmarkContEventNotificationController(SubscrContEventNotificationService subscrContEventNotifiicationService, SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService, ContEventMonitorService contEventMonitorService, ContEventMonitorV3Service contEventMonitorV3Service, ContEventLevelColorService contEventLevelColorService, ContEventTypeService contEventTypeService, ContEventService contEventService, SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service, ObjectAccessService objectAccessService, SubscrContEventNotificationMapper mapper, BenchmarkService benchmarkService) {
        super(subscrContEventNotifiicationService, subscrContEventNotifiicationStatusService, contEventMonitorService, contEventMonitorV3Service, contEventLevelColorService, contEventTypeService, contEventService, subscrContEventNotifiicationStatusV2Service, objectAccessService, mapper);
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
