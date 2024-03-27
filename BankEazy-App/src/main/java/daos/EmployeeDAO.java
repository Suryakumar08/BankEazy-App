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
import model.Employee;
import utilities.Validators;

public class EmployeeDAO implements EmployeeDaoInterface {

	private String dbName = "BankEazy";

	private final String selectEmployeeQuery = "select * from User join Employee on User.id = Employee.userId";
	private final String insertUserQuery = "insert into User(name, password, mobile, gender, dob, status, type) values(?, ?, ?, ?, ?, ?, ?)";
	private final String insertEmployeeQuery = "insert into Employee values(?, ?, ?, ?)";

	// create
	@Override
	public int addEmployee(Employee employee) throws CustomBankException {
		Validators.checkNull(employee);
		int newId = -1;
		Connection connection = JDBCConnector.getConnection(dbName);
		try {
			connection.setAutoCommit(false);
			try (PreparedStatement statement = connection.prepareStatement(insertUserQuery,
					Statement.RETURN_GENERATED_KEYS)) {

				statement.setObject(1, employee.getName());
				statement.setObject(2, employee.getPassword());
				statement.setObject(3, employee.getMobile());
				statement.setObject(4, employee.getGender());
				statement.setObject(5, employee.getDob());
				statement.setObject(6, employee.getStatus());
				statement.setObject(7, employee.getType());

				int result = statement.executeUpdate();
				if (result == 0) {
					throw new CustomBankException(CustomBankException.ERROR_OCCURRED);
				}

				try (ResultSet genKeys = statement.getGeneratedKeys()) {
					while (genKeys.next()) {
						newId = genKeys.getInt(1);
					}
				}

				try (PreparedStatement statement1 = connection.prepareStatement(insertEmployeeQuery)) {
					statement1.setObject(1, newId);
					statement1.setObject(2, employee.getSalary());
					statement1.setObject(3, employee.getJoiningDate());
					statement1.setObject(4, employee.getBranchId());
					int noOfRows = statement1.executeUpdate();
					connection.commit();
					if (noOfRows == 0) {
						return -1;
					}
				}
			}
			return newId;

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
	}

	// read

	@Override
	public Map<Integer, Employee> getEmployees(Employee employee, int limit, long offset) throws CustomBankException {
		Validators.checkNull(employee);
		DAOHelper daoHelper = new DAOHelper();
		Map<Integer, Employee> employeeMap = null;
		StringBuilder query = new StringBuilder(selectEmployeeQuery);
		daoHelper.addWhereConditions(query, employee);
		query.append(" limit ? offset ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				int indexToAdd = daoHelper.setFields(statement, employee);
				statement.setObject(indexToAdd++, limit);
				statement.setObject(indexToAdd++, offset);
				try (ResultSet result = statement.executeQuery()) {
					Map<String, Method> settersMap = daoHelper.getSettersMap(Employee.class);
					while (result.next()) {
						if (employeeMap == null) {
							employeeMap = new HashMap<>();
						}
						Employee currEmployee = daoHelper.mapResultSetToGivenClassObject(result, Employee.class,
								settersMap);
						employeeMap.put(currEmployee.getId(), currEmployee);

					}
					return employeeMap;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e.getCause());
		}
	}

	@Override
	public Map<Integer, Employee> getEmployeesJoinedBetween(long from, long to, int limit, long offset)
			throws CustomBankException {
		DAOHelper daoHelper = new DAOHelper();
		Map<Integer, Employee> employeeMap = null;
		StringBuilder query = new StringBuilder(selectEmployeeQuery);
		query.append(" where joiningDate between ? and ? limit ? offset ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				statement.setObject(1, from);
				statement.setObject(2, to);
				statement.setObject(3, limit);
				statement.setObject(4, offset);
				try (ResultSet result = statement.executeQuery()) {
					Map<String, Method> settersMap = daoHelper.getSettersMap(Employee.class);
					while (result.next()) {
						if (employeeMap == null) {
							employeeMap = new HashMap<>();
						}
						Employee currEmployee = daoHelper.mapResultSetToGivenClassObject(result, Employee.class,
								settersMap);
						employeeMap.put(currEmployee.getId(), currEmployee);

					}
					return employeeMap;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e.getCause());
		}
	}

	// update
	@Override
	public boolean updateEmployee(Employee employee, long employeeId) throws CustomBankException {
		Validators.checkNull(employee);
		DAOHelper daoHelper = new DAOHelper();
		StringBuilder updateQuery = new StringBuilder("Update Employee join User on Employee.userId = User.id");
		updateQuery.append(daoHelper.generateUpdateQuery(employee));
		updateQuery.append(" where User.id = ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(updateQuery.toString())) {
				int parameterIndexToSet = daoHelper.setFields(statement, employee);
				statement.setObject(parameterIndexToSet++, employeeId);
				int noOfRowsAffected = statement.executeUpdate();
				return noOfRowsAffected > 0;
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

}
