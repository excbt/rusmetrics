/**
 * 
 */
package ru.excbt.datafuse.nmk.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class LoadingBtsData {
	private static final Logger log = LoggerFactory.getLogger(LoadingBtsData.class);

	@Data
	@Builder
	@ToString
	public static class BtsInfo {
		private String btsNr;
		private String riserNr;
		private String aptNr;
		private String porchNr;
	}


	/**
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<BtsInfo> loadBtsInfo(String file) throws FileNotFoundException, IOException {

		List<BtsInfo> resultList = new ArrayList<>();
		
		File f = ResourceHelper.findResource(file);
		log.info("File: {}", f.getAbsolutePath());
		try (Reader r = new FileReader(file)) {
			try (BufferedReader br = new BufferedReader(r)) {
				String header = br.readLine();
				String line;
				String prevPorch = "";
				while ((line = br.readLine()) != null) {
					String data[] = line.split(";");
					log.debug("Read line: {}, items: {}", line, data.length);
					if (data.length <= 0 || data.length <= 5) {
						log.warn("SKIP");
						continue;
					}
					checkState(data.length > 0);
					String[] nrBtss = data[0].split(",");
					String riserNrs = data[2];
					String aptNrs = data[5];
					String porchNrs = data.length >= 9 ? data[8] : prevPorch;
					//String porchNrs = "";
					for (String nrBts : nrBtss) {
						BtsInfo info = BtsInfo.builder().btsNr(nrBts.trim()).aptNr(aptNrs.trim()).riserNr(riserNrs.trim()).porchNr(porchNrs.trim()).build();
						log.debug("Bts Nr: {}, info: {}", nrBts, info);
						resultList.add(info);
					}
					if (porchNrs != prevPorch) 
						prevPorch = porchNrs;
				}
			}

		}

		return resultList;
	}
}
