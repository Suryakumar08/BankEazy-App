package daos;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.CustomBankException;
import utilities.Validators;
import yamlConvertor.YamlMapper;

public class DAOHelper {

	private static YamlMapper mapper = null;
	
	private static Map<String, Map<String, String>> classTableMap = null;
	private static Map<String, Map<String, Map<String, String>>> columnFieldMap = null;

	public DAOHelper() throws CustomBankException {
		if(mapper == null) {
			mapper = new YamlMapper();
		}
		if (classTableMap == null) {
			classTableMap = mapper.getClassTableMap();
		}
		if (columnFieldMap == null) {
			columnFieldMap = mapper.getFieldColumnMap();
		}
	}

	public <T> T mapResultSetToGivenClassObject(ResultSet resultSet, Class<T> clazz, Map<String, Method> settersMap)
			throws CustomBankException {
		T givenClassInstance = null;
		try {
			givenClassInstance = clazz.getDeclaredConstructor().newInstance();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String tableName = metaData.getTableName(i);
				String className = classTableMap.get("classToTable").get(tableName);
				Validators.checkNull(className, "Mapping error!");
				String columnName = metaData.getColumnName(i);
				Object columnValue = resultSet.getObject(i);
				String fieldName = columnFieldMap.get(tableName).get("columnToField").get(columnName);
				Validators.checkNull(fieldName, "Mapping Error!");
				settersMap.get(fieldName).invoke(givenClassInstance, columnValue);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | SQLException e) {
			e.printStackTrace();
			throw new CustomBankException("Error in mapping ResltSet to " + clazz.getSimpleName(), e);
		}

		return givenClassInstance;

	}

	public Map<String, Method> getSettersMap(Class<?> clazz) {
		Map<String, Method> settersMap = new HashMap<>();
		for (Method method : clazz.getMethods()) {
			if (isSetter(method)) {
				String fieldName = getFieldNameFromGetterOrSetter(method.getName());
				settersMap.put(fieldName, method);
			}
		}
		return settersMap;
	}

	public String generateUpdateQuery(Object pojo) throws CustomBankException {

		Class<?> clazz = pojo.getClass();
		StringBuilder query = new StringBuilder();
		List<Field> fields = getAllFields(clazz);
		Collections.sort(fields, (a, b) -> a.getName().compareTo(b.getName()));
		Map<String, Method> gettersMap = getGettersMap(pojo);
		int noOfSetAdded = 0;
		for (Field field : fields) {
			try {
				String className = field.getDeclaringClass().getSimpleName();
				String fieldName = field.getName();
				Method getterMethod = gettersMap.get(fieldName);
				if (getterMethod != null) {
					Object fieldValue = getterMethod.invoke(pojo);
					if (fieldValue != null) {
						if (noOfSetAdded == 0) {
							query.append(" SET ");
							noOfSetAdded++;
						}
						String tableName = classTableMap.get("classToTable").get(className);
						Validators.checkNull(tableName, "Mapping Error!");
						String columnName = columnFieldMap.get(tableName).get("fieldToColumn").get(fieldName);
						Validators.checkNull(columnName, "Mapping Error!");
						query.append(tableName).append(".").append(columnName).append(" = ?, ");
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
			}
		}

		query.setLength(query.length() - 2);

		return query.toString();
	}

	public <T> int setFields(PreparedStatement statement, T pojo) throws CustomBankException {

		Map<String, Method> getterMethodMap = getGettersMap(pojo);

		Class<?> clazz = pojo.getClass();
		int parameterIndex = 1;
		List<Field> fields = getAllFields(clazz);
		Collections.sort(fields, (a, b) -> a.getName().compareTo(b.getName()));

		for (Field field : fields) {
			String fieldName = field.getName();
			Method getterMethod = getterMethodMap.get(fieldName);
			if (getterMethod != null) {
				try {
					Object value = getterMethod.invoke(pojo);
					if (value != null) {
						statement.setObject(parameterIndex++, value);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| SQLException e) {
					throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
				}
			}
		}
		return parameterIndex;
	}

	public <T> void addWhereConditions(StringBuilder query, T pojo) throws CustomBankException {
		int noOfParametersToCheck = 0;
		Class<?> clazz = pojo.getClass();
		List<Field> fields = getAllFields(clazz);
		Collections.sort(fields, (a, b) -> a.getName().compareTo(b.getName()));
		Map<String, Method> gettersMap = getGettersMap(pojo);
		for (Field field : fields) {
			try {
				String fieldName = field.getName();
				Method getterMethod = gettersMap.get(fieldName);
				if (getterMethod != null) {
					Object fieldValue = getterMethod.invoke(pojo);
					if (fieldValue != null) {
						String className = getterMethod.getDeclaringClass().getSimpleName();
						String tableName = classTableMap.get("classToTable").get(className);
						Validators.checkNull(tableName, "Mapping Error!");
						String columnName = columnFieldMap.get(tableName).get("fieldToColumn").get(fieldName);
						Validators.checkNull(columnName, "Mapping Error!");
						if (noOfParametersToCheck == 0) {
							query.append(" where ").append(tableName).append(".").append(columnName).append(" = ?");
							noOfParametersToCheck++;
						} else {
							query.append(" and ").append(tableName).append(".").append(columnName).append(" = ?");
							noOfParametersToCheck++;
						}
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
			}
		}
	}

	private List<Field> getAllFields(Class<?> clazz) {
		List<Field> allFields = new ArrayList<>();
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				allFields.add(field);
			}
			clazz = clazz.getSuperclass();
		}
		return allFields;
	}

	private <T> Map<String, Method> getGettersMap(T pojo) {
		Map<String, Method> getterMethodsMap = new HashMap<>();
		Class<?> clazz = pojo.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (isGetter(method)) {
				String fieldName = getFieldNameFromGetterOrSetter(method.getName());
				getterMethodsMap.put(fieldName, method);
			}
		}
		return getterMethodsMap;
	}

	private boolean isGetter(Method method) {
		return method.getName().startsWith("get") && method.getParameterCount() == 0;
	}

	private boolean isSetter(Method method) {
		return method.getName().startsWith("set") && method.getParameterCount() == 1;
	}

	private String getFieldNameFromGetterOrSetter(String methodName) {
		return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
	}
}
