package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectAccessDTO;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.ColumnHelper;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kovtonyk on 04.07.2017.
 */
@Service
@Transactional(readOnly = true)
public class ObjectAccessService {

    private final static boolean NEW_ACCESS = true;

    private final SubscrContObjectRepository subscrContObjectRepository;

    private final ContObjectAccessRepository contObjectAccessRepository;

    private final ContZPointAccessRepository contZPointAccessRepository;

    private final ContGroupService contGroupService;

    private final SubscrServiceAccessService subscrServiceAccessService;


    public ObjectAccessService(SubscrContObjectRepository subscrContObjectRepository,
                               ContObjectAccessRepository contObjectAccessRepository,
                               ContZPointAccessRepository contZPointAccessRepository,
                               ContGroupService contGroupService,
                               SubscrServiceAccessService subscrServiceAccessService) {
        this.subscrContObjectRepository = subscrContObjectRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contGroupService = contGroupService;
        this.subscrServiceAccessService = subscrServiceAccessService;
    }



    private List<Long> makeFilterList (List<Long> idList) {
        return idList != null && !idList.isEmpty() ? idList : Arrays.asList(0L);
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    public List<ContObject> findContObjects(Long subscriberId) {
        List<ContObject> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findAllContObjects(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContObjects(subscriberId);
        }
        return ObjectFilters.deletedFilter(result);
    }

    public List<ContObject> findContObjectsNoTtl(Long subscriberId) {
        List<ContObject> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findAllContObjectsNoTtl(subscriberId);
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
            result = contObjectAccessRepository.findAllContObjectIds(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContObjectIds(subscriberId);
        }
        return result;
    }


    public List<ContObject> findContObjectsExcludingIds (Long subscriberId, List<Long> idList) {
        List<ContObject> result;

        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findContObjectsExcludingIdsNoTtl(subscriberId, makeFilterList(idList));
        } else {
            result = subscrContObjectRepository.selectContObjectsExcludingIds(subscriberId, makeFilterList(idList));
        }
        return result;
    }

