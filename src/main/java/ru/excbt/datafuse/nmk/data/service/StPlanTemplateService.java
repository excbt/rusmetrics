package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.StPlanTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.repository.StPlanTemplateRepository;
import ru.excbt.datafuse.nmk.service.mapper.StPlanTemplateMapper;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

@Service
public class StPlanTemplateService {

    private final StPlanTemplateRepository stPlanTemplateRepository;

    private final StPlanTemplateMapper planTemplateMapper;

    @Autowired
    public StPlanTemplateService(StPlanTemplateRepository stPlanTemplateRepository, StPlanTemplateMapper planTemplateMapper) {
        this.stPlanTemplateRepository = stPlanTemplateRepository;
        this.planTemplateMapper = planTemplateMapper;
    }

    /**
     *
     * @param keyname
     * @return
     */
    public SubscrStPlan cloneFromTemplate(String keyname) {
        StPlanTemplate planTemplate = stPlanTemplateRepository.findById(keyname)
            .orElseThrow(() -> new EntityNotFoundException(StPlanTemplate.class, keyname));
        SubscrStPlan subscrStPlan = planTemplateMapper.planTemplateToSubscrStPlan(planTemplate);
        subscrStPlan.getPlanCharts().forEach(i -> i.setSubscrStPlan(subscrStPlan));
        return subscrStPlan;
    }

}
