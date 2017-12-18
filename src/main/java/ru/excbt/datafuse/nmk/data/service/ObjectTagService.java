package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagGlobalRepository;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagInfoRepository;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagRepository;
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

    @Autowired
    public ObjectTagService(ObjectTagRepository objectTagRepository, ObjectTagInfoRepository objectTagInfoRepository, ObjectTagGlobalRepository tagGlobalRepository, ObjectTagMapper objectTagMapper) {
        this.objectTagRepository = objectTagRepository;
        this.objectTagInfoRepository = objectTagInfoRepository;
        this.tagGlobalRepository = tagGlobalRepository;
        this.objectTagMapper = objectTagMapper;
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
    public List<ObjectTagDTO> saveTags(List<ObjectTagDTO> dtos, PortalUserIds portalUserIds) {
        Objects.requireNonNull(dtos);
        Objects.requireNonNull(portalUserIds);

        //List<ObjectTagDTO> resultTags = new ArrayList<>();

        Set<String> objectTagKeynames = dtos.stream()
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

        resultTags = dtos.stream()
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


    @Transactional
    public void findObjectTagInfo (PortalUserIds portalUserIds) {

        Objects.requireNonNull(portalUserIds);

        objectTagInfoRepository.findBySubscriberId(portalUserIds.getSubscriberId());

    }

}
