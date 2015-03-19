package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants;

@Service
public class ReportService {

	private final static String DATE_TEMPLATE = "yyyy-MM-dd";
	
	
	@Autowired
	private SystemParamService systemParamService;
	
	/**
	 * 
	 * @param contObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public String getCommercialReportPath(long contObjectId, DateTime beginDate, DateTime endDate) {
		checkArgument(contObjectId > 0);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) < 0);
		
		String defaultUrl = systemParamService
				.getParamValue(ReportConstants.COMMERCIAL_REPORT_TEMPLATE_PATH);
		
		checkNotNull(defaultUrl);
		
		return String.format(defaultUrl, endDate.toString(DATE_TEMPLATE), beginDate.toString(DATE_TEMPLATE), contObjectId);
	}
	
	
}
