package ru.excbt.datafuse.nmk.web.rest;

import org.json.JSONArray;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.StPlanTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.StPlanTemplateService;
import ru.excbt.datafuse.nmk.data.service.SubscrStPlanService;
import ru.excbt.datafuse.nmk.web.AnyControllerSubscriberTest;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import static org.junit.Assert.*;

@Transactional
public class SubscrStPlanResourceTest extends AnyControllerTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrStPlanResourceTest.class);


    @Autowired
    private SubscrStPlanService subscrStPlanService;

    @Autowired
    private StPlanTemplateService stPlanTemplateService;

    @Autowired
    private CurrentSubscriberService currentSubscriberService;

    @Test
    public void getStPlans() throws Exception {

        SubscrStPlan stPlan = stPlanTemplateService.cloneFromTemplate("TEMP_CHART_001");
        subscrStPlanService.save(stPlan, currentSubscriberService.getSubscriberParam());

        String result = _testGetJson("/api/subscr/st-plans");

        JSONArray resultJsonArray = new JSONArray(result);
        log.info("Result Json:\n {}", resultJsonArray.toString(4));

    }

}
