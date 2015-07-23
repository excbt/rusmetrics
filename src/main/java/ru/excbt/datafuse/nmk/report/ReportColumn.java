package ru.excbt.datafuse.nmk.report;

import java.io.Serializable;

import ru.excbt.nmk.reports.ColumnElement;

public class ReportColumn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1645430436067608438L;
	
	
	private int systemNumber;
	private int columnNumber;
	private String columnHeader;
	
	public ReportColumn() {
		
	}
	
	public ReportColumn(ColumnElement srcColumnElement) {
		this.systemNumber = srcColumnElement.nSystem;
		this.columnNumber = srcColumnElement.n;
		this.columnHeader = srcColumnElement.name;
	}



	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getColumnHeader() {
		return columnHeader;
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}

	public int getSystemNumber() {
		return systemNumber;
	}

	public void setSystemNumber(int systemNumber) {
		this.systemNumber = systemNumber;
	}
}
