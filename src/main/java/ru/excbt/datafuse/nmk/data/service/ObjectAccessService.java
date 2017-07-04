package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;

import java.util.List;

/**
 * Created by kovtonyk on 04.07.2017.
 */
@Service
@Transactional(readOnly = true)
public class ObjectAccessService {

    private final static boolean NEW_ACCESS = true;

    private final SubscrContObjectRepository subscrContObjectRepository;

    private final ContObjectAccessRepository contObjectAccessRepository;


    public ObjectAccessService(SubscrContObjectRepository subscrContObjectRepository, ContObjectAccessRepository contObjectAccessRepository) {
        this.subscrContObjectRepository = subscrContObjectRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
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
        return result;
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
            result = contObjectAccessRepository.findContObjectIdsByRmaSubscriberId(rmaSubscriberId);
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
            result = contObjectAccessRepository.findSubscriberIdsByRma(rmaSubscriberId, contObjectId);
        } else {
            result = subscrContObjectRepository.selectContObjectSubscriberIdsByRma(rmaSubscriberId, contObjectId);
        }
        return result;

    }

}
