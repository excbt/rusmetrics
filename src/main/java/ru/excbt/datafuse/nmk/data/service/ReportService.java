package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.constant.ReportConstants;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportType;

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
	public String getCommercialReportHtmlPath(long contObjectId,
			DateTime beginDate, DateTime endDate) {
		checkArgument(contObjectId > 0);
		checkNotNull(beginDate);
		checkNotNull(endDate);
		checkArgument(beginDate.compareTo(endDate) < 0);

		String defaultUrl = systemParamService
				.getParamValueAsString(ReportConstants.COMMERCIAL_REPORT_TEMPLATE_PATH);

		checkNotNull(defaultUrl);

		return String.format(defaultUrl, ReportType.HTML.toParam(),
				endDate.toString(DATE_TEMPLATE),
				beginDate.toString(DATE_TEMPLATE), contObjectId);
	}

	/**
	 * 
	 * @return
	 */
	public boolean externalJasperServerEnable() {
		boolean result = false;
		try {
			result = systemParamService
					.getParamValueAsBoolean(ReportConstants.EXTERNAL_JASPER_SERVER_ENABLE);
		} catch (PersistenceException e) {
			result = false;
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public String externalJasperServerUrl() {
		String result = systemParamService
				.getParamValueAsString(ReportConstants.EXTERNAL_JASPER_SERVER_URL);
		return result;
	}

}
