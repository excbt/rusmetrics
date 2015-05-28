package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import ru.excbt.datafuse.nmk.data.constant.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataJson;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.model.support.MetaFieldType;
import ru.excbt.datafuse.nmk.data.model.support.MetaFieldValue;
import ru.excbt.datafuse.nmk.data.service.DeviceMetadataService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectDataJsonService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectMetaService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetadataCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(MetadataCli.class);

	private final static long DEVICE_OBJECT_22 = 22;

	private final static String META_SUBSTR = "(var)";
	private final static String META_SUBSTR_REXPR = "\\(var\\)";
	private final static Pattern META_VAR = Pattern.compile(META_SUBSTR_REXPR);
	private final static String COMMA_STR = ",";
	private final static String DEGIT_REGEXP = "(\\\\d)+";
	private final static String SUM_FUNCION = "SUM(*)";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private DeviceObjectMetaService deviceObjectMetaService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private DeviceMetadataService deviceMetadataService;

	@Autowired
	private DeviceObjectDataJsonService deviceObjectDataJsonService;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MetadataCli app = new MetadataCli();
		app.autowireBeans();
		app.showAppStatus();
		app.readDeviceObjectModel(1);
		// app.readMetaVzlet();
	}

	private void readMetaVzlet() {

		List<DeviceObjectMetaVzlet> metaList = deviceObjectMetaService
				.findMetaVzlet(DEVICE_OBJECT_22);

		checkState(metaList.size() > 0);

		metaList.stream().forEach(
				(val) -> logger.info("VzletTableDay: {}, VzletTableHour: {}",
						val.getVzletTableDay(), val.getVzletTableHour()));

	}

	private void readDeviceObjectModel(int propVar) {
		DeviceObject deviceObject = deviceObjectService
				.findOne(DEVICE_OBJECT_22);

		DeviceModel model = deviceObject.getDeviceModel();

		logger.info("DeviceObjectModel: {}. ExSystem:{}. ExLabel:{} ",
				model.getModelName(), model.getExSystem(), model.getExLabel());

		logger.info("deviceModelId: {}", model.getId());

		List<DeviceMetadata> deviceMetadataList = deviceMetadataService
				.findDeviceMetadata(model.getId());

		List<DeviceMetadata> resultMetadataList = processPropsVars(
				deviceMetadataList, propVar);

		resultMetadataList.stream().forEach(
				(val) -> logger.info("srcProp: {}", val.getSrcProp()));

		logger.info("Date: {}", LocalDateTime.of(2011, 10, 1, 0, 0));

		List<DeviceObjectDataJson> jsonList = deviceObjectDataJsonService
				.selectDeviceObjectDataJson(deviceObject.getId(),
						TimeDetailKey.TYPE_24H, LocalDateTime.of(2011, 10, 1,
								0, 0), new PageRequest(0, 1));

		checkState(!jsonList.isEmpty());

		DeviceObjectDataJson jsonData = jsonList.get(0);
		checkNotNull(jsonData);

		logger.info(jsonData.getDataJson());
		try {
			processJsonFieldValues(jsonData.getDataJson(), resultMetadataList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param deviceMetadataList
	 * @param propVar
	 * @return
	 */
	private List<DeviceMetadata> processPropsVars(
			List<DeviceMetadata> deviceMetadataList, int propVar) {
		checkNotNull(deviceMetadataList);
		String propVarStr = String.valueOf(propVar);
		List<DeviceMetadata> result = new ArrayList<>();
		for (DeviceMetadata data : deviceMetadataList) {
			String srcPropComplete = null;

			Matcher m = META_VAR.matcher(data.getSrcProp());

			if (data.getPropVars() != null
					&& !data.getPropVars().contains(propVarStr)) {
				logger.trace("srcProp: {} SKIPPED", data.getSrcProp());
				continue;
			}

			if (m.find()) {
				srcPropComplete = m.replaceAll(propVarStr);
			} else {
				srcPropComplete = data.getSrcProp();
			}

			logger.trace("srcProp: {}. srcPropComplete: {}", data.getSrcProp(),
					srcPropComplete);

			data.setSrcProp(srcPropComplete);
			result.add(data);
		}

		for (DeviceMetadata d : result) {
			deviceMetadataList.remove(d);
		}

		return result;
	}

	/**
	 * 
	 * @param parentNode
	 * @param strPattern
	 * @return
	 */
	private List<JsonNode> findByPattern(JsonNode parentNode, String strPattern) {
		checkNotNull(parentNode);

		String regexpPattern = strPattern.replaceAll("\\*", DEGIT_REGEXP);

		logger.debug("Pattern: {}", strPattern);
		logger.debug("RegExp Pattern: {}", regexpPattern);
		Pattern pattern = Pattern.compile(regexpPattern);

		List<JsonNode> result = new ArrayList<>();
		Iterator<String> fieldNames = parentNode.fieldNames();
		while (fieldNames.hasNext()) {
			String fName = fieldNames.next();
			Matcher matcher = pattern.matcher(fName);
			if (matcher.matches()) {
				// We find nodes that matches: add it to result list
				List<JsonNode> nodes = parentNode.findValues(fName);
				result.addAll(nodes);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param propFunc
	 * @param nodes
	 * @return
	 */
	private BigDecimal processPropFunction(String propFunc, List<JsonNode> nodes) {
		checkNotNull(propFunc);
		checkNotNull(nodes);
		checkArgument(!nodes.isEmpty());

		BigDecimal result = BigDecimal.ZERO;

		for (JsonNode node : nodes) {
			if (node.isBigDecimal() && SUM_FUNCION.equals(propFunc)) {
				BigDecimal val = node.decimalValue();
				result = result.add(val);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param srcJson
	 * @param deviceMetadataList
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public List<MetaFieldValue> processJsonFieldValues(String srcJson,
			List<DeviceMetadata> deviceMetadataList)
			throws JsonProcessingException, IOException {

		List<MetaFieldValue> result = new ArrayList<>();

		JsonNode rootNode = OBJECT_MAPPER.readTree(srcJson);

		for (DeviceMetadata meta : deviceMetadataList) {
			if (meta.getSrcProp().contains(",")) {
				String[] fieldPatterns = meta.getSrcProp().split(COMMA_STR);
				for (String fPattern : fieldPatterns) {
					List<JsonNode> fNodes = findByPattern(rootNode, fPattern);

					if (fNodes.size() == 0) {
						logger.warn("Nothing to find by pattern: {}", fPattern);
					}

					logger.info("Func Field: {} Values: {}", fPattern,
							fNodes.size());

					if (fNodes.size() > 0 && meta.getPropFunc() != null) {
						BigDecimal sum = processPropFunction(
								meta.getPropFunc(), fNodes);
						logger.info("sum: {}", sum);

						MetaFieldValue metaFieldValue = new MetaFieldValue(
								meta.getDestProp(), sum,
								MetaFieldType.BIG_DECIMAL);
						result.add(metaFieldValue);

					}

				}
				continue;
			}

			JsonNode valueNode = rootNode.findValue(meta.getSrcProp());
			if (valueNode == null) {
				logger.error("Node with Name {} is not found",
						meta.getSrcProp());
				continue;
			}
			MetaFieldValue metaFieldValue = new MetaFieldValue(
					meta.getDestProp(), valueNode.asText(), MetaFieldType.STRING);
			result.add(metaFieldValue);
			
			logger.info("metaFieldValue: {}", metaFieldValue);

		}

		return result;
	}

}
