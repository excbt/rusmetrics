package ru.excbt.datafuse.nmk.metadata;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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

/**
 * Класс для работы с метаданными прибора
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.05.2015
 *
 */
public class JsonMetadataParser {

	private static final Logger logger = LoggerFactory.getLogger(JsonMetadataParser.class);

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final static String META_SUBSTR_REXPR = "\\(var\\)";
	private final static Pattern META_VAR = Pattern.compile(META_SUBSTR_REXPR);
	private final static String COMMA_STR = ",";
	private final static String DEGIT_REGEXP = "(\\\\d)+";
	private final static String SUM_FUNCION = "SUM(*)";
	private final static List<String> SUPPORTED_FUNC = Arrays.asList(SUM_FUNCION);

	/**
	 * 
	 * @param metadataInfoList
	 * @param propVar
	 * @return
	 */
	public static List<MetadataInfo> processPropsVars(List<? extends MetadataInfo> metadataInfoList, int propVar) {
		checkNotNull(metadataInfoList);

		final String propVarStr = String.valueOf(propVar);

		List<MetadataInfo> result = new ArrayList<>();
		for (MetadataInfo srcMetadata : metadataInfoList) {
			String srcPropComplete = null;

			MetadataInfoHolder data = new MetadataInfoHolder(srcMetadata);

			Matcher m = META_VAR.matcher(srcMetadata.getSrcProp());

			if (data.getPropVars() != null && !srcMetadata.getPropVars().contains(propVarStr)) {
				logger.trace("srcProp: {} SKIPPED (by propVars)", srcMetadata.getSrcProp());
				continue;
			}

			if (data.getMetaNumber() != null && !data.getMetaNumber().equals(propVar)) {
				logger.trace("srcProp: {} SKIPPED (by metaNumber)", srcMetadata.getSrcProp());
				continue;
			}

			if (m.find()) {
				srcPropComplete = m.replaceAll(propVarStr);
			} else {
				srcPropComplete = data.getSrcProp();
			}

			logger.trace("srcProp: {}. srcPropComplete: {}", data.getSrcProp(), srcPropComplete);

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
	private static List<JsonNode> findByPattern(JsonNode parentNode, String strPattern) {
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

	/**
	 * 
	 * @param propFunc
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	private static BigDecimal processFunction(String propFunc, BigDecimal arg1, BigDecimal arg2) {
		checkNotNull(propFunc);
		checkNotNull(arg1);
		checkNotNull(arg2);

		BigDecimal result = BigDecimal.ZERO;

		if (!SUPPORTED_FUNC.contains(propFunc)) {
			throw new IllegalArgumentException("propFunc " + propFunc + " is not supported");
		}

		if (SUM_FUNCION.equals(propFunc)) {
			result = arg1.add(arg2);
		}
		return result;
	}

	/**
	 * 
	 * @param propFunc
	 * @param nodes
	 * @return
	 */
	private static BigDecimal processNodePropFunction(String propFunc, List<JsonNode> nodes) {
		checkNotNull(propFunc);
		checkNotNull(nodes);
		checkArgument(!nodes.isEmpty());

		logger.trace("Process function: {}", propFunc);

		BigDecimal result = BigDecimal.ZERO;

		StringBuilder sumArgs = new StringBuilder();

		for (JsonNode node : nodes) {
			BigDecimal val = null;
			try {
				val = node.decimalValue();
				result = processFunction(propFunc, result, val);
				sumArgs.append(val);
				sumArgs.append(',');
			} catch (Exception e) {
				logger.warn("Node: {} is not NUMBER. Type: {}", node.asText(), node.getNodeType());
			}
		}
		if (sumArgs.length() > 0) {
			sumArgs.deleteCharAt(sumArgs.length() - 1);
		}

		logger.trace("Function {} arguments: {}", propFunc, sumArgs.toString());
		logger.trace("Function {} result: {}", propFunc, result);
		return result;
	}

	/**
	 * 
	 * @param srcJson
	 * @param metadataInfoList
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public static List<MetadataFieldValue> processJsonFieldValues(String srcJson, List<MetadataInfo> metadataInfoList)
			throws JsonProcessingException, IOException {

		List<MetadataFieldValue> result = new ArrayList<>();

		JsonNode rootNode = OBJECT_MAPPER.readTree(srcJson);

		for (MetadataInfo meta : metadataInfoList) {

			final String srcProp = meta.getSrcProp();

			logger.trace("Process prop: {}", srcProp);

			// comma separated props or field by mask
			if (srcProp.contains(",") || srcProp.contains("*")) {
				String[] fieldPatterns = srcProp.split(COMMA_STR);
				BigDecimal totalResult = BigDecimal.ZERO;
				for (String fPattern : fieldPatterns) {
					List<JsonNode> fNodes = findByPattern(rootNode, fPattern);

					if (fNodes.size() == 0) {
						logger.warn("Nothing to find by pattern: {}", fPattern);
					}

					if (fNodes.size() > 0 && meta.getPropFunc() != null) {
						BigDecimal patternSum = processNodePropFunction(meta.getPropFunc(), fNodes);

						totalResult = processFunction(meta.getPropFunc(), totalResult, patternSum);

					} else {
						logger.warn("Can't process any field by pattern {} and function {}", fPattern,
								meta.getPropFunc());
					}

				}
				MetadataFieldValue metaFieldValue = new MetadataFieldValue(srcProp, meta.getDestProp(), totalResult,
						meta.getDestDbType());
				result.add(metaFieldValue);

				continue;
			}

			// / Array Property
			if (srcProp.contains("[")) {
				int idx = srcProp.indexOf("[");
				String propName = srcProp.substring(0, idx);
				String propIdxs = srcProp.substring(idx, srcProp.length());

				if (propIdxs.length() % 3 != 0) {
					logger.error("propIdxs is Invalid. srcProp:{}", srcProp);
					continue;
				}

				int[] idxs = new int[propIdxs.length() / 3];
				for (int i = 0; i < idxs.length; i++) {
					String idxString = propIdxs.substring(i * 3 + 1, i * 3 + 2);
					idxs[i] = Integer.valueOf(idxString);
				}

				JsonNode valueNode = rootNode.findValue(propName);
				if (valueNode == null) {
					logger.error("!!! Node with Name {} is not found !!!", propName);
				}

				checkState(idxs.length <= 2, "Only one and two dimentions array supported");

				// 1D array
				if (valueNode.isArray() && idxs.length == 1) {

					JsonNode arrayValue = findArrayElement(valueNode, idxs[0]);
					checkNotNull(arrayValue);

					MetadataFieldValue metaFieldValue = new MetadataFieldValue(meta.getSrcProp(), meta.getDestProp(),
							arrayValue.asText(), meta.getDestDbType());
					result.add(metaFieldValue);

					continue;
				}
				// 2D array
				if (valueNode.isArray() && idxs.length == 2) {
					JsonNode arrayValue1D = findArrayElement(valueNode, idxs[0]);
					checkNotNull(arrayValue1D);
					checkState(arrayValue1D.isArray());
					JsonNode arrayValue2D = findArrayElement(arrayValue1D, idxs[1]);
					checkNotNull(arrayValue2D);
					MetadataFieldValue metaFieldValue = new MetadataFieldValue(meta.getSrcProp(), meta.getDestProp(),
							arrayValue2D.asText(), meta.getDestDbType());
					result.add(metaFieldValue);
					continue;
				}
			} // Is array

			// Simple value
			JsonNode valueNode = rootNode.findValue(meta.getSrcProp());
			if (valueNode == null) {
				logger.error("!!! Node with Name {} is not found !!!", meta.getSrcProp());
				continue;
			}

			MetadataFieldValue metaFieldValue = new MetadataFieldValue(meta.getSrcProp(), meta.getDestProp(),
					valueNode.asText(), meta.getDestDbType());
			result.add(metaFieldValue);
		}

		return result;
	}

	/**
	 * 
	 * @param parentNode
	 * @param idx
	 * @return
	 */
	private static JsonNode findArrayElement(JsonNode parentNode, int idx) {

		checkNotNull(parentNode);
		checkArgument(parentNode.isArray());
		checkArgument(idx >= 0);

		Iterator<JsonNode> arrayElements = parentNode.elements();
		int i = 0;
		JsonNode result = null;
		while (arrayElements.hasNext()) {
			i++;
			JsonNode arrayValueNode = arrayElements.next();
			if (i < idx) {
				continue;
			}
			if (i == idx) {
				result = arrayValueNode;
			}
			break;
		}
		return result;
	}

}
