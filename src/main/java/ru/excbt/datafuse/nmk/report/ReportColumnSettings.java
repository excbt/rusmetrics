package ru.excbt.datafuse.nmk.report;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Работа с колонками отчета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.07.2015
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportColumnSettings {

	private List<ReportColumn> allTsList = new ArrayList<>();

	private List<ReportColumn> ts1List = new ArrayList<>();

	private List<ReportColumn> ts2List = new ArrayList<>();

	public List<ReportColumn> getAllTsList() {
		return allTsList;
	}

	public void setAllTsList(List<ReportColumn> allTsList) {
		this.allTsList = allTsList;
	}

	public List<ReportColumn> getTs1List() {
		return ts1List;
	}

	public void setTs1List(List<ReportColumn> ts1List) {
		this.ts1List = ts1List;
	}

	public List<ReportColumn> getTs2List() {
		return ts2List;
	}

	public void setTs2List(List<ReportColumn> ts2List) {
		this.ts2List = ts2List;
	}

}
