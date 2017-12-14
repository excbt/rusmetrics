package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.*;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointIdPair;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.data.model.support.ObjectAccess;
import ru.excbt.datafuse.nmk.data.model.support.ObjectAccessInitializer;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;
import ru.excbt.datafuse.nmk.service.utils.ObjectAccessUtil;

import javax.persistence.Tuple;
import java.util.*;
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

    private final ContZPointAccessRepository contZPointAccessRepository;

    private final ContGroupService contGroupService;

    private final SubscrServiceAccessService subscrServiceAccessService;

    private final ContObjectMapper contObjectMapper;

    private final ContZPointMapper contZPointMapper;

    public ObjectAccessService(SubscrContObjectRepository subscrContObjectRepository,
                               ContObjectAccessRepository contObjectAccessRepository,
                               ContZPointAccessRepository contZPointAccessRepository,
                               ContGroupService contGroupService,
                               SubscrServiceAccessService subscrServiceAccessService, ContObjectMapper contObjectMapper, ContZPointMapper contZPointMapper) {
        this.subscrContObjectRepository = subscrContObjectRepository;
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contGroupService = contGroupService;
        this.subscrServiceAccessService = subscrServiceAccessService;
        this.contObjectMapper = contObjectMapper;
        this.contZPointMapper = contZPointMapper;
    }



    public ObjectAccessUtil objectAccessUtil() {
        return new ObjectAccessUtil(this);
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

    public List<Long> findContObjectIds(PortalUserIds portalUserIds) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contObjectAccessRepository.findAllContObjectIds(portalUserIds.getSubscriberId());
        } else {
            result = subscrContObjectRepository.selectContObjectIds(portalUserIds.getSubscriberId());
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


    public boolean checkContObjectId (Long contObjectId, Subscriber subscriber) {
        Objects.requireNonNull(contObjectId);
        Objects.requireNonNull(subscriber);
        Objects.requireNonNull(subscriber.getId());
        if (NEW_ACCESS) {
            return contObjectAccessRepository.findByPK(subscriber.getId(), contObjectId).isPresent();
        } else {
            return subscrContObjectRepository.selectContObjectId(subscriber.getId(), contObjectId).size() > 0;
        }
    }

    public boolean checkContObjectId (Long contObjectId, PortalUserIds portalUserIds) {
        if (NEW_ACCESS) {
            return contObjectAccessRepository.findByPK(portalUserIds.getSubscriberId(), contObjectId).isPresent();
        } else {
            return subscrContObjectRepository.selectContObjectId(portalUserIds.getSubscriberId(), contObjectId).size() > 0;
        }
    }

    public boolean checkContObjectIds(List<Long> contObjectIds, Subscriber subscriber) {
        Objects.requireNonNull(subscriber);
        Objects.requireNonNull(subscriber.getId());

        if (contObjectIds == null || contObjectIds.isEmpty()) {
            return false;
        }
        List<Long> subscrContObjectIds = findContObjectIds(subscriber.getId());
        return ObjectAccessUtil.checkIds(contObjectIds, subscrContObjectIds);
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
    public void readContObjectAccess(Long subscriberId, List<? extends ObjectAccessInitializer> contObjectDTOS) {
        Objects.requireNonNull(contObjectDTOS);
        List<ContObjectAccess> accesses = contObjectAccessRepository.findBySubscriberId(subscriberId);
        Map<Long, ContObjectAccess> accessMap = new HashMap<>();
        accesses.forEach((i) -> accessMap.put(i.getContObjectId(), i));
        contObjectDTOS.forEach((co) -> {
            Optional.ofNullable(accessMap.get(co.getId())).ifPresent((a) -> {
                if (a.getAccessTtl() != null)
                    co.objectAccess(ObjectAccess.AccessType.TRIAL, a.getAccessTtl().toLocalDate());
            });
        });
    }

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public void setupRmaHaveSubscr(final SubscriberParam subscriberParam, final List<ContObject> contObjects) {
        Objects.requireNonNull(subscriberParam);
        Objects.requireNonNull(contObjects);

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
        Objects.requireNonNull(subscriberParam);
        Objects.requireNonNull(contObjects);

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

    public List<ContZPointDTO> findAllContZPoints(Long subscriberId) {
        List<ContZPoint> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllContZPointsBySubscriberId(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContZPoints(subscriberId);
        }
        return result.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(p -> contZPointMapper.toDto(p)).collect(Collectors.toList());
    }



    /**
     *
      * @param contObjectId
     * @param portalUserIds
     * @return
     */
    public List<ContZPoint> findAllContZPoints(Long contObjectId, PortalUserIds portalUserIds) {
        List<ContZPoint> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllContZPointsBySubscriberId(portalUserIds.getSubscriberId(), contObjectId);
        } else {
            result = subscrContObjectRepository.selectContZPoints(portalUserIds.getSubscriberId());
        }
        return ObjectFilters.deletedFilter(result);
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    public List<Long> findAllContZPointIds(Long subscriberId) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllContZPointIds(subscriberId);
        } else {
            result = subscrContObjectRepository.selectContZPointIds(subscriberId);
        }
        return result;
    }

    public List<Long> findAllContZPointIds(PortalUserIds portalUserIds) {
        List<Long> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllContZPointIds(portalUserIds.getSubscriberId());
        } else {
            result = subscrContObjectRepository.selectContZPointIds(portalUserIds.getSubscriberId());
        }
        return result;
    }


    public List<ContZPointIdPair> findAllContZPointPairIds(PortalUserIds portalUserIds) {

        Objects.requireNonNull(portalUserIds);
        List<ContZPointIdPair> result = new ArrayList<>();

        ColumnHelper columnHelper = new ColumnHelper("id", "contObjectId");

        List<Object[]> queryResult = contZPointAccessRepository.findAllContZPointIdPairs(portalUserIds.getSubscriberId());

        for (Object[] row : queryResult) {

            Long contZPointId = columnHelper.getResultAsClass(row, "id", Long.class);
            Long contObjectId = columnHelper.getResultAsClass(row, "contObjectId", Long.class);
            ContZPointShortInfo shortInfo = ContZPointShortInfoVM.builder()
                .contZPointId(contZPointId)
                .contObjectId(contObjectId)
                .build();
            result.add(shortInfo);
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
        Objects.requireNonNull(subscriberId);
        List<ContZPointShortInfo> result = new ArrayList<>();

        ColumnHelper columnHelper = new ColumnHelper("id", "contObjectId", "customServiceName", "contServiceTypeKeyname",
            "caption");

        List<Object[]> queryResult = contZPointAccessRepository.findAllContZPointShortInfo(subscriberId);

        for (Object[] row : queryResult) {

            Long contZPointId = columnHelper.getResultAsClass(row, "id", Long.class);
            Long contObjectId = columnHelper.getResultAsClass(row, "contObjectId", Long.class);
            String customServiceName = columnHelper.getResultAsClass(row, "customServiceName", String.class);
            String contServiceType = columnHelper.getResultAsClass(row, "contServiceTypeKeyname", String.class);
            String contServiceTypeCaption = columnHelper.getResultAsClass(row, "caption", String.class);

            ContZPointShortInfo shortInfo = ContZPointShortInfoVM.builder()
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
        Objects.requireNonNull(subscriberId);
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
            ContZPointShortInfo shortInfo = ContZPointShortInfoVM.builder()
                .contZPointId(contZPointId)
                .contObjectId(contObjectId)
                .customServiceName(customServiceName)
                .contServiceType(contServiceType)
                .contServiceTypeCaption(contServiceTypeCaption).build();
            result.add(shortInfo);
        }

        return result;
    }


    public boolean checkContZPointIds(List<Long> ContZPointIds, PortalUserIds portalUserIds) {
        Objects.requireNonNull(portalUserIds);
        if (ContZPointIds == null || ContZPointIds.isEmpty()) {
            return false;
        }
        List<Long> subscrContObjectIds = findAllContZPointIds(portalUserIds.getSubscriberId());
        return ObjectAccessUtil.checkIds(ContZPointIds, subscrContObjectIds);
    }

    /**
     *
     * @param contZPointId
     * @param portalUserIds
     * @return
     */
    public boolean checkContZPointId (Long contZPointId, PortalUserIds portalUserIds) {
        return checkContZPointIds(Arrays.asList(contZPointId), portalUserIds);
    }



    public List<DeviceObject> findAllContZPointDeviceObjects(Long subscriberId) {

        List<DeviceObject> result;
        if (NEW_ACCESS) {
            result = contZPointAccessRepository.findAllDeviceObjects(subscriberId);
        } else {
            result = subscrContObjectRepository.selectDeviceObjects(subscriberId);
        }

//        result.forEach(i -> {
//            i.loadLazyProps();
//        });

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
