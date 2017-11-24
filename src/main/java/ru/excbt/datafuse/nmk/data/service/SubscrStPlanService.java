package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrStPlanChartRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrStPlanRepository;
import ru.excbt.datafuse.nmk.service.mapper.SubscrStPlanMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscrStPlanService {

    private final SubscrStPlanRepository subscrStPlanRepository;

    private final SubscrStPlanChartRepository subscrStPlanChartRepository;

    private final SubscrStPlanMapper subscrStPlanMapper;

    @Autowired
    public SubscrStPlanService(SubscrStPlanRepository subscrStPlanRepository, SubscrStPlanChartRepository subscrStPlanChartRepository, SubscrStPlanMapper subscrStPlanMapper) {
        this.subscrStPlanRepository = subscrStPlanRepository;
        this.subscrStPlanChartRepository = subscrStPlanChartRepository;
        this.subscrStPlanMapper = subscrStPlanMapper;
    }

    /**
     *
     * @param portalUserIds
     * @return
     */
    public List<SubscrStPlanDTO> findStPlanDTOs(PortalUserIds portalUserIds) {
        return subscrStPlanRepository.findBySubscriberId(portalUserIds.getSubscriberId()).stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(i -> subscrStPlanMapper.toDto(i))
            .collect(Collectors.toList());
    }

    @Transactional()
    public SubscrStPlan save(SubscrStPlan stPlan, PortalUserIds portalUserIds) {
        stPlan.setSubscriberId(portalUserIds.getSubscriberId());
        return subscrStPlanRepository.save(stPlan);
    }


}
