package ru.excbt.datafuse.nmk.data.ptree;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;

public final class SubscrObjectTreeTools {

    private SubscrObjectTreeTools() {
    }

    public static void copyFromSubscrObjectTree (PTreeNode pTreeNode, SubscrObjectTree subscrObjectTree) {
        pTreeNode.setNodeName(subscrObjectTree.getObjectName());
        pTreeNode.set_id(subscrObjectTree.getId());
    }


    public static PTreeElement makeFromSubscrObjectTree (SubscrObjectTree subscrObjectTree) {
        PTreeElement pTreeElement = new PTreeElement();
        copyFromSubscrObjectTree(pTreeElement, subscrObjectTree);
        return pTreeElement;
    }

}
