package yamlConvertor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import exception.CustomBankException;

public class YamlMapper {
	private static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private static Map<String, Map<String, String>> classTableMap;
	private static Map<String, Map<String, Map<String, String>>> fieldColumnMap;
	private static Map<String, Map<String, String>> mappingMap = null;

	static {
		classTableMap = new HashMap<>();
		classTableMap.put("classToTable", new HashMap<String, String>());
		classTableMap.put("tableToClass", new HashMap<String, String>());
		fieldColumnMap = new HashMap<>();
	}

	public YamlMapper() throws CustomBankException {
		setMappings();
	}

	@SuppressWarnings("unchecked")
	private static void setMappings() throws CustomBankException {
		if (mappingMap == null) {
			try {
				mappingMap = mapper.readValue(YamlMapper.class.getClassLoader().getResourceAsStream("FieldColumnMapper.yaml"), Map.class);
				for (Map.Entry<String, Map<String, String>> element : mappingMap.entrySet()) {
					String tableName = element.getKey();
					Map<String, String> valueMap = element.getValue();
					String pojoName = valueMap.remove("Class");
					classTableMap.get("tableToClass").put(tableName, pojoName);
					classTableMap.get("classToTable").put(pojoName, tableName);

					Map<String, Map<String, String>> currValueMap = fieldColumnMap.get(tableName);
					Map<String, String> fieldToColumnMap;
					Map<String, String> columnToFieldMap;
					if (currValueMap == null) {
						currValueMap = new HashMap<>();
						fieldToColumnMap = new HashMap<>();
						columnToFieldMap = new HashMap<>();
						currValueMap.put("fieldToColumn", fieldToColumnMap);
						currValueMap.put("columnToField", columnToFieldMap);
						fieldColumnMap.put(tableName, currValueMap);
					} else {
						fieldToColumnMap = currValueMap.get("fieldToColumn");
						columnToFieldMap = currValueMap.get("columnToField");
					}

					for (Map.Entry<String, String> el : valueMap.entrySet()) {
						String fieldName = el.getKey();
						String columnName = el.getValue();
						fieldToColumnMap.put(fieldName, columnName);
						columnToFieldMap.put(columnName, fieldName);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new CustomBankException("Error in Mapping!", e);
			}
		}
	}

	public Map<String, Map<String, String>> getClassTableMap() {
		return classTableMap;
	}

	public Map<String, Map<String, Map<String, String>>> getFieldColumnMap() {
		return fieldColumnMap;
	}
}
