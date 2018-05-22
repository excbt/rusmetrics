package ru.excbt.datafuse.nmk.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.opensaml.xml.signature.Q;
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
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;
import ru.excbt.datafuse.nmk.service.vm.ContObjectAccessVM;
import ru.excbt.datafuse.nmk.service.vm.ContZPointAccessVM;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ContObjectAccessService(ContObjectAccessRepository contObjectAccessRepository, ContObjectAccessMapper contObjectAccessMapper, ContZPointAccessRepository contZPointAccessRepository, ContZPointAccessMapper contZPointAccessMapper, QueryDSLService queryDSLService, SubscriberRepository subscriberRepository, SubscriberMapper subscriberMapper) {
        this.contObjectAccessRepository = contObjectAccessRepository;
        this.contObjectAccessMapper = contObjectAccessMapper;
        this.contZPointAccessRepository = contZPointAccessRepository;
        this.contZPointAccessMapper = contZPointAccessMapper;
        this.queryDSLService = queryDSLService;
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
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
                                                          Optional<String> searchStringOptional,
                                                          Pageable pageable) {

        BooleanExpression subscriberFilter = qContObjectAccess.subscriberId.eq(subscriberId);


        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilter);

        searchStringOptional.ifPresent(s -> where.and(searchCondition(s)));

        Page<ContObjectAccess> resultPage = contObjectAccessRepository.findAll(where,pageable);

        return resultPage;
    }

    @Transactional(readOnly = true)
    public Page<ContObjectAccessVM> getContObjectAccessVMPage(PortalUserIds portalUserIds,
                                                              Optional<Long> optSubscriberId,
                                                              Optional<String> searchStringOptional,
                                                              Pageable pageable) {

        Long id = optSubscriberId.orElse(portalUserIds.getSubscriberId());

        Page<ContObjectAccess> accessPage = searchContObjectAccess(id, searchStringOptional, pageable);

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
            .and(subscriberFilter).and(contObjectFilter);

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
            .and(subscriberFilter).and(contObjectFilter);

        //Collections.
        List<ContZPointAccess> resultList = new ArrayList<>();
        contZPointAccessRepository.findAll(where).forEach(resultList::add);
        return resultList;
    }

    /**
     *
     * @param portalUserIds
     * @param contObjectId
     * @return
     */
    @Transactional(readOnly = true)
    public List<ContZPointAccessDTO> getContZPointAccess(PortalUserIds portalUserIds,
                                                         Long contObjectId) {
        List<ContZPointAccess> resultList = findContZPointAccess(portalUserIds.getSubscriberId(), contObjectId);
        return resultList.stream().map(contZPointAccessMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContZPointAccessVM> getContZPointAccessVM(PortalUserIds portalUserIds,
                                                          Optional<Long> optSubscriberId,
                                                          Long contObjectId) {

        Long subscriberId = optSubscriberId.orElse(portalUserIds.getSubscriberId());

        List<ContZPointAccess> accesList = findContZPointAccess(subscriberId, contObjectId);
        List<ContZPoint> zPointList = findAllContZPoints(contObjectId);
        List<Long> accessIds = accesList.stream().map(ContZPointAccess::getContZPointId).collect(Collectors.toList());
        List<ContZPointAccessVM> resultVMList = accesList.stream().map(contZPointAccessMapper::toVM).collect(Collectors.toList());;

        resultVMList.forEach(i -> i.setAccessEnabled(true));
        zPointList.forEach(i -> {
            if (!accessIds.contains(i.getId())) {
                ContZPointAccessVM availableAccess = new ContZPointAccessVM();
                availableAccess.setSubscriberId(subscriberId);
                availableAccess.setContZPointId(i.getId());
                availableAccess.setContServiceTypeKeyname(i.getContServiceTypeKeyname());
                availableAccess.setContZPointCustomServiceName(i.getCustomServiceName());
                availableAccess.setAccessEnabled(false);
                resultVMList.add(availableAccess);
            }
        });

        return resultVMList;
    }


    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public List<SubscriberVM> findSubscribersManageList(PortalUserIds userIds) {

        QSubscriber qSubscriber = QSubscriber.subscriber;
        QAbstractPersistableEntity qPersistableEntity = new QAbstractPersistableEntity(qSubscriber);

        BooleanExpression subscriberFilter = qSubscriber.rmaSubscriberId.eq(userIds.getSubscriberId());

        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(qSubscriber.deleted.eq(0))
            .and(qPersistableEntity.id.ne(userIds.getSubscriberId()))
            .and(subscriberFilter);

        Sort sorting = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<SubscriberVM> resultList = new ArrayList<>();
        subscriberRepository.findAll(where,sorting).forEach(i -> resultList.add(subscriberMapper.toVM(i)));

        return resultList;
    }
}
