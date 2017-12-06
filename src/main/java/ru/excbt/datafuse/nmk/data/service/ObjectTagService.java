package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;
import ru.excbt.datafuse.nmk.data.model.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagRepository;
import ru.excbt.datafuse.nmk.service.mapper.ObjectTagMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ObjectTagService {

    private final ObjectTagRepository objectTagRepository;

    private final ObjectTagMapper objectTagMapper;

    @Autowired
    public ObjectTagService(ObjectTagRepository objectTagRepository, ObjectTagMapper objectTagMapper) {
        this.objectTagRepository = objectTagRepository;
        this.objectTagMapper = objectTagMapper;
    }

    @Transactional (readOnly = true)
    public List<ObjectTagDTO> findTags(String objectKeyname, PortalUserIds portalUserIds) {
        return objectTagRepository.findBySubscriberAndObjectTagKeyname(portalUserIds.getSubscriberId(), objectKeyname)
            .stream().map(i -> objectTagMapper.toDto(i)).collect(Collectors.toList());
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

        List<ObjectTagDTO> resultTags = new ArrayList<>();

        for (ObjectTagDTO dto : dtos) {
            ObjectTag.PK tagPK = objectTagMapper.toEntityPK(dto);
            tagPK.setSubscriberId(portalUserIds.getSubscriberId());
            ObjectTag tag = objectTagRepository.findOne(tagPK);
            if (tag == null) {
                tag = objectTagMapper.toEntity(dto);
                tag.setSubscriberId(portalUserIds.getSubscriberId());
                tag = objectTagRepository.saveAndFlush(tag);
            }
            resultTags.add(objectTagMapper.toDto(tag));
        }

        return resultTags;
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

}
