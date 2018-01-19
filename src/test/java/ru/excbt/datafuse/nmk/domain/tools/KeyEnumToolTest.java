package ru.excbt.datafuse.nmk.domain.tools;

import org.junit.Test;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyEnumToolTest {

    @Test
    public void testCheckKeys() {
        boolean result = KeyEnumTool.checkKeys(TimeDetailKey.class, TimeDetailKey.TYPE_24H.getKeyname());
        assertTrue(result);

        boolean result2 = KeyEnumTool.checkKeys(TimeDetailKey.class, "BAD VAL1", "BAD VAL2");
        assertFalse(result2);

    }


    @Test
    public void testSearchKey() {
        Optional<TimeDetailKey> key = KeyEnumTool.searchKey(TimeDetailKey.class, TimeDetailKey.TYPE_24H.getKeyname());
        assertTrue(key.isPresent());
    }

    @Test
    public void testSearchName() {
        Optional<TimeDetailKey> key = KeyEnumTool.searchName(TimeDetailKey.class, TimeDetailKey.TYPE_24H.name());
        assertTrue(key.isPresent());
    }

}
