package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.metadata.JsonMetadataParser;
import ru.excbt.datafuse.nmk.metadata.MetadataFieldValue;
import ru.excbt.datafuse.nmk.metadata.MetadataInfo;
import ru.excbt.datafuse.nmk.metadata.MetadataInfoHolder;

public class DeviceObjectMetadataRawCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(DeviceObjectMetadataRawCli.class);

	private final static String jsonFilename = "metadata_json/deviceObjectMetadataRaw.json";

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DeviceObjectMetadataRawCli app = new DeviceObjectMetadataRawCli();
		app.autowireBeans();
		String inJson = readFile(jsonFilename, StandardCharsets.UTF_8);
		logger.info("Loaded json: {}", inJson);

		List<MetadataInfo> metadataList = new ArrayList<>();
		{
			MetadataInfoHolder metadata = new MetadataInfoHolder();
			metadata.setSrcProp("VolumeTotal[2]");
			metadata.setDestProp("v_out");
			metadataList.add(metadata);
		}

		{
			MetadataInfoHolder metadata2 = new MetadataInfoHolder();
			metadata2.setSrcProp("Temperature[2][1]");
			metadata2.setDestProp("t_out");
			metadataList.add(metadata2);
		}

		try {
			List<MetadataFieldValue> fieldValues = JsonMetadataParser
					.processJsonFieldValues(inJson, metadataList);
			fieldValues.stream().forEach((val) -> logger.info("{}", val));

			checkState(metadataList.size() == fieldValues.size());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param path
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
