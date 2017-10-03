package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.ptree.*;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.service.mapper.DeviceObjectMapper;

import java.util.List;

@Service
public class SubscrObjectPTreeNodeService extends AbstractService implements SecuredRoles {

    private final SubscrObjectTreeRepository subscrObjectTreeRepository;

    private final SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;

    private final ContObjectMapper contObjectMapper;

    private final ContZPointMapper contZPointMapper;

    private final ContZPointRepository contZPointRepository;

    private final DeviceObjectMapper deviceObjectMapper;


    @Autowired
    public SubscrObjectPTreeNodeService(SubscrObjectTreeRepository subscrObjectTreeRepository, SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository, ContObjectMapper contObjectMapper, ContZPointMapper contZPointMapper, ContZPointRepository contZPointRepository, DeviceObjectMapper deviceObjectMapper) {
        this.subscrObjectTreeRepository = subscrObjectTreeRepository;
        this.subscrObjectTreeContObjectRepository = subscrObjectTreeContObjectRepository;
        this.contObjectMapper = contObjectMapper;
        this.contZPointMapper = contZPointMapper;
        this.contZPointRepository = contZPointRepository;
        this.deviceObjectMapper = deviceObjectMapper;
    }


    public PTreeNode readSubscrObjectTree (Long subscrObjectTreeId) {
        SubscrObjectTree subscrObjectTree = subscrObjectTreeRepository.findOne(subscrObjectTreeId);

        PTreeElement pTreeElement = SubscrObjectTreeTools.makeFromSubscrObjectTree(subscrObjectTree);

        readChildSubscrObjectTree (pTreeElement, subscrObjectTree);

        return pTreeElement;
    }

    public PTreeNode readSubscrObjectTree (Long subscrObjectTreeId, Integer childLevel) {
        SubscrObjectTree subscrObjectTree = subscrObjectTreeRepository.findOne(subscrObjectTreeId);

        PTreeElement pTreeElement = SubscrObjectTreeTools.makeFromSubscrObjectTree(subscrObjectTree);

        readChildSubscrObjectTree (pTreeElement, subscrObjectTree, childLevel);

        return pTreeElement;
    }



    private void readChildSubscrObjectTree (PTreeElement pTreeElement, SubscrObjectTree subscrObjectTree, final Integer childLevel) {

        List<ContObject> contObjects = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTree.getId());

        for (ContObject contObject : contObjects) {
            PTreeContObjectNode pTreeContObjectNode = new PTreeContObjectNode(contObjectMapper.contObjectToDto(contObject));
            pTreeElement.addLinkedObject(pTreeContObjectNode);
            addContZPoints(pTreeContObjectNode, contObject);
        }

        boolean levelThreshold = childLevel != null && childLevel < 1;

        if (!levelThreshold) {
            for (SubscrObjectTree child : subscrObjectTree.getChildObjectList()) {
                readChildSubscrObjectTree (pTreeElement.addChildElement(SubscrObjectTreeTools.makeFromSubscrObjectTree(child)), child, childLevel != null ? childLevel - 1 : null);
            }
        } else {
            for (SubscrObjectTree child : subscrObjectTree.getChildObjectList()) {
                PTreeElement pChildTreeElement = SubscrObjectTreeTools.makeFromSubscrObjectTree(child);
                pChildTreeElement.setLazyNode(true);
                pTreeElement.addChildElement(pChildTreeElement);
            }
        }


    }



    public void readChildSubscrObjectTree (PTreeElement pTreeElement, SubscrObjectTree subscrObjectTree) {
        List<ContObject> contObjects = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTree.getId());

        for (ContObject contObject : contObjects) {
            if (contObject.getDeleted() != 0) continue;
            PTreeContObjectNode pTreeContObjectNode = new PTreeContObjectNode(contObjectMapper.contObjectToDto(contObject));
            pTreeElement.addLinkedObject(pTreeContObjectNode);
            addContZPoints(pTreeContObjectNode, contObject);
        }

        for (SubscrObjectTree child : subscrObjectTree.getChildObjectList()) {
            readChildSubscrObjectTree (pTreeElement.addChildElement(SubscrObjectTreeTools.makeFromSubscrObjectTree(child)), child);
        }
    }



    private void addContZPoints(PTreeContObjectNode pTreeContObjectNode, ContObject contObject) {
        List<ContZPoint> contZPoints = contZPointRepository.findByContObjectId(contObject.getId());
        for (ContZPoint contZPoint : contZPoints) {
            if (contZPoint.getDeleted() != 0) continue;
            PTreeContZPointNode contZPointNode = pTreeContObjectNode.addContZPoint(contZPointMapper.toDto(contZPoint));
            addDeviceObject(contZPointNode, contZPoint);
        }
    }


    private void addDeviceObject (PTreeContZPointNode pTreeContZPointNode, ContZPoint contZPoint) {
        for (DeviceObject deviceObject : contZPoint.getDeviceObjects()) {
            if (deviceObject.getDeleted() != 0) continue;
            DeviceObjectDTO deviceObjectDTO = deviceObjectMapper.deviceObjectToDeviceObjectDTO(deviceObject);
            if (contZPoint.get_activeDeviceObject() == deviceObject) {
                deviceObjectDTO.setActiveDeviceObject(true);
            }
            pTreeContZPointNode.addDeviceObject(deviceObjectDTO);
        }
    }



}
