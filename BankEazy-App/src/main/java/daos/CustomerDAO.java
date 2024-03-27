package daos;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import exception.CustomBankException;
import jdbc.JDBCConnector;
import model.Customer;
import utilities.Validators;

public class CustomerDAO implements CustomerDaoInterface {

	private String dbName = "BankEazy";

	private String insertUserQuery = "insert into User(name, password, phone, gender, dob, status, type) values(?, ?, ?, ?, ?, ?, ?)";
	private String insertCustomerQuery = "insert into Customer values(?, ?, ?)";
	private String getCustomerQuery = "select id, name, password, phone, gender, dob, status, type, pan, aadhar from User join Customer on User.id = Customer.userId";

	// create
	@Override
	public int addCustomer(Customer customer) throws CustomBankException {
		int newId = -1;
		Connection connection = JDBCConnector.getConnection(dbName);
		try {
			connection.setAutoCommit(false);
			try (PreparedStatement statement = connection.prepareStatement(insertUserQuery,
					Statement.RETURN_GENERATED_KEYS)) {

				statement.setObject(1, customer.getName());
				statement.setObject(2, customer.getPassword());
				statement.setObject(3, customer.getMobile());
				statement.setObject(4, customer.getGender());
				statement.setObject(5, customer.getDob());
				statement.setObject(6, customer.getStatus());
				statement.setObject(7, customer.getType());

				int result = statement.executeUpdate();
				if (result == 0) {
					throw new CustomBankException(CustomBankException.ERROR_OCCURRED);
				}

				try (ResultSet genKeys = statement.getGeneratedKeys()) {
					if (genKeys.next()) {
						newId = genKeys.getInt(1);
					}
				}

				try (PreparedStatement statement1 = connection.prepareStatement(insertCustomerQuery)) {
					statement1.setObject(1, newId);
					statement1.setObject(2, customer.getPan());
					statement1.setObject(3, customer.getAadhar());

					int noOfRows = statement1.executeUpdate();
					connection.commit();
					if (noOfRows == 0) {
						return -1;
					}
				}
			}

		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.addSuppressed(e1);
			}
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
		return newId;

	}

	// read
	@Override
	public Map<Integer, Customer> getCustomers(Customer customer, int limit, long offset) throws CustomBankException {
		Validators.checkNull(customer);
		DAOHelper daoHelper = new DAOHelper();
		Map<Integer, Customer> customerMap = null;
		StringBuilder query = new StringBuilder(getCustomerQuery);
		daoHelper.addWhereConditions(query, customer);
		query.append(" limit ? offset ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				int indexToAdd = daoHelper.setFields(statement, customer);
				statement.setObject(indexToAdd++, limit);
				statement.setObject(indexToAdd++, offset);
				try (ResultSet records = statement.executeQuery()) {
					Map<String, Method> settersMap = daoHelper.getSettersMap(Customer.class);
					while (records.next()) {
						if (customerMap == null) {
							customerMap = new HashMap<>();
						}
						Customer currCustomer = daoHelper.mapResultSetToGivenClassObject(records, Customer.class,
								settersMap);
						customerMap.put(currCustomer.getId(), currCustomer);
					}
					return customerMap;
				}
			} 
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	// update
	@Override
	public boolean updateCustomer(Customer customer, int customerId) throws CustomBankException {
		DAOHelper daoHelper = new DAOHelper();

		StringBuilder updateQuery = new StringBuilder("Update Customer join User on Customer.userId = User.id");
		updateQuery.append(daoHelper.generateUpdateQuery(customer));
		updateQuery.append(" where User.id = ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(updateQuery.toString())) {
				int parameterIndexToSet = daoHelper.setFields(statement, customer);
				statement.setObject(parameterIndexToSet++, customerId);
				int noOfRowsAffected = statement.executeUpdate();
				return noOfRowsAffected > 0;
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}
}
