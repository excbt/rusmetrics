package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectAccessDTO;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by kovtonyk on 04.07.2017.
 */
@Service
@Transactional(readOnly = true)
public class ObjectAccessService {

    private final static boolean NEW_ACCESS = true;

    private final SubscrContObjectRepository subscrContObjectRepository;

    private final ContObjectAccessRepository contObjectAccessRepository;

    private final ContGroupService contGroupService;

    public ObjectAccessService(SubscrContObjectRepository subscrContObjectRepository, ContObjectAccessRepository contObjectAccessRepository, ContGroupService contGroupService) {
        this.subscrContObjectRepository = subscrContObjectRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contGroupService = contGroupService;
    }


    /**
     *
     * @param subscriberId
     * @return
     */
    public List<ContObject> findContObjects(Long subscriberId) {
        List<ContObject> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findContObjectsBySubscriberId(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContObjects(subscriberId);
        }
        return ObjectFilters.deletedFilter(result);
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    public List<ContObject> findContObjects(Long subscriberId, Long contObjectGroupId) {
        List<ContObject> result;
        if (contObjectGroupId == null) {
            return findContObjects(subscriberId);
        }
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findContObjectsBySubscriberGroup(subscriberId,contObjectGroupId);
        } else {
            result = contGroupService.selectContGroupObjects(SubscriberParam.builder().subscriberId(subscriberId).build(),
                contObjectGroupId);
        }
        return ObjectFilters.deletedFilter(result);
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    public List<Long> findContObjectIds(Long subscriberId) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findContObjectIdsBySubscriber(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContObjectIds(subscriberId);
        }
        return result;
    }


    public List<ContObject> findContObjectsExcludingIds (Long subscriberId, List<Long> idList) {
        List<ContObject> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findContObjectsExcludingIds(subscriberId, idList);
        } else {
            result = subscrContObjectRepository.selectContObjectsExcludingIds(subscriberId,idList);
        }
        return result;
    }

    public List<Long> findContObjectIdsByRmaSubscriberId (Long rmaSubscriberId) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findRmaSubscribersContObjectIds(rmaSubscriberId);
        } else {
            result = subscrContObjectRepository.selectRmaSubscribersContObjectIds(rmaSubscriberId);
        }
        return result;
    }


    public List<ContObject> findContObjectsByIds(Long subscriberId, List<Long> idList) {
        List<ContObject> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findContObjectsByIds(subscriberId, idList);
        } else {
            result = subscrContObjectRepository.selectContObjectsByIds(subscriberId,
                idList);
        }
        return result;
    }


    public List<Long> findSubscriberIdsByRma (Long rmaSubscriberId, Long contObjectId) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findRmaSubscriberIds(rmaSubscriberId, contObjectId);
        } else {
            result = subscrContObjectRepository.selectContObjectSubscriberIdsByRma(rmaSubscriberId, contObjectId);
        }
        return result;

    }


    public boolean checkContObjectId (Long subscriberId, Long contObjectId) {
        if (NEW_ACCESS) {
            return contObjectAccessRepository.findByPK(subscriberId, contObjectId).isPresent();
        } else {
            return subscrContObjectRepository.selectContObjectId(subscriberId, contObjectId).size() > 0;
        }
    }

    public boolean checkContObjectIds(Long subscriberId, List<Long> contObjectIds) {
        if (contObjectIds == null || contObjectIds.isEmpty()) {
            return false;
        }
        List<Long> subscrContObjectIds = findContObjectIds(subscriberId);
        return AbstractService.checkIds(contObjectIds, subscrContObjectIds);
    }



    public List<ContObject> findRmaAvailableContObjects (Long subscriberId, Long rmaSubscriberId) {
        List<ContObject> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findRmaAvailableContObjects(subscriberId, rmaSubscriberId);
        } else {
            result = subscrContObjectRepository.selectRmaAvailableContObjects(subscriberId, rmaSubscriberId);
        }
        return result;

    }

    public List<Object[]> findChildSubscrCabinetContObjectsStats(Long parentSubscriberId) {
        List<Object[]> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findChildSubscrCabinetContObjectsStats(parentSubscriberId);
        } else {
            result = subscrContObjectRepository.selectChildSubscrCabinetContObjectsStats(parentSubscriberId);
        }
        return result;
    }

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public void readContObjectAccess(Long subscriberId, List<? extends ContObjectDTO> contObjectDTOS) {
        List<ContObjectAccess> accesses = contObjectAccessRepository.findBySubscriberId(subscriberId);
        Map<Long, ContObjectAccess> accessMap = new HashMap<>();
        accesses.forEach((i) -> accessMap.put(i.getContObjectId(), i));
        contObjectDTOS.forEach((co) -> {
            Optional.ofNullable(accessMap.get(co.getId())).ifPresent((a) -> {
                if (a.getAccessTtl() != null)
                    co.objectAccess(ObjectAccessDTO.AccessType.TRIAL, a.getAccessTtl().toLocalDate());
            });
        });
    }


}
