package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.domain.QAbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.service.dto.ContZPointAccessDTO;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectAccessMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointAccessMapper;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberAccessStatsMapper;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;
import ru.excbt.datafuse.nmk.service.vm.ContObjectAccessVM;
import ru.excbt.datafuse.nmk.service.vm.ContZPointAccessVM;
import ru.excbt.datafuse.nmk.service.vm.SubscriberAccessStatsVM;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ContObjectAccessService {

    private final static QContObjectAccess qContObjectAccess = QContObjectAccess.contObjectAccess;
    private final static QContZPointAccess qContZPointAccess = QContZPointAccess.contZPointAccess;
    private final static QContZPoint qContZPoint = QContZPoint.contZPoint;

    private final ContObjectAccessRepository contObjectAccessRepository;
    private final ContObjectAccessMapper contObjectAccessMapper;
    private final ContZPointAccessRepository contZPointAccessRepository;
    private final ContZPointAccessMapper contZPointAccessMapper;

    private final QueryDSLService queryDSLService;
    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;
    private final SubscriberAccessStatsMapper subscriberAccessStatsMapper;

    private final SubscriberAccessService subscriberAccessService;

    public ContObjectAccessService(ContObjectAccessRepository contObjectAccessRepository, ContObjectAccessMapper contObjectAccessMapper, ContZPointAccessRepository contZPointAccessRepository, ContZPointAccessMapper contZPointAccessMapper, QueryDSLService queryDSLService, SubscriberRepository subscriberRepository, SubscriberMapper subscriberMapper, SubscriberAccessStatsMapper subscriberAccessStatsMapper, SubscriberAccessService subscriberAccessService) {
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessMapper = contObjectAccessMapper;
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contZPointAccessMapper = contZPointAccessMapper;
        this.queryDSLService = queryDSLService;
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
        this.subscriberAccessStatsMapper = subscriberAccessStatsMapper;
        this.subscriberAccessService = subscriberAccessService;
    }

    private static BooleanExpression searchCondition(String s) {
        if (s.isEmpty()) {
            return null;
        }
        return qContObjectAccess.contObject().name.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s))
            .or(qContObjectAccess.contObject().fullName.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s)))
            .or(qContObjectAccess.contObject().fullAddress.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s)));
    }

    private Page<ContObjectAccess> searchContObjectAccess(Long subscriberId,
                                                          String searchString,
                                                          Pageable pageable) {

        BooleanExpression subscriberFilter = qContObjectAccess.subscriberId.eq(subscriberId);

        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilter)
            .and(qContObjectAccess.revokeTz.isNull());

        Optional.ofNullable(searchString).ifPresent(s -> where.and(searchCondition(s)));

        Page<ContObjectAccess> resultPage = contObjectAccessRepository.findAll(where,pageable);

        return resultPage;
    }

    @Transactional(readOnly = true)
    public Page<ContObjectAccessVM> getContObjectAccessVMPage(PortalUserIds portalUserIds,
                                                              Long subscriberId,
                                                              String searchString,
                                                              Pageable pageable) {

        Long id = Optional.ofNullable(subscriberId).orElse(portalUserIds.getSubscriberId());

        Page<ContObjectAccess> accessPage = searchContObjectAccess(id, searchString, pageable);

        Page<ContObjectAccessVM> accessVMPage = accessPage.map(contObjectAccessMapper::toVM);

        accessVMPage.forEach(i -> {
            i.setAccessContZPointCnt(findContZPointAccessCnt(i.getSubscriberId(), i.getContObjectId()));
            i.setAllContZPointCnt(findAllContZPointCnt(i.getContObjectId()));
            i.setAccessEnabled(true);
        });

        return accessVMPage;
    }


    private int findContZPointAccessCnt(Long subscriberId, Long contObjectId) {

        BooleanExpression subscriberFilter = qContZPointAccess.subscriberId.eq(subscriberId);
        BooleanExpression contObjectFilter = qContZPointAccess.contZPoint().contObjectId.eq(contObjectId)
            .and(qContZPointAccess.contZPoint().deleted.eq(0));

        WhereClauseBuilder whereBuilder = new WhereClauseBuilder()
            .and(subscriberFilter).and(contObjectFilter)
            .and(qContZPointAccess.revokeTz.isNull());

        List<Long> count= queryDSLService.queryFactory().select(qContZPointAccess.contZPoint().count()).from(qContZPointAccess).
            where(whereBuilder).fetch();

        return QueryDSLUtil.getCountValue(count);

    }

    /**
     *
     * @param contObjectId
     * @return
     */
    private int findAllContZPointCnt(Long contObjectId) {
        BooleanExpression wherePredicate = qContZPoint.contObject().id.eq(contObjectId).and(qContZPoint.deleted.eq(0));
        List<Long> count = queryDSLService.queryFactory().select(qContZPoint.id.count()).from(qContZPoint).where(wherePredicate).fetch();
        return QueryDSLUtil.getCountValue(count);
    }

    /**
     *
     * @param contObjectId
     * @return
     */
    private List<ContZPoint> findAllContZPoints(Long contObjectId) {
        BooleanExpression wherePredicate = qContZPoint.contObject().id.eq(contObjectId).and(qContZPoint.deleted.eq(0));
        List<ContZPoint> resultList = queryDSLService.queryFactory().select(qContZPoint).from(qContZPoint).where(wherePredicate).fetch();
        return resultList;
    }

    /**
     *
     * @param subscriberId
     * @param contObjectId
     * @return
     */
    private List<ContZPointAccess> findContZPointAccess(Long subscriberId, Long contObjectId) {
        BooleanExpression subscriberFilter = qContZPointAccess.subscriberId.eq(subscriberId);
        BooleanExpression contObjectFilter = qContZPointAccess.contZPoint().contObjectId.eq(contObjectId)
            .and(qContZPointAccess.contZPoint().deleted.eq(0));

        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilter)
            .and(contObjectFilter);

        List<ContZPointAccess> resultList = new ArrayList<>();
        contZPointAccessRepository.findAll(where).forEach(resultList::add);
        return resultList;
    }

