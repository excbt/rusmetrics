package ru.excbt.datafuse.nmk.data.service;

import com.fasterxml.uuid.Generators;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.CabinetMessage;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageDirection;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageType;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.CabinetMessageRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.service.QueryDSLService;
import ru.excbt.datafuse.nmk.service.mapper.CabinetMessageMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.RepositoryUtil;

import java.sql.Connection;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Service Implementation for managing CabinetMessage.
 */
@Service
public class CabinetMessageService {

    private final Logger log = LoggerFactory.getLogger(CabinetMessageService.class);

    private final CabinetMessageRepository cabinetMessageRepository;

    private final CabinetMessageMapper cabinetMessageMapper;

    private final SubscriberRepository subscriberRepository;

    private final DBFDWSequence fdwSequence;

    private final QueryDSLService queryDSLService;

    public CabinetMessageService(CabinetMessageRepository cabinetMessageRepository, CabinetMessageMapper cabinetMessageMapper, SubscriberRepository subscriberRepository, QueryDSLService queryDSLService) {
        this.cabinetMessageRepository = cabinetMessageRepository;
        this.cabinetMessageMapper = cabinetMessageMapper;
        this.subscriberRepository = subscriberRepository;
        this.fdwSequence = new DBFDWSequence(queryDSLService, DBMetadata.SCHEME_CABINET2, 50);
        this.queryDSLService = queryDSLService;
    }

    // CustomQueryBuilder
    private static class QCustomCabinetMessagePath {

        private final static QCustomCabinetMessagePath instance = new QCustomCabinetMessagePath();

        private final RelationalPath<Object> userPath = new RelationalPathBase<>(Object.class, "cabinetMessage", DBMetadata.SCHEME_CABINET2, "cabinet_message");

        private final DateTimePath<ZonedDateTime> creationDateTime = Expressions.dateTimePath(ZonedDateTime.class, userPath, "creation_date_time");
        private final NumberPath<Long> fromPortalSubscriberId = Expressions.numberPath(Long.class, userPath, "from_portal_subscriber_id");
        private final NumberPath<Long> fromPortalUserId = Expressions.numberPath(Long.class, userPath, "from_portal_user_id");
        private final NumberPath<Long> id = Expressions.numberPath(Long.class, userPath, "id");
        private final NumberPath<Long> masterId = Expressions.numberPath(Long.class, userPath, "master_id");
        private final ComparablePath<UUID> masterUuid = Expressions.comparablePath(java.util.UUID.class, userPath, "master_uuid");
        private final StringPath messageBody = Expressions.stringPath(userPath, "message_body");
        private final StringPath messageDirection = Expressions.stringPath(userPath, "message_direction");
        private final StringPath messageSubject = Expressions.stringPath(userPath, "message_subject");
        private final StringPath messageType = Expressions.stringPath(userPath, "message_type");
        private final NumberPath<Long> responseToId = Expressions.numberPath(Long.class, userPath, "response_to_id");
        private final DateTimePath<ZonedDateTime> reviewDateTime = Expressions.dateTimePath(ZonedDateTime.class, userPath, "review_date_time");
        private final NumberPath<Long> toPortalSubscriberId = Expressions.numberPath(Long.class, userPath, "to_portal_subscriber_id");
        private final NumberPath<Long> toPortalUserId = Expressions.numberPath(Long.class, userPath, "to_portal_user_id");

    }

    /**
     *
     * @param cabinetMessage
     * @return
     */
    private Long insertCabinetMessageSQL(CabinetMessage cabinetMessage) {

        final Long seqId = fdwSequence.next();

        final QCustomCabinetMessagePath p = QCustomCabinetMessagePath.instance;
        log.debug("new cabinet message id: {}", seqId);
        queryDSLService.doWork((Connection c) -> {

            SQLInsertClause insert = new SQLInsertClause(c, QueryDSLService.templates, p.userPath);
            insert
                .set(p.id, seqId)
                .set(p.messageType, cabinetMessage.getMessageType())
                .set(p.messageDirection, cabinetMessage.getMessageDirection())
                .set(p.fromPortalSubscriberId, cabinetMessage.getFromPortalSubscriberId())
                .set(p.fromPortalUserId, cabinetMessage.getFromPortalUserId())
                .set(p.toPortalSubscriberId, cabinetMessage.getToPortalSubscriberId())
                .set(p.toPortalUserId, cabinetMessage.getToPortalUserId())
                .set(p.messageSubject, cabinetMessage.getMessageSubject())
                .set(p.messageBody, cabinetMessage.getMessageBody())
                .set(p.masterId, cabinetMessage.getMasterId())
                .set(p.responseToId, cabinetMessage.getResponseToId())
                .set(p.creationDateTime, cabinetMessage.getCreationDateTime())
                .set(p.reviewDateTime, cabinetMessage.getCreationDateTime())
                .set(p.masterUuid, cabinetMessage.getMasterUuid());

            insert.execute();
        });

        return seqId;
    }

