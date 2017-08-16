package ru.excbt.datafuse.nmk.data.ptree;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;

import static org.junit.Assert.*;

public class PTreeNodeTest {

    private static final Logger log = LoggerFactory.getLogger(PTreeNodeTest.class);


    @Test
    public void testJson() throws Exception {
        PTreeElement node = new PTreeElement().nodeName("Тестовый объект");
        node.addContObjectNode(new ContObjectDTO()).addContZPoint(new ContZPointDTO()).addDeviceObject(new DeviceObjectDTO());
        node.setNodeName("My Root");
        node.addContObjectNode(new ContObjectDTO()).setNodeName("ContObject");
        String json = JsonMapperUtils.objectToJson(node, true);
        assertNotNull(json);
        log.info("\n {}", json);
    }
}