//    /**
//     *
//     * @param portalUserIds
//     * @param contObjectId
//     * @return
//     */
//    @Transactional(readOnly = true)
//    public List<ContZPointAccessDTO> getContZPointAccess(PortalUserIds portalUserIds,
//                                                         Long contObjectId) {
//        List<ContZPointAccess> resultList = findContZPointAccess(portalUserIds.getSubscriberId(), contObjectId);
//        return resultList.stream().map(contZPointAccessMapper::toDto).collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<ContZPointAccessVM> getContZPointAccessVM(PortalUserIds portalUserIds,
                                                          Long subscriberId,
                                                          Long contObjectId) {

        Long qrySubscriberId = Optional.ofNullable(subscriberId).orElse(portalUserIds.getSubscriberId());

        List<ContZPointAccess> accessList = findContZPointAccess(qrySubscriberId, contObjectId);
        List<ContZPoint> zPointList = findAllContZPoints(contObjectId);
        List<Long> accessIds = accessList.stream().map(ContZPointAccess::getContZPointId).collect(Collectors.toList());
        List<ContZPointAccessVM> resultVMList = accessList.stream().map(contZPointAccessMapper::toVM).collect(Collectors.toList());;

        resultVMList.forEach(i -> {
            boolean accessEnabled = i.getRevokeTz() == null;
            i.setAccessEnabled(accessEnabled);
            if (!accessEnabled) {
                i.setGrantTz(null);
            }
        });
        zPointList.forEach(i -> {
            if (!accessIds.contains(i.getId())) {
                ContZPointAccessVM availableAccess = new ContZPointAccessVM();
                availableAccess.setSubscriberId(null);
                availableAccess.setContZPointId(i.getId());
                availableAccess.setContServiceTypeKeyname(i.getContServiceTypeKeyname());
                availableAccess.setContZPointCustomServiceName(i.getCustomServiceName());
                availableAccess.setAccessEnabled(false);
                resultVMList.add(availableAccess);
            }
        });
        Comparator<ContZPointAccessVM> c = Comparator.comparing(ContZPointAccessVM::getContServiceTypeKeyname,
            Comparator.nullsLast(Comparator.naturalOrder()));
        resultVMList.sort(c);
        return resultVMList;
    }

    /**
     *
     * @param portalUserIds
     * @param subscriberConsumer
     */
    private void subscriberAccessLoader(PortalUserIds portalUserIds, Consumer<Subscriber> subscriberConsumer) {
        QSubscriber qSubscriber = QSubscriber.subscriber;
        QAbstractPersistableEntity qPersistableEntity = new QAbstractPersistableEntity(qSubscriber);
        BooleanExpression subscriberFilter = qSubscriber.rmaSubscriberId.eq(portalUserIds.getSubscriberId());
        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(qSubscriber.deleted.eq(0))
            .and(qPersistableEntity.id.ne(portalUserIds.getSubscriberId()))
            .and(subscriberFilter);

        Sort sorting = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        subscriberRepository.findAll(where,sorting).forEach(subscriberConsumer);
    }


    /**
     *
     * @param userIds
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public List<SubscriberVM> findSubscribersManageList(PortalUserIds userIds) {
        List<SubscriberVM> resultList = new ArrayList<>();
        subscriberAccessLoader(userIds, i -> resultList.add(subscriberMapper.toVM(i)));
        return resultList;
    }

    /**
     *
     * @param userIds
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public List<SubscriberAccessStatsVM> findSubscriberAccessManageList(PortalUserIds userIds) {
        List<SubscriberAccessStatsVM> subscriberList = new ArrayList<>();
        subscriberAccessLoader(userIds, i -> subscriberList.add(subscriberAccessStatsMapper.toVM(i)));

        List<Long> subscriberIds = subscriberList.stream().map(SubscriberAccessStatsVM::getId)
            .filter(Objects::nonNull).collect(Collectors.toList());

        List<Tuple> statsList = queryDSLService.queryFactory()
            .select(qContObjectAccess.subscriberId, qContObjectAccess.contObjectId.count())
            .from(qContObjectAccess)
            .where(qContObjectAccess.subscriberId.in(subscriberIds))
            .groupBy(qContObjectAccess.subscriberId)
            .fetch();

        Map<Long, Long> statsMap = statsList.stream().collect(Collectors.toMap(i -> i.get(qContObjectAccess.subscriberId), i -> i.get(qContObjectAccess.contObjectId.count())));
        subscriberList.forEach(i -> Optional.ofNullable(statsMap.get(i.getId())).ifPresent(v -> i.setTotalObjects(v.intValue())));
        return subscriberList;
    }

    /**
     *
     * @param subscriberId
     * @param searchString
     * @return
     */
    private List<ContObjectAccess> findContObjectAccessList(Long subscriberId, String searchString) {
        BooleanExpression subscriberFilterForRma = qContObjectAccess.subscriberId.eq(subscriberId);
        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilterForRma)
            .and(qContObjectAccess.revokeTz.isNull());
        Optional.ofNullable(searchString).ifPresent(s -> where.and(searchCondition(s)));
        List<ContObjectAccess> resultList = new ArrayList<>();
        contObjectAccessRepository.findAll(where).forEach(resultList::add);
        return resultList;
    }

    /**
     *
     * @param allAccessVM
     * @param subscrAccess
     */
    private void fillupAccessVM(Iterable<ContObjectAccessVM> allAccessVM, Iterable<ContObjectAccess> subscrAccess) {
        Map<Long, ContObjectAccess> susbcrAccessMap = StreamSupport.stream(subscrAccess.spliterator(), false)
            .collect(Collectors.toMap(i -> i.getContObjectId(), Function.identity()));

        allAccessVM.forEach(i -> {
            i.setAllContZPointCnt(findAllContZPointCnt(i.getContObjectId()));
            ContObjectAccess sAccess = susbcrAccessMap.get(i.getContObjectId());
            boolean accessFound = sAccess != null;
            if (accessFound) {
                i.setSubscriberId(sAccess.getSubscriberId());
                i.setAccessContZPointCnt(findContZPointAccessCnt(i.getSubscriberId(), i.getContObjectId()));
                i.setGrantTz(sAccess.getGrantTz());
                i.setAccessTtl(sAccess.getAccessTtl());
                i.setAccessType(sAccess.getAccessType());
                i.setAccessEnabled(sAccess.getRevokeTz() == null);
            } else {
                i.setAccessContZPointCnt(0);
                i.setGrantTz(null);
                i.setAccessTtl(null);
                i.setAccessType(null);
                i.setAccessEnabled(false);
                i.setSubscriberId(null);
            }
        });
    }

    /**
     *
     * @param portalUserIds
     * @param subscriberId
     * @param searchString
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public Page<ContObjectAccessVM> findAvailableContObjectAccess(PortalUserIds portalUserIds, Long subscriberId, String searchString, Pageable pageable) {
        Page<ContObjectAccess> allAccessPage = searchContObjectAccess(portalUserIds.getSubscriberId(), searchString, pageable);
        List<ContObjectAccess> allContObjectsForSubscriber = findContObjectAccessList(subscriberId, searchString);

        Page<ContObjectAccessVM> accessVMPage = allAccessPage.map(contObjectAccessMapper::toVM);
        fillupAccessVM(accessVMPage, allContObjectsForSubscriber);

        return accessVMPage;
    }

    /**
     *
     * @param portalUserIds
     * @param subscriberId
     * @param contObjectId
     * @return
     */
    @Transactional
    public boolean grantContObjectAccess(PortalUserIds portalUserIds, Long subscriberId, Long contObjectId) {
        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (subscriber == null) {
            return false;
        }

        if (!portalUserIds.getSubscriberId().equals(subscriber.getRmaSubscriberId())) {
            return false;
        }

        {
            WhereClauseBuilder rmaWhereBuilder = new WhereClauseBuilder()
                .and(qContObjectAccess.subscriberId.eq(portalUserIds.getSubscriberId()))
                .and(qContObjectAccess.contObjectId.eq(contObjectId));

            List<ContObjectAccess> checkRmaAccess = new ArrayList<>();
            contObjectAccessRepository.findAll(rmaWhereBuilder).forEach(checkRmaAccess::add);
            if (checkRmaAccess.size() <= 0) {
                return false;
            }


            if (SubscriberAccessService.TRIAL_ACCESS.equals(checkRmaAccess.get(0).getAccessType())) {
                return false;
            }

        }
        subscriberAccessService.grantContObjectAccess(new ContObject().id(contObjectId), new Subscriber().id(subscriberId));
        return true;
    }


    /**
     *
     * @param portalUserIds
     * @param subscriberId
     * @param contObjectId
     * @return
     */
    @Transactional
    public boolean revokeContObjectAccess(PortalUserIds portalUserIds, Long subscriberId, Long contObjectId) {
        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (subscriber == null) {
            return false;
        }

        if (!portalUserIds.getSubscriberId().equals(subscriber.getRmaSubscriberId())) {
            return false;
        }

        subscriberAccessService.revokeContObjectAccess(new ContObject().id(contObjectId), new Subscriber().id(subscriberId));
        return true;
    }


    @Transactional
    public boolean grantContZPointAccess(PortalUserIds portalUserIds, Long subscriberId, Long contZPointId) {
        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (subscriber == null) {
            return false;
        }

        if (!portalUserIds.getSubscriberId().equals(subscriber.getRmaSubscriberId())) {
            return false;
        }

        {
            WhereClauseBuilder rmaWhereBuilder = new WhereClauseBuilder()
                .and(qContZPointAccess.subscriberId.eq(portalUserIds.getSubscriberId()))
                .and(qContZPointAccess.contZPointId.eq(contZPointId));

            List<ContZPointAccess> checkRmaAccess = new ArrayList<>();
            contZPointAccessRepository.findAll(rmaWhereBuilder).forEach(checkRmaAccess::add);
            if (checkRmaAccess.size() <= 0) {
                return false;
            }

            if (SubscriberAccessService.TRIAL_ACCESS.equals(checkRmaAccess.get(0).getAccessType())) {
                return false;
            }

        }
        subscriberAccessService.grantContZPointAccess(new ContZPoint().id(contZPointId), new Subscriber().id(subscriberId));

        return true;
    }


    /**
     *
     * @param portalUserIds
     * @param subscriberId
     * @param contZPointId
     * @return
     */
    @Transactional
    public boolean revokeContZPointAccess(PortalUserIds portalUserIds, Long subscriberId, Long contZPointId) {
        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (subscriber == null) {
            return false;
        }

        if (!portalUserIds.getSubscriberId().equals(subscriber.getRmaSubscriberId())) {
            return false;
        }

        subscriberAccessService.revokeContZPointAccess(new ContZPoint().id(contZPointId), new Subscriber().id(subscriberId));
        return true;
    }
}