    /**
     *
     * @param id
     * @param reviewDateTime
     * @return
     */
    private int updateCabinetMessageReviewDate(Long id, ZonedDateTime reviewDateTime) {
        final QCustomCabinetMessagePath p = QCustomCabinetMessagePath.instance;
        long count = queryDSLService.doReturningWork((Connection c) -> {
            SQLUpdateClause updateClause = new SQLUpdateClause(c, QueryDSLService.templates, p.userPath);
            updateClause.set(p.reviewDateTime, reviewDateTime).where(p.id.eq(id));
            long r = updateClause.execute();
            return r;
        });
        return Long.valueOf(count).intValue();
    }

    /**
     * Save a cabinetMessage.
     *
     * @param cabinetMessageDTO the entity to save
     * @return the persisted entity
     */
    @Transactional
    public CabinetMessageDTO save(CabinetMessageDTO cabinetMessageDTO, PortalUserIds userIds) {
        log.debug("Request to save CabinetMessage : {}", cabinetMessageDTO);
        if (cabinetMessageDTO.getId() != null) {
            //throw new IllegalStateException("Can't save existing CabinetMessageDTO");
        }

        setFromFields(cabinetMessageDTO, userIds);
        CabinetMessage cabinetMessage = cabinetMessageMapper.toEntity(cabinetMessageDTO);

        cabinetMessage.setToPortalSubscriberId(userIds.getParentSubscriberId());
        if (cabinetMessage.getMessageDirection() == null) {
            cabinetMessage.setMessageDirection(CabinetMessage.DEFAULT_DIRECTION.name());
        }
        if (cabinetMessage.getMessageType() == null) {
            cabinetMessage.setMessageType(CabinetMessage.DEFAULT_TYPE.name());
        }

        cabinetMessage.setCreationDateTime(ZonedDateTime.now());

        Long id = insertCabinetMessageSQL(cabinetMessage);

        cabinetMessageRepository.flush();

        cabinetMessage = cabinetMessageRepository.findOne(id);
        return cabinetMessageMapper.toDto(cabinetMessage);
    }



    private static void setResponseFields(CabinetMessageDTO cabinetMessageDTO, CabinetMessage responseToMessage) {
        cabinetMessageDTO.setToPortalSubscriberId(responseToMessage.getFromPortalSubscriberId());
        cabinetMessageDTO.setToPortalUserId(responseToMessage.getFromPortalUserId());
        cabinetMessageDTO.setMessageType(CabinetMessageType.RESPONSE.name());
        cabinetMessageDTO.setMasterId(CabinetMessageType.REQUEST.name().
            equals(responseToMessage.getMessageType()) ?
            responseToMessage.getId() : responseToMessage.getMasterId());
    }

    private static void setFromFields(CabinetMessageDTO cabinetMessageDTO, PortalUserIds userIds) {
        cabinetMessageDTO.setFromPortalSubscriberId(userIds.getSubscriberId());
        cabinetMessageDTO.setFromPortalUserId(userIds.getUserId());
    }


    /**
     *
     * @param cabinetMessageDTO
     * @param userIds
     * @return
     */
    @Transactional
    public CabinetMessageDTO saveResponse(CabinetMessageDTO cabinetMessageDTO, PortalUserIds userIds) {
        log.debug("Request to saveResponse CabinetMessage : {}", cabinetMessageDTO);
        if (cabinetMessageDTO.getResponseToId() == null) {
            throw new IllegalStateException("responseToId cannot be null");
        }

        CabinetMessage responseToMessage = cabinetMessageRepository.findOne(cabinetMessageDTO.getResponseToId());
        if (responseToMessage == null) {
            throw DBExceptionUtil.entityNotFoundException(CabinetMessage.class, cabinetMessageDTO.getResponseToId());
        }

        setFromFields (cabinetMessageDTO, userIds);
        setResponseFields(cabinetMessageDTO, responseToMessage);

        return save(cabinetMessageDTO, userIds);
    }

