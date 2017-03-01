/**
 * 
 */
package ru.excbt.datafuse.nmk.cli;

import ru.excbt.datafuse.nmk.utils.LoadingBtsData;
import ru.excbt.datafuse.nmk.utils.ResourceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.02.2017
 * 
 */
public class LoadingBts {
	
	
	private static final Logger log = LoggerFactory.getLogger(LoadingBts.class);


	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<LoadingBtsData.BtsInfo> btsInfos = LoadingBtsData.loadBtsInfo("БТСки.csv");
	}

}
