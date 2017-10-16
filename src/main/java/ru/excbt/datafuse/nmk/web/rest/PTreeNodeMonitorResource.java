package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/p-tree-node-monitor")
public class PTreeNodeMonitorResource {

    private final CurrentSubscriberService currentSubscriberServicel;

    @Autowired
    public PTreeNodeMonitorResource(CurrentSubscriberService currentSubscriberServicel) {
        this.currentSubscriberServicel = currentSubscriberServicel;
    }
}
