package ru.excbt.datafuse.nmk.data.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

public class ContServiceDataHWaterRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterRepositoryTest.class);

	private final static long ZPOINT_ID = 18811557;
	private final static String TIME_DETAIL_24H = "24h";

	@Autowired
	private ContServiceDataHWaterRepository repository;

	@Test
	public void testSelectByZPoint() {

		PageRequest pageRequest = new PageRequest(1, 100, new Sort(Direction.ASC, "dataDate"));

		Page<?> resultPage = repository.selectByZPoint(ZPOINT_ID, TIME_DETAIL_24H, pageRequest);
		assertTrue(resultPage.getTotalElements() > 0);
		logger.info("ZPoint (ID:{}) Found {} records", ZPOINT_ID, resultPage.getTotalElements());
	}

	@Test
	public void testSelectByZPointDated() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime srcDate = formatter.parseDateTime("2013-10-01");
		checkNotNull(srcDate);
		DateTime beginDate = srcDate.dayOfMonth().withMinimumValue();
		DateTime endDate = srcDate.dayOfMonth().withMaximumValue();

		List<?> resultList = repository.selectByZPoint(ZPOINT_ID, TIME_DETAIL_24H, beginDate.toDate(),
				endDate.toDate());
		assertTrue(resultList.size() > 0);
		logger.info("ZPoint (ID:{}) Found {} records on period: [{}...{}]", ZPOINT_ID, resultList.size(),
				beginDate.toDate(), endDate.toDate());
	}

}
