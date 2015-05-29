package ru.excbt.datafuse.nmk.metadata;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMetadataParser {

	private static final Logger logger = LoggerFactory
			.getLogger(JsonMetadataParser.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final static String META_SUBSTR_REXPR = "\\(var\\)";
	private final static Pattern META_VAR = Pattern.compile(META_SUBSTR_REXPR);
	private final static String COMMA_STR = ",";
	private final static String DEGIT_REGEXP = "(\\\\d)+";
	private final static String SUM_FUNCION = "SUM(*)";
	private final static List<String> SUPPORTED_FUNC = Arrays
			.asList(SUM_FUNCION);

	/**
	 * 
	 * @param metadataInfoList
	 * @param propVar
	 * @return
	 */
	public static List<MetadataInfo> processPropsVars(
			List<? extends MetadataInfo> metadataInfoList, int propVar) {
		checkNotNull(metadataInfoList);

		final String propVarStr = String.valueOf(propVar);

		List<MetadataInfo> result = new ArrayList<>();
		for (MetadataInfo srcMetadata : metadataInfoList) {
			String srcPropComplete = null;

			MetadataInfoHolder data = new MetadataInfoHolder(srcMetadata);

			Matcher m = META_VAR.matcher(srcMetadata.getSrcProp());

			if (data.getPropVars() != null
					&& !srcMetadata.getPropVars().contains(propVarStr)) {
				logger.trace("srcProp: {} SKIPPED", srcMetadata.getSrcProp());
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

		return result;
	}

	/**
	 * 
	 * @param parentNode
	 * @param strPattern
	 * @return
	 */
	private static List<JsonNode> findByPattern(JsonNode parentNode,
			String strPattern) {
		checkNotNull(parentNode);

		String regexpPattern = strPattern.replaceAll("\\*", DEGIT_REGEXP);

		logger.trace("Find Props By Pattern: {}", strPattern);
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

		logger.trace("Found {} Props ", result.size());
		return result;
	}

	private static BigDecimal processFunction(String propFunc, BigDecimal arg1,
			BigDecimal arg2) {
		checkNotNull(propFunc);
		checkNotNull(arg1);
		checkNotNull(arg2);

		BigDecimal result = BigDecimal.ZERO;

		if (!SUPPORTED_FUNC.contains(propFunc)) {
			throw new IllegalArgumentException("propFunc " + propFunc
					+ " is not supported");
		}

		if (SUM_FUNCION.equals(propFunc)) {
			if (!arg2.equals(BigDecimal.ZERO)) {
				result = arg2.add(arg1);
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
	private static BigDecimal processNodePropFunction(String propFunc,
			List<JsonNode> nodes) {
		checkNotNull(propFunc);
		checkNotNull(nodes);
		checkArgument(!nodes.isEmpty());

		logger.trace("Process function: {}", propFunc);

		BigDecimal result = BigDecimal.ZERO;

		for (JsonNode node : nodes) {
			if (node.isBigDecimal()) {
				BigDecimal val = node.decimalValue();
				result = processFunction(propFunc, result, val);
			}

		}
		logger.trace("Function result: {}", result);
		return result;
	}

	/**
	 * 
	 * @param srcJson
	 * @param metadataInfoList
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public static List<MetadataFieldValue> processJsonFieldValues(
			String srcJson, List<MetadataInfo> metadataInfoList)
			throws JsonProcessingException, IOException {

		List<MetadataFieldValue> result = new ArrayList<>();

		JsonNode rootNode = OBJECT_MAPPER.readTree(srcJson);

		for (MetadataInfo meta : metadataInfoList) {
			logger.trace("Process prop: {}", meta.getSrcProp());
			if (meta.getSrcProp().contains(",")) {
				String[] fieldPatterns = meta.getSrcProp().split(COMMA_STR);
				BigDecimal totalResult = BigDecimal.ZERO;
				for (String fPattern : fieldPatterns) {
					List<JsonNode> fNodes = findByPattern(rootNode, fPattern);

					if (fNodes.size() == 0) {
						logger.warn("Nothing to find by pattern: {}", fPattern);
					}

					if (fNodes.size() > 0 && meta.getPropFunc() != null) {
						BigDecimal patternSum = processNodePropFunction(
								meta.getPropFunc(), fNodes);

						totalResult = processFunction(meta.getPropFunc(),
								totalResult, patternSum);

					} else {
						logger.warn(
								"Can't process any field by pattern {} and function {}",
								fPattern, meta.getPropFunc());
					}

				}
				MetadataFieldValue metaFieldValue = new MetadataFieldValue(
						meta.getSrcProp(), meta.getDestProp(), totalResult,
						MetadataFieldType.BIG_DECIMAL);
				result.add(metaFieldValue);

				continue;
			}

			JsonNode valueNode = rootNode.findValue(meta.getSrcProp());
			if (valueNode == null) {
				logger.error("Node with Name {} is not found",
						meta.getSrcProp());
				continue;
			}
			MetadataFieldValue metaFieldValue = new MetadataFieldValue(
					meta.getSrcProp(), meta.getDestProp(), valueNode.asText(),
					MetadataFieldType.STRING);
			result.add(metaFieldValue);

		}

		return result;
	}

}
