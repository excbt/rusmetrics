/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 * 
 */
public class BuildingTypeControllerTest extends AnyControllerTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildingType() throws Exception {
		_testGetJson("/api/subscr/service/buildingType/list");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildingTypeCategory() throws Exception {
		_testGetJson("/api/subscr/service/buildingType/category/list");
	}

}
