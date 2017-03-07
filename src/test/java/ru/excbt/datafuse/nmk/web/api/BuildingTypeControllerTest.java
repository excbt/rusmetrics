/**
 *
 */
package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import javax.transaction.Transactional;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2016
 *
 */
public class BuildingTypeControllerTest extends AnyControllerTest {

	/*

	 */
	@Test
    @Transactional
	public void testBuildingType() throws Exception {
		_testGetJson("/api/subscr/service/buildingType/list");
	}

	/*

	 */
	@Test
    @Transactional
	public void testBuildingTypeCategory() throws Exception {
		_testGetJson("/api/subscr/service/buildingType/category/list");
	}

}
