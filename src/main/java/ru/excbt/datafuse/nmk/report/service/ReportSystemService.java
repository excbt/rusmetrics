package ru.excbt.datafuse.nmk.report.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.report.model.ReportColumn;
import ru.excbt.datafuse.nmk.report.model.ReportColumnSettings;
import ru.excbt.nmk.reports.ColumnElement;
import ru.excbt.nmk.reports.ReportConvert;

@Service
public class ReportSystemService {

	private void loadColumnHeader() {
		// ReportConvert.getHeaderColumn()
	}

	/**
	 * 
	 * @return
	 */
	public List<ReportColumn> getReportColumns() {
		List<ReportColumn> resultList = new ArrayList<>();
		ColumnElement[] elements = ReportConvert.getHeaderColumn();
		checkNotNull(elements);
		for (ColumnElement ce : elements) {
			resultList.add(new ReportColumn(ce));
		}
		return resultList;
	}

	/**
	 * 
	 * @return
	 */
	public ReportColumnSettings getReportColumnSettings() {
		ReportColumnSettings result = new ReportColumnSettings();
		ColumnElement[] elements = ReportConvert.getHeaderColumn();
		checkNotNull(elements);

		for (ColumnElement ce : elements) {
			if (ce.n == 0) {
				result.getAllTsList().add(new ReportColumn(ce));
				continue;
			}

			if (ce.n != 0 && ce.nSystem == 1) {
				result.getTs1List().add(new ReportColumn(ce));
			}

			if (ce.n != 0 && ce.nSystem == 2) {
				result.getTs2List().add(new ReportColumn(ce));
			}
		}
		return result;
	}

}