    public List<Long> findRmaSubscribersContObjectIds(Long rmaSubscriberId) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findRmaSubscribersContObjectIdsNoTtl(rmaSubscriberId);
        } else {
            result = subscrContObjectRepository.selectRmaSubscribersContObjectIds(rmaSubscriberId);
        }
        return result;
    }


    public List<ContObject> findContObjectsByIds(Long subscriberId, List<Long> idList) {
        List<ContObject> result;

        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findContObjectsByIdsNoTtl(subscriberId, makeFilterList(idList));
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


    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public void setupRmaHaveSubscr(final SubscriberParam subscriberParam, final List<ContObject> contObjects) {
        checkNotNull(subscriberParam);
        checkNotNull(contObjects);

        if (!subscriberParam.isRma()) {
            return;
        }

        List<Long> subscrContObjectIds = this.findRmaSubscribersContObjectIds(subscriberParam.getRmaSubscriberId());

        Set<Long> subscrContObjectIdMap = new HashSet<>(subscrContObjectIds);
        contObjects.forEach(i -> {
            boolean haveSubscr = subscrContObjectIdMap.contains(i.getId());
            i.set_haveSubscr(haveSubscr);
        });
    }

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public void setupRmaHaveSubscrDTO(final SubscriberParam subscriberParam, final List<ContObjectDTO> contObjects) {
        checkNotNull(subscriberParam);
        checkNotNull(contObjects);

        if (!subscriberParam.isRma()) {
            return;
        }

        List<Long> subscrContObjectIds = findRmaSubscribersContObjectIds(subscriberParam.getRmaSubscriberId());

        Set<Long> subscrContObjectIdMap = new HashSet<>(subscrContObjectIds);
        contObjects.forEach(i -> {
            boolean haveSubscr = subscrContObjectIdMap.contains(i.getId());
            i.set_haveSubscr(haveSubscr);
        });
    }


    /// ContZPoints

    public List<ContZPoint> findAllContZPoints(Long subscriberId) {
        List<ContZPoint> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllContZPointsBySubscriberId(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContZPoints(subscriberId);
        }
        return ObjectFilters.deletedFilter(result);
    }

    public List<ContZPoint> findAllContZPoints(SubscriberParam subscriberParam, Long contObjectId) {
        List<ContZPoint> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllContZPointsBySubscriberId(subscriberParam.getSubscriberId(), contObjectId);
        } else {
            result = subscrContObjectRepository.selectContZPoints(subscriberParam.getSubscriberId());
        }
        return ObjectFilters.deletedFilter(result);
    }

    public List<Long> findAllContZPointIds(Long subscriberId) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllContZPointIds(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContZPointIds(subscriberId);
        }
        return result;
    }

    public List<ContZPointShortInfo> findContZPointShortInfo(Long subscriberId) {
        List<ContZPointShortInfo> result;
        if (NEW_ACCESS) {
            result = findContZPointsShortInfoNew(subscriberId);
        } else {
            result = findContZPointsShortInfoOld(subscriberId);
        }
        return result;
    }


    private List<ContZPointShortInfo> findContZPointsShortInfoNew(Long subscriberId) {
        checkNotNull(subscriberId);
        List<ContZPointShortInfo> result = new ArrayList<>();

        String[] QUERY_COLUMNS = new String[] { "id", "contObjectId", "customServiceName", "contServiceTypeKeyname",
            "caption" };

        ColumnHelper columnHelper = new ColumnHelper(QUERY_COLUMNS);

        List<Object[]> queryResult = contZPointAccessRepository.findAllContZPointShortInfo(subscriberId);

        for (Object[] row : queryResult) {

            Long contZPointId = columnHelper.getResultAsClass(row, "id", Long.class);
            Long contObjectId = columnHelper.getResultAsClass(row, "contObjectId", Long.class);
            String customServiceName = columnHelper.getResultAsClass(row, "customServiceName", String.class);
            String contServiceType = columnHelper.getResultAsClass(row, "contServiceTypeKeyname", String.class);
            String contServiceTypeCaption = columnHelper.getResultAsClass(row, "caption", String.class);


            ContZPointShortInfo shortInfo = ContZPointDTO.ShortInfo.builder()
                .contZPointId(contZPointId)
                .contObjectId(contObjectId)
                .customServiceName(customServiceName)
                .contServiceType(contServiceType)
                .contServiceTypeCaption(contServiceTypeCaption).build();
            result.add(shortInfo);
        }

        return result;
    }

    private List<ContZPointShortInfo> findContZPointsShortInfoOld(Long subscriberId) {
        checkNotNull(subscriberId);
        List<ContZPointShortInfo> result = new ArrayList<>();

        String[] QUERY_COLUMNS = new String[] { "id", "contObjectId", "customServiceName", "contServiceTypeKeyname",
            "caption" };

        ColumnHelper columnHelper = new ColumnHelper(QUERY_COLUMNS);

        List<Object[]> queryResult = subscrContObjectRepository.selectContZPointShortInfo(subscriberId);

        for (Object[] row : queryResult) {

            Long contZPointId = columnHelper.getResultAsClass(row, "id", Long.class);
            Long contObjectId = columnHelper.getResultAsClass(row, "contObjectId", Long.class);
            String customServiceName = columnHelper.getResultAsClass(row, "customServiceName", String.class);
            String contServiceType = columnHelper.getResultAsClass(row, "contServiceTypeKeyname", String.class);
            String contServiceTypeCaption = columnHelper.getResultAsClass(row, "caption", String.class);
            ContZPointShortInfo shortInfo = ContZPointDTO.ShortInfo.builder()
                .contZPointId(contZPointId)
                .contObjectId(contObjectId)
                .customServiceName(customServiceName)
                .contServiceType(contServiceType)
                .contServiceTypeCaption(contServiceTypeCaption).build();
            result.add(shortInfo);
        }

        return result;
    }


    public boolean checkContZPointIds(Long subscriberId, List<Long> ContZPointIds) {
        if (ContZPointIds == null || ContZPointIds.isEmpty()) {
            return false;
        }
        List<Long> subscrContObjectIds = findAllContZPointIds(subscriberId);
        return AbstractService.checkIds(ContZPointIds, subscrContObjectIds);
    }


    public List<DeviceObject> findAllContZPointDeviceObjects(Long subscriberId) {

        List<DeviceObject> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllDeviceObjects(subscriberId);
        } else {
            result = subscrContObjectRepository.selectDeviceObjects(subscriberId);
        }

        result.forEach(i -> {
            i.loadLazyProps();
        });

        return result;
    }



    public List<Tuple> findAllContZPointDeviceObjectsEx (Long subscriberId, List<String> deviceObjectNumbers) {

        if (deviceObjectNumbers.isEmpty()) {
            return Collections.emptyList();
        }

        List<Tuple> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllDeviceObjectsEx(subscriberId, deviceObjectNumbers);
        } else {
            result = subscrContObjectRepository.selectSubscrDeviceObjectByNumber(subscriberId,deviceObjectNumbers);
        }
        return result;
    }

}
