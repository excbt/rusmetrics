/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.01.2017
 * 
 */
public class WidgetTest {

	private static final Logger log = LoggerFactory.getLogger(WidgetTest.class);

	@Test
	public void testJsonModel() throws Exception {
		Widget widget = Widget.builder().widgetName("My Widget").widgetPath("\\bla-bla\\path.txt")
				.contServiceType(ContServiceTypeKey.EL.getKeyname()).build();
		log.info("Json Model: {}", widget.toJson());
		assertTrue(ContServiceTypeKey.EL.equals(widget.contServiceTypeKey()));
	}

}
