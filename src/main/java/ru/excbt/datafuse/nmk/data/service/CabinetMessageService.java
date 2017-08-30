package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.CabinetMessage;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageType;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.model.ids.CabinetUserIds;
import ru.excbt.datafuse.nmk.data.repository.CabinetMessageRepository;
import ru.excbt.datafuse.nmk.data.service.support.DBExceptionUtils;
import ru.excbt.datafuse.nmk.service.mapper.CabinetMessageMapper;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service Implementation for managing CabinetMessage.
 */
@Service
@Transactional
public class CabinetMessageService {

    private final Logger log = LoggerFactory.getLogger(CabinetMessageService.class);

    private final CabinetMessageRepository cabinetMessageRepository;

    private final CabinetMessageMapper cabinetMessageMapper;

//    private final CabinetIdsService cabinetIdsService;

    public CabinetMessageService(CabinetMessageRepository cabinetMessageRepository, CabinetMessageMapper cabinetMessageMapper) {
        this.cabinetMessageRepository = cabinetMessageRepository;
        this.cabinetMessageMapper = cabinetMessageMapper;
    }

    /**
     * Save a cabinetMessage.
     *
     * @param cabinetMessageDTO the entity to save
     * @return the persisted entity
     */
    public CabinetMessageDTO save(CabinetMessageDTO cabinetMessageDTO, CabinetUserIds userIds) {
        log.debug("Request to save CabinetMessage : {}", cabinetMessageDTO);
        if (cabinetMessageDTO.getId() != null) {
            //throw new IllegalStateException("Can't save existing CabinetMessageDTO");
        }

        CabinetMessage cabinetMessage = cabinetMessageMapper.toEntity(cabinetMessageDTO);
        setFromFields(cabinetMessageDTO, userIds);
        cabinetMessage.setToPortalSubscriberId(userIds.getParentSubscriberId());
        if (cabinetMessage.getMessageDirection() == null) {
            cabinetMessage.setMessageDirection(CabinetMessage.DEFAULT_DIRECTION.name());
        }
        if (cabinetMessage.getMessageType() == null) {
            cabinetMessage.setMessageType(CabinetMessage.DEFAULT_TYPE.name());
        }

        cabinetMessage.setCreationDateTime(ZonedDateTime.now());

        cabinetMessage = cabinetMessageRepository.save(cabinetMessage);
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

    private static void setFromFields(CabinetMessageDTO cabinetMessageDTO, CabinetUserIds cabinetUserIds) {
        cabinetMessageDTO.setFromPortalSubscriberId(cabinetUserIds.getSubscriberId());
        cabinetMessageDTO.setFromPortalUserId(cabinetUserIds.getUserId());
    }


    /**
     *
     * @param cabinetMessageDTO
     * @param userIds
     * @return
     */
    public CabinetMessageDTO saveResponse(CabinetMessageDTO cabinetMessageDTO, CabinetUserIds userIds) {
        log.debug("Request to saveResponse CabinetMessage : {}", cabinetMessageDTO);
        if (cabinetMessageDTO.getResponseToId() == null) {
            throw new IllegalStateException("responseToId cannot be null");
        }

        CabinetMessage responseToMessage = cabinetMessageRepository.findOne(cabinetMessageDTO.getResponseToId());
        if (responseToMessage == null) {
            throw DBExceptionUtils.entityNotFoundException(CabinetMessage.class, cabinetMessageDTO.getResponseToId());
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
    public Page<CabinetMessageDTO> findAll(CabinetUserIds cabinetUserIds, Pageable pageable) {
        log.debug("Request to get all CabinetMessages");
        return cabinetMessageRepository.findByFromPortalSubscriberId(cabinetUserIds.getSubscriberId(),
            pageable).map(cabinetMessageMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CabinetMessageDTO> findAllRequest(CabinetUserIds userIds, Pageable pageable) {
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
        log.debug("Request to delete CabinetMessage : {}", id);
        cabinetMessageRepository.delete(id);
    }


    @Transactional(readOnly = true)
    public List<CabinetMessageDTO> findMessageChain(Long messageId) {
        log.debug("Request to get all CabinetMessages");
        CabinetMessage cabinetMessage = cabinetMessageRepository.findOne(messageId);

        if (cabinetMessage == null) {
            return Collections.emptyList();
        }

        return cabinetMessageRepository.findMessageChain(messageId).stream()
            .map(cabinetMessageMapper::toDto).collect(Collectors.toList());
    }

}
