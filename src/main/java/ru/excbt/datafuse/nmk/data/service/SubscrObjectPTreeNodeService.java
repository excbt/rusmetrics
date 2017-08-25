package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.ptree.*;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrObjectTreeRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;

import java.util.List;

@Service
public class SubscrObjectPTreeNodeService extends AbstractService implements SecuredRoles {

    private final SubscrObjectTreeRepository subscrObjectTreeRepository;

    private final SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository;

    private final ContObjectMapper contObjectMapper;

    private final ContZPointMapper contZPointMapper;

    private final ContZPointRepository contZPointRepository;


    @Autowired
    public SubscrObjectPTreeNodeService(SubscrObjectTreeRepository subscrObjectTreeRepository, SubscrObjectTreeContObjectRepository subscrObjectTreeContObjectRepository, ContObjectMapper contObjectMapper, ContZPointMapper contZPointMapper, ContZPointRepository contZPointRepository) {
        this.subscrObjectTreeRepository = subscrObjectTreeRepository;
        this.subscrObjectTreeContObjectRepository = subscrObjectTreeContObjectRepository;
        this.contObjectMapper = contObjectMapper;
        this.contZPointMapper = contZPointMapper;
        this.contZPointRepository = contZPointRepository;
    }


    public PTreeNode readSubscrObjectTree (Long subscrObjectTreeId) {
        SubscrObjectTree subscrObjectTree = subscrObjectTreeRepository.findOne(subscrObjectTreeId);

        PTreeElement pTreeElement = SubscrObjectTreeTools.makeFromSubscrObjectTree(subscrObjectTree);

        readChildSubscrObjectTree (pTreeElement, subscrObjectTree);

        return pTreeElement;
    }

    public void readChildSubscrObjectTree (PTreeElement pTreeElement, SubscrObjectTree subscrObjectTree) {
        List<ContObject> contObjects = subscrObjectTreeContObjectRepository.selectContObjects(subscrObjectTree.getId());

        for (ContObject contObject : contObjects) {
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
            pTreeContObjectNode.addContZPoint(contZPointMapper.toDto(contZPoint));
        }
    }




}
