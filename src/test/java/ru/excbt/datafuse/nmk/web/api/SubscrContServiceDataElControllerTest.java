package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;

import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SubscrContServiceDataElControllerTest extends AnyControllerTest {

	private final static Long TEST_OBJECT_ID = 66948436L;
	private final static Long EL_ZPOINT_ID = 159919982L;
	//	private final static Long TEST_OBJECT_ID = 725L;
	//	private final static Long EL_ZPOINT_ID = 183740672L;

	private final static String CONS_TIME_DETAIL = TimeDetailKey.TYPE_ABS.getKeyname();
	private final static String PROFILE_TIME_DETAIL = TimeDetailKey.TYPE_30MIN.getKeyname();
	private final static String TECH_TIME_DETAIL = TimeDetailKey.TYPE_24H.getKeyname();

	private RequestExtraInitializer requestParamInitializer() {
		return (builder) -> {
			builder.param("beginDate", "2015-12-01");
			builder.param("endDate", "2015-12-31");
		};
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testElConsSummary() throws Exception {
		String url = apiSubscrUrl(
				String.format("/%d/serviceElCons/%s/%d/summary", TEST_OBJECT_ID, CONS_TIME_DETAIL, EL_ZPOINT_ID));

		_testGet(url, requestParamInitializer());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testElProfileSummary() throws Exception {
		String url = apiSubscrUrl(
				String.format("/%d/serviceElProfile/%s/%d/summary", TEST_OBJECT_ID, PROFILE_TIME_DETAIL, EL_ZPOINT_ID));

		_testGet(url, requestParamInitializer());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testElTechSummary() throws Exception {
		String url = apiSubscrUrl(
				String.format("/%d/serviceElTech/%s/%d/summary", TEST_OBJECT_ID, TECH_TIME_DETAIL, EL_ZPOINT_ID));

		_testGet(url, requestParamInitializer());
	}

	@Test
	public void testElConsData() throws Exception {
		String url = apiSubscrUrl(
				String.format("/%d/serviceElCons/%s/%d", TEST_OBJECT_ID, CONS_TIME_DETAIL, EL_ZPOINT_ID));

		_testGet(url, requestParamInitializer());
	}

	@Test
	public void testElConsDataAbs() throws Exception {
		String url = apiSubscrUrl(String.format("/%d/serviceElCons/24h_abs/%d", TEST_OBJECT_ID, EL_ZPOINT_ID));

		_testGet(url, requestParamInitializer());
	}

	@Test
	public void testElProfileData() throws Exception {
		String url = apiSubscrUrl(
				String.format("/%d/serviceElProfile/%s/%d", TEST_OBJECT_ID, PROFILE_TIME_DETAIL, EL_ZPOINT_ID));

		_testGet(url, requestParamInitializer());
	}

}
