package daos;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import exception.CustomBankException;
import jdbc.JDBCConnector;
import model.Customer;
import model.User;

public class UserDAO implements UserDaoInterface {

	private String dbName = "BankEazy";
	StringBuilder selectAllQuery = new StringBuilder("select * from User");

	// read
	public User getUser(int userId) throws CustomBankException {

		StringBuilder query = new StringBuilder(selectAllQuery);
		query.append(" where id = ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				statement.setInt(1, userId);
				try (ResultSet result = statement.executeQuery()) {
					User user = null;
					DAOHelper daoHelper = new DAOHelper();
					Map<String, Method> settersMap = daoHelper.getSettersMap(User.class);
					if (result.next()) {
						user = daoHelper.mapResultSetToGivenClassObject(result, User.class, settersMap);
					}
					return user;
				}
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}
	
	//update
	@Override
	public void updateUser(User user, int userId) throws CustomBankException {
		DAOHelper daoHelper = new DAOHelper();

		StringBuilder updateQuery = new StringBuilder("Update User");
		updateQuery.append(daoHelper.generateUpdateQuery(user));
		updateQuery.append(" where User.id = ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(updateQuery.toString())) {
				int parameterIndexToSet = daoHelper.setFields(statement, user);
				statement.setObject(parameterIndexToSet++, userId);
				int noOfRowsAffected = statement.executeUpdate();
				if(noOfRowsAffected < 1) {
					throw new CustomBankException(CustomBankException.ERROR_OCCURRED);
				}
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

}
