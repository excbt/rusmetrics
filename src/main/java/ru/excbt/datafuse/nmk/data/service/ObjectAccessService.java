package ru.excbt.datafuse.nmk.data.service;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;

import java.util.List;
import java.util.stream.Collectors;

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
            //result = contObjectAccessRepository.findBySubscriberId(subscriberId).stream().map((a) -> a.getContObject()).collect(Collectors.toList());
            result = contObjectAccessRepository.findContObjectBySubscriberId(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContObjects(subscriberId);
        }
        return result;
    }

}
