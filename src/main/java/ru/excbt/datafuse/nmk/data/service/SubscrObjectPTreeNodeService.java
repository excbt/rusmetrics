package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.ptree.*;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.service.mapper.DeviceObjectMapper;
import ru.excbt.datafuse.nmk.service.utils.ObjectAccessUtil;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SubscrObjectPTreeNodeService implements SecuredRoles {

    private final SubscrObjectTreeRepository subscrObjectTreeRepository;

    private final SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;

    private final ContObjectMapper contObjectMapper;

    private final ContZPointMapper contZPointMapper;

    private final ContZPointRepository contZPointRepository;

    private final DeviceObjectMapper deviceObjectMapper;

    private final ObjectAccessService objectAccessService;


    @Autowired
    public SubscrObjectPTreeNodeService(SubscrObjectTreeRepository subscrObjectTreeRepository, SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository, ContObjectMapper contObjectMapper, ContZPointMapper contZPointMapper, ContZPointRepository contZPointRepository, DeviceObjectMapper deviceObjectMapper, ObjectAccessService objectAccessService) {
        this.subscrObjectTreeRepository = subscrObjectTreeRepository;
        this.subscrObjectTreeContObjectRepository = subscrObjectTreeContObjectRepository;
        this.contObjectMapper = contObjectMapper;
        this.contZPointMapper = contZPointMapper;
        this.contZPointRepository = contZPointRepository;
        this.deviceObjectMapper = deviceObjectMapper;
        this.objectAccessService = objectAccessService;
    }


    public PTreeNode readSubscrObjectTree (Long subscrObjectTreeId) {
        SubscrObjectTree subscrObjectTree = subscrObjectTreeRepository.findOne(subscrObjectTreeId);

        PTreeElement pTreeElement = SubscrObjectTreeTools.makeFromSubscrObjectTree(subscrObjectTree);

        readChildSubscrObjectTree (pTreeElement, subscrObjectTree);

        return pTreeElement;
    }

    public PTreeNode readSubscrObjectTree (Long subscrObjectTreeId, Integer childLevel, PortalUserIds portalUserIds) {
        SubscrObjectTree subscrObjectTree = subscrObjectTreeRepository.findOne(subscrObjectTreeId);

        PTreeElement pTreeElement = SubscrObjectTreeTools.makeFromSubscrObjectTree(subscrObjectTree);

        ObjectAccessUtil objectAccessUtil = objectAccessService.objectAccessUtil();

        readChildSubscrObjectTree (
            pTreeElement,
            subscrObjectTree,
            childLevel,
            objectAccessUtil.checkContObject(portalUserIds),
            objectAccessUtil.checkContZPoint(portalUserIds)
        );

        return pTreeElement;
    }



    private void readChildSubscrObjectTree (PTreeElement pTreeElement, SubscrObjectTree subscrObjectTree, final Integer childLevel,
                                            final Predicate<ContObject> contObjectAccess,
                                            final Predicate<ContZPoint> contZPointAccess) {

        List<ContObject> contObjects = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTree.getId());

        List<Long> contObjectIds = contObjects.stream()
            .filter(i -> contObjectAccess.test(i)).map(i -> i.getId()).collect(Collectors.toList());


        List<ContZPoint> contZPoints = contObjectIds.isEmpty() ? Collections.emptyList() :
            contZPointRepository.findByContObjectIds(contObjectIds);

        Map<Long,List<ContZPoint>> contZPointMap = new HashMap<>();
        contZPoints.stream().filter(zp -> contZPointAccess.test(zp)).forEach(i -> {
            List<ContZPoint> contZPointList = contZPointMap.get(i.getContObjectId());
            if (contZPointList == null) {
                contZPointList = new ArrayList<>();
                contZPointMap.put(i.getContObjectId(), contZPointList);
            }
            contZPointList.add(i);
        });


        for (ContObject contObject : contObjects) {
            PTreeContObjectNode pTreeContObjectNode = new PTreeContObjectNode(contObjectMapper.toDto(contObject));
            pTreeElement.addLinkedObject(pTreeContObjectNode);
            addContZPointsMap(pTreeContObjectNode, contObject, contZPointMap);
        }

        boolean levelThreshold = childLevel != null && childLevel < 1;

        if (!levelThreshold) {
            for (SubscrObjectTree child : subscrObjectTree.getChildObjectList()) {
                readChildSubscrObjectTree (pTreeElement.addChildElement(SubscrObjectTreeTools.makeFromSubscrObjectTree(child)),
                    child,
                    childLevel != null ? childLevel - 1 : null,
                    contObjectAccess,
                    contZPointAccess);
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
            PTreeContObjectNode pTreeContObjectNode = new PTreeContObjectNode(contObjectMapper.toDto(contObject));
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


    private void addContZPointsMap(PTreeContObjectNode pTreeContObjectNode, final ContObject contObject, final Map<Long, List<ContZPoint>> contZPointMap) {
        List<ContZPoint> contZPoints = contZPointMap.get(contObject.getId());

        if (contZPoints == null) contZPoints = Collections.emptyList();

        for (ContZPoint contZPoint : contZPoints) {
            if (contZPoint.getDeleted() != 0) continue;
            PTreeContZPointNode contZPointNode = pTreeContObjectNode.addContZPoint(contZPointMapper.toDto(contZPoint));
            addDeviceObject(contZPointNode, contZPoint);
        }
    }



    private void addDeviceObject (PTreeContZPointNode pTreeContZPointNode, ContZPoint contZPoint) {
        //for (DeviceObject deviceObject : contZPoint.getDeviceObjects()) {
            if (contZPoint.getDeviceObject() != null &&  contZPoint.getDeviceObject().getDeleted() != 0) {
                return;
            }
            DeviceObjectDTO deviceObjectDTO = deviceObjectMapper.toDto(contZPoint.getDeviceObject());
            //if (contZPoint.get_activeDeviceObject() == deviceObject) {
                deviceObjectDTO.setActiveDeviceObject(true);
            //}
            pTreeContZPointNode.addDeviceObject(deviceObjectDTO);
        //}
    }



}