    /**
     *  Get all the cabinetMessages.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAll(PortalUserIds userIds, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByFromPortalSubscriberId(userIds.getSubscriberId(),
            pageable).map(cabinetMessageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAllRequest(PortalUserIds userIds, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByFromPortalSubscriberId(userIds.getSubscriberId(),
            CabinetMessageType.REQUEST.name(), pageable).map(cabinetMessageMapper::toDto);
    }


    /**
     *  Get one cabinetMessage by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public CabinetMessageDTO findOne(Long id) {
        log.debug("Request to get CabinetMessage : {}", id);
        CabinetMessage cabinetMessage = cabinetMessageRepository.findOne(id);
        return cabinetMessageMapper.toDto(cabinetMessage);
    }

    /**
     *  Delete the  cabinetMessage by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        throw new UnsupportedOperationException("Delete is not supported");
//        log.debug("Request to delete CabinetMessage : {}", id);
//        cabinetMessageRepository.delete(id);
    }


    @Transactional(readOnly = true)
    public List<CabinetMessageDTO> findMessageChain(Long messageId) {
        log.debug("Request to get all CabinetMessages");
        CabinetMessage cabinetMessage = cabinetMessageRepository.findOne(messageId);

        if (cabinetMessage == null) {
            return Collections.emptyList();
        }

        return cabinetMessageRepository.findMessageChainByMasterId(messageId).stream()
            .map(cabinetMessageMapper::toDto).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAllRequestToSubscriber(PortalUserIds userIds, CabinetMessageType cabinetMessageType, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByToSubscriberIds(RepositoryUtil.safeList(userIds.getSubscriberId()),
            cabinetMessageType != null ? cabinetMessageType.name() : CabinetMessageType.REQUEST.name(),
            pageable).map(cabinetMessageMapper::toDto);
    }


    @Transactional
    public List<Long> updateMessageChainReview(Long masterMessageId, PortalUserIds userIds, boolean resetReviews) {
        return cabinetMessageRepository.findMessageChainByMasterId(masterMessageId).stream()
            .filter(i -> (i.getReviewDateTime() == null && !resetReviews) ||
                (resetReviews && i.getReviewDateTime() != null))
            .filter(i -> userIds.getSubscriberId().equals(i.getToPortalSubscriberId()))
            .map(i -> {
                int cnt = updateCabinetMessageReviewDate(i.getId(), ZonedDateTime.now());
                return cnt > 0 ? i.getId() : null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    /**
     *
     * @param parentIds
     * @param messageSubject
     * @param messageBody
     * @param subscrCabinetIds
     * @return
     */
    @Transactional
    public UUID sendNotificationToCabinets(PortalUserIds parentIds, String messageSubject, String messageBody, List<Long> subscrCabinetIds) {

        final UUID masterUuid = Generators.timeBasedGenerator().generate();

        subscriberRepository.selectChildSubscribers(parentIds.getSubscriberId()).stream()
            .filter(i -> subscrCabinetIds == null || subscrCabinetIds.isEmpty() || subscrCabinetIds.contains(i.getId()))
            .forEach(s -> {
                CabinetMessage cabinetMessage = new CabinetMessage();
                cabinetMessage.setFromPortalSubscriberId(parentIds.getSubscriberId());
                cabinetMessage.setFromPortalUserId(parentIds.getUserId());
                cabinetMessage.setMessageDirection(CabinetMessageDirection.OUT.name());
                cabinetMessage.setMessageType(CabinetMessageType.NOTIFICATION.name());
                cabinetMessage.setToPortalSubscriberId(s.getId());
                cabinetMessage.setMessageSubject(messageSubject);
                cabinetMessage.setMessageBody(messageBody);
                cabinetMessage.setMasterUuid(masterUuid);
                cabinetMessage.setCreationDateTime(ZonedDateTime.now());
                Long messageId = insertCabinetMessageSQL(cabinetMessage);
                log.info("Cabinet Message Id: {}", messageId);

            });

        return masterUuid;
    }


}
