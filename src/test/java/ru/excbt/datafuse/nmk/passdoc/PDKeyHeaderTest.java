package ru.excbt.datafuse.nmk.passdoc;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kovtonyk on 24.05.2017.
 */
public class PDKeyHeaderTest {

    @Test
    public void testKey() throws Exception {

        PDKeyHeader.mode.set_shortKey(true);

        PDKeyHeader header = new PDKeyHeader("2.1", "BlaBlaBla");

        Assert.assertEquals("2.1", header.getKey());
        Assert.assertEquals("1", header.getShortKey());


    }
}
