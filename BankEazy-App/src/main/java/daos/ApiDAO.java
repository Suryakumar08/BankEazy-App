package daos;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import exception.CustomBankException;
import jdbc.JDBCConnector;
import model.ApiData;
import utilities.Validators;

public class ApiDAO implements ApiDaoInterface{
	
	private final String dbName = "BankEazy";
	private final String selectUserApiListQuery = "select * from ApiData where userId = ?";
	private final String selectOneApiData = "select * from ApiData where apiKey = ?";

	@Override
	public List<ApiData> getApiList(int userId) throws CustomBankException {
		List<ApiData> datas = null;
		try(Connection con = JDBCConnector.getConnection(dbName)){
			try(PreparedStatement statement = con.prepareStatement(selectUserApiListQuery)){
				statement.setObject(1, userId);
				try(ResultSet dataSet = statement.executeQuery()){
					DAOHelper daoHelper = new DAOHelper();
					Map<String, Method> settersMap = daoHelper.getSettersMap(ApiData.class);
					while(dataSet.next()) {
						if(datas == null) {
							datas = new ArrayList<ApiData>();
						}
						datas.add(daoHelper.mapResultSetToGivenClassObject(dataSet, ApiData.class, settersMap));
					}
				}
			}
			return datas;
		} catch (SQLException e) {
			throw new CustomBankException(e.getMessage());
		}
	}

	@Override
	public boolean addApi(ApiData apiData) throws CustomBankException {
		Validators.checkNull(apiData);
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement("insert into ApiData values(?, ?, ?, ?, ?)")) {
				statement.setObject(1, apiData.getApiKey());
				statement.setObject(2, apiData.getUserId());
				statement.setObject(3, apiData.getCreatedAt());
				statement.setObject(4, apiData.getValidity());
				statement.setObject(5, apiData.getScope());
				return (statement.executeUpdate() > 0);
			}
		} catch (SQLException e) {
			throw new CustomBankException("Api addition failed!", e);
		}
	}

	@Override
	public boolean removeApi(String apiKey) throws CustomBankException {
		Validators.checkNull(apiKey, "Empty Api key!");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement("delete from ApiData where apiKey = ?")) {
				statement.setObject(1, apiKey);
				return (statement.executeUpdate() > 0);
			}
		} catch (SQLException e) {
			throw new CustomBankException("Api Deletion failed!", e);
		}
	}

	@Override
	public ApiData getApiData(String apiKey) throws CustomBankException {
		Validators.checkNull(apiKey, "Empty Api key!");
		ApiData apiData = null;
		try(Connection con = JDBCConnector.getConnection(dbName)){
			try(PreparedStatement statement = con.prepareStatement(selectOneApiData)){
				statement.setObject(1, apiKey);
				try(ResultSet dataSet = statement.executeQuery()){
					DAOHelper daoHelper = new DAOHelper();
					Map<String, Method> settersMap = daoHelper.getSettersMap(ApiData.class);
					if(dataSet.next()) {
						return daoHelper.mapResultSetToGivenClassObject(dataSet, ApiData.class, settersMap);
					}
				}
			}
			return apiData;
		} catch (SQLException e) {
			throw new CustomBankException(e.getMessage());
		}
	}

}
