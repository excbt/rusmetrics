package ru.excbt.datafuse.nmk.data.service;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrContZPointStPlan;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContZPointStPlanDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.SubscrContZPointStPlanRepository;
import ru.excbt.datafuse.nmk.service.mapper.SubscrContZPointStPlanMapper;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class SubscrContZPointStPlanService {

    private final SubscrContZPointStPlanRepository repository;

    private final SubscrContZPointStPlanMapper mapper;

    @Autowired
    public SubscrContZPointStPlanService(SubscrContZPointStPlanRepository subscrContZPointStPlanRepository, SubscrContZPointStPlanMapper mapper) {
        this.repository = subscrContZPointStPlanRepository;
        this.mapper = mapper;
    }


    /**
     * @param contZPointId
     * @param portalUserIds
     * @return
     */
    @Transactional
    public List<SubscrContZPointStPlanDTO> findContZPointStPlans(Long contZPointId, PortalUserIds portalUserIds) {
        Preconditions.checkNotNull(contZPointId);
        Preconditions.checkNotNull(portalUserIds);
        List<SubscrContZPointStPlanDTO> dtos = repository.findBySubscriberAndContZPoint(contZPointId, portalUserIds.getSubscriberId())
            .stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> mapper.toDto(i)).collect(Collectors.toList());
        return dtos;
    }

    /**
     * @param planDTO
     * @param portalUserIds
     * @return
     */
    @Transactional
    public SubscrContZPointStPlanDTO saveDTO (SubscrContZPointStPlanDTO planDTO, PortalUserIds portalUserIds) {
        SubscrContZPointStPlan stPlan = mapper.toEntity(planDTO);
        stPlan.setSubscriberId(planDTO.getSubscriberId());
        return mapper.toDto(repository.saveAndFlush(stPlan));
    }

    @Transactional
    public void delete (Long id, PortalUserIds portalUserIds) {
        SubscrContZPointStPlan zPointStPlan = repository.findOne(id);
        Preconditions.checkNotNull(zPointStPlan);
        Preconditions.checkArgument(zPointStPlan.getSubscriberId().equals(portalUserIds.getSubscriberId()));
        zPointStPlan.setDeleted(1);
        repository.saveAndFlush(zPointStPlan);
    }

}
