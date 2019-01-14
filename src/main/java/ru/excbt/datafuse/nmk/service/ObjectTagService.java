package ru.excbt.datafuse.nmk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;
import ru.excbt.datafuse.nmk.data.model.ObjectTagInfo;
import ru.excbt.datafuse.nmk.data.util.GroupUtil;
import ru.excbt.datafuse.nmk.service.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagGlobalRepository;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagInfoRepository;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagRepository;
import ru.excbt.datafuse.nmk.service.dto.ObjectTagInfoDTO;
import ru.excbt.datafuse.nmk.service.mapper.ObjectTagGlobalMapper;
import ru.excbt.datafuse.nmk.service.mapper.ObjectTagInfoMapper;
import ru.excbt.datafuse.nmk.service.mapper.ObjectTagMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ObjectTagService {

    private static final Logger log = LoggerFactory.getLogger(ObjectTagService.class);

    private final ObjectTagRepository objectTagRepository;

    private final ObjectTagInfoRepository objectTagInfoRepository;

    private final ObjectTagGlobalRepository tagGlobalRepository;

    private final ObjectTagMapper objectTagMapper;

    private final ObjectTagInfoMapper objectTagInfoMapper;

    private final ObjectTagGlobalMapper objectTagGlobalMapper;

    @Autowired
    public ObjectTagService(ObjectTagRepository objectTagRepository, ObjectTagInfoRepository objectTagInfoRepository, ObjectTagGlobalRepository tagGlobalRepository, ObjectTagMapper objectTagMapper, ObjectTagInfoMapper objectTagInfoMapper, ObjectTagGlobalMapper objectTagGlobalMapper) {
        this.objectTagRepository = objectTagRepository;
        this.objectTagInfoRepository = objectTagInfoRepository;
        this.tagGlobalRepository = tagGlobalRepository;
        this.objectTagMapper = objectTagMapper;
        this.objectTagInfoMapper = objectTagInfoMapper;
        this.objectTagGlobalMapper = objectTagGlobalMapper;
    }

    @Transactional (readOnly = true)
    public List<ObjectTagDTO> findAllObjectsTags(String objectKeyname, PortalUserIds portalUserIds) {
        return objectTagRepository.findAllObjectsTags(portalUserIds.getSubscriberId(), objectKeyname)
            .stream().map(i -> objectTagMapper.toDto(i)).collect(Collectors.toList());
    }

    @Transactional (readOnly = true)
    public List<ObjectTagDTO> findObjectTags(String objectKeyname, Long objectId, PortalUserIds portalUserIds) {
        return objectTagRepository.findObjectTags(portalUserIds.getSubscriberId(), objectKeyname, objectId)
            .stream().map(i -> objectTagMapper.toDto(i)).collect(Collectors.toList());
    }


    @Transactional (readOnly = true)
    public List<String> findAllObjectsTagNames(String objectKeyname, PortalUserIds portalUserIds) {
        return objectTagRepository.findAllObjectTagNames(portalUserIds.getSubscriberId(), objectKeyname);
    }


    @Transactional
    public ObjectTagDTO saveTag(ObjectTagDTO dto, PortalUserIds portalUserIds) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(portalUserIds);

        ObjectTag.PK tagPK = objectTagMapper.toEntityPK(dto);
        tagPK.setSubscriberId(portalUserIds.getSubscriberId());
        ObjectTag tag = objectTagRepository.findOne(tagPK);
        if (tag == null) {
            tag = objectTagMapper.toEntity(dto);
            tag.setSubscriberId(portalUserIds.getSubscriberId());
            tag = objectTagRepository.saveAndFlush(tag);
        }
        return objectTagMapper.toDto(tag);
    }

    @Transactional
    public List<ObjectTagDTO> saveTags(List<ObjectTagDTO> objectTagDTOS, PortalUserIds portalUserIds) {
        Objects.requireNonNull(objectTagDTOS);
        Objects.requireNonNull(portalUserIds);

        //List<ObjectTagDTO> resultTags = new ArrayList<>();

        Set<String> objectTagKeynames = objectTagDTOS.stream()
            .filter(i -> i.getObjectTagKeyname() != null && !i.getObjectTagKeyname().isEmpty())
            .map(t -> t.getObjectTagKeyname()).collect(Collectors.toSet());

        if (objectTagKeynames.size() > 1) {
            throw new UnsupportedOperationException("Multiple tags is not supported");
        }

        if (objectTagKeynames.size() == 0) {
            log.warn("ObjectTag.objectTagKeyname is undefined");
            return Collections.emptyList();
        }

        List<ObjectTag> resultTags;

        resultTags = objectTagDTOS.stream()
            .filter(i -> i.getObjectTagKeyname() != null && !i.getObjectTagKeyname().isEmpty() &&
                i.getTagName() != null && !i.getTagName().isEmpty())
            .map(dto -> {
                ObjectTag.PK tagPK = objectTagMapper.toEntityPK(dto);
                tagPK.setSubscriberId(portalUserIds.getSubscriberId());
                ObjectTag tag = objectTagRepository.findOne(tagPK);
                if (tag == null) {
                    tag = objectTagMapper.toEntity(dto);
                    tag.setSubscriberId(portalUserIds.getSubscriberId());
                    tag = objectTagRepository.saveAndFlush(tag);
                }
                return tag;
            }).collect(Collectors.toList());

//        for (ObjectTagDTO dto : dtos) {
//            ObjectTag.PK tagPK = objectTagMapper.toEntityPK(dto);
//            tagPK.setSubscriberId(portalUserIds.getSubscriberId());
//            ObjectTag tag = objectTagRepository.findOne(tagPK);
//            if (tag == null) {
//                tag = objectTagMapper.toEntity(dto);
//                tag.setSubscriberId(portalUserIds.getSubscriberId());
//                tag = objectTagRepository.saveAndFlush(tag);
//            }
//            resultTags.add(objectTagMapper.toDto(tag));
//        }


        String objectTagKeyname = objectTagKeynames.iterator().next();

        Set<ObjectTag> existingTags = new HashSet<>(objectTagRepository.findAllObjectsTags(portalUserIds.getSubscriberId(), objectTagKeyname));
        Set<ObjectTag> savedTags = new HashSet<>(resultTags);

        existingTags.removeAll(savedTags);

        existingTags.stream().forEach( t -> {
            objectTagRepository.delete(t);
        });

        return resultTags.stream().map(t -> objectTagMapper.toDto(t)).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteTag(ObjectTagDTO dto, PortalUserIds portalUserIds) {
        ObjectTag.PK tagPK = objectTagMapper.toEntityPK(dto);
        tagPK.setSubscriberId(portalUserIds.getSubscriberId());
        ObjectTag tag = objectTagRepository.findOne(tagPK);
        if (tag == null) {
            return false;
        }
        return true;
    }


    @Transactional(readOnly = true)
    public List<ObjectTagInfoDTO> findAllObjectTagInfo(PortalUserIds portalUserIds) {

        Objects.requireNonNull(portalUserIds);

        return objectTagInfoRepository.findBySubscriberId(portalUserIds.getSubscriberId())
            .stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> objectTagInfoMapper.toDto(i)).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public ObjectTagInfoDTO findOneObjectTagInfo(ObjectTagInfoDTO objectTagInfoDTO, PortalUserIds portalUserIds) {

        Objects.requireNonNull(portalUserIds);

        ObjectTagInfo.PK pk = objectTagInfoMapper.toPK(objectTagInfoDTO);
        pk.setSubscriberId(portalUserIds.getSubscriberId());

        return Optional.ofNullable(objectTagInfoRepository.findOne(pk)).map(i -> objectTagInfoMapper.toDto(i)).orElse(null);

    }

    @Transactional
    public List<ObjectTagInfoDTO> saveObjectTagInfo(List<ObjectTagInfoDTO> tagInfoDTOS, PortalUserIds portalUserIds) {
        Objects.requireNonNull(tagInfoDTOS);
        Objects.requireNonNull(portalUserIds);

        List<ObjectTagInfo> newTagInfos = new ArrayList<>();
        for (ObjectTagInfoDTO dto : tagInfoDTOS) {
            ObjectTagInfo newObjectTagInfo = objectTagInfoMapper.toEntity(dto);
            newObjectTagInfo.setSubscriberId(portalUserIds.getSubscriberId());
            newTagInfos.add(newObjectTagInfo);
        }

        objectTagInfoRepository.save(newTagInfos);
        objectTagInfoRepository.flush();

        List<ObjectTagInfoDTO> resultTagInfo = objectTagInfoMapper.toDto(newTagInfos);

        return resultTagInfo;
    }

    @Transactional
    public void deleteObjectTagInfo(ObjectTagInfoDTO tagInfoDTO, PortalUserIds portalUserIds) {
        Objects.requireNonNull(tagInfoDTO);
        Objects.requireNonNull(portalUserIds);

        ObjectTagInfo.PK pk = objectTagInfoMapper.toPK(tagInfoDTO);
        pk.setSubscriberId(portalUserIds.getSubscriberId());

        ObjectTagInfo tagInfo = objectTagInfoRepository.findOne(pk);
        if (tagInfo != null) {
            tagInfo.setDeleted(1);
            objectTagInfoRepository.saveAndFlush(tagInfo);
        }
    }

    @Transactional(readOnly = true)
    public ObjectTagInfoDTO saveOneObjectTagInfo(ObjectTagInfoDTO objectTagInfoDTO, PortalUserIds portalUserIds) {

        Objects.requireNonNull(portalUserIds);

        ObjectTagInfo tagInfo = objectTagInfoMapper.toEntity(objectTagInfoDTO);
        tagInfo.setSubscriberId(portalUserIds.getSubscriberId());

        ObjectTagInfo savedTagInfo = objectTagInfoRepository.saveAndFlush(tagInfo);

        return objectTagInfoMapper.toDto(savedTagInfo);

    }


}
