package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.service.ContZPointConsumptionService;

@RestController
@RequestMapping(value = "/api/subscr/consumption")
public class ContZPointConsumptionResource {

    private final ContZPointConsumptionService contZPointConsumptionService;

    @Autowired
    public ContZPointConsumptionResource(ContZPointConsumptionService contZPointConsumptionService) {
        this.contZPointConsumptionService = contZPointConsumptionService;
    }




}
