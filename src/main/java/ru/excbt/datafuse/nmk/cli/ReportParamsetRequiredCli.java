package ru.excbt.datafuse.nmk.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;

public class ReportParamsetRequiredCli extends AbstractDBToolCli {

	
	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetRequiredCli.class);
	
	@Autowired
	private ReportParamsetService reportParamsetService;
	
	public static void main(String[] args) {
		ReportParamsetRequiredCli app = new ReportParamsetRequiredCli();
		app.autowireBeans();
		logger.info("{} INITIALIZED",ReportParamsetRequiredCli.class.getName());
		app.reportParamsetService.setupRequiredPassed();
		logger.info("{} FINISHED",ReportParamsetRequiredCli.class.getName());		
	}
}
