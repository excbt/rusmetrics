package ru.excbt.datafuse.nmk.data.energypassport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;

import java.lang.reflect.Type;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by kovtonyk on 27.04.2017.
 */
public class EPSectionValueUtilTest {

    private static final Logger log = LoggerFactory.getLogger(EPSectionValueUtilTest.class);

    @Getter
    @Setter
    private static class SomeObject {
        private String caption;
    }


    @Test
    public void testReplaceJsonVars() throws Exception {
        Properties vars = new Properties();
        vars.put(Pattern.quote("{YYYY}"), "2017");
        vars.put(Pattern.quote("{YYYY-1}"), "2016");
        vars.put(Pattern.quote("{YYYY-2}"), "2015");
        SomeObject obj = new SomeObject();
        obj.caption = "2017:{YYYY} , 2016:{YYYY-1}, 2015:{YYYY-2}";
        String inJson = JsonMapperUtils.objectToJson(obj,true);
        log.info("inJson:\n{}",inJson);
        String outJson = EPSectionValueUtil.replaceJsonVars(inJson, vars);
        log.info("outJson:\n{}",outJson);
        SomeObject obj2 = JsonMapperUtils.jsonToObject(outJson, new TypeReference<SomeObject>() {
        });
        Assert.assertEquals(obj2.caption, "2017:2017 , 2016:2016, 2015:2015");
    }
}
