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
import model.Branch;
import utilities.Validators;

public class BranchDAO implements BranchDaoInterface {
	private String dbName = "BankEazy";

	private String selectBranchQuery = "select id, name, address, ifsc from Branch";
	private String addBranchQuery = "insert into Branch(name, address, ifsc) values (?, ?, ?)";

	// create
	@Override
	public int addBranch(Branch branch) throws CustomBankException {
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement query = connection.prepareStatement(addBranchQuery,
					Statement.RETURN_GENERATED_KEYS)) {
				query.setObject(1, branch.getName());
				query.setObject(2, branch.getAddress());
				query.setObject(3, branch.getIfsc());

				int noOfRowsAffected = query.executeUpdate();

				int lastAddedBranchId = -1;

				try (ResultSet result = query.getGeneratedKeys()) {
					if (result.next()) {
						lastAddedBranchId = result.getInt(1);
					}
				}
				if (noOfRowsAffected < 0) {
					throw new CustomBankException(CustomBankException.ERROR_OCCURRED + " Branch not added!");
				}

				return lastAddedBranchId;
			}

		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	// read
	@Override
	public Map<Integer, Branch> getBranches(Branch branch, int limit, long offset) throws CustomBankException {
		Validators.checkNull(branch);
		Map<Integer, Branch> branchMap = null;
		DAOHelper daoHelper = new DAOHelper();
		StringBuilder query = new StringBuilder(selectBranchQuery);
		daoHelper.addWhereConditions(query, branch);
		query.append(" limit ? offset ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				int indexToAdd = daoHelper.setFields(statement, branch);
				statement.setObject(indexToAdd++, limit);
				statement.setObject(indexToAdd++, offset);
				try (ResultSet branches = statement.executeQuery()) {
					Map<String, Method> settersMap = daoHelper.getSettersMap(Branch.class);
					while (branches.next()) {
						if (branchMap == null) {
							branchMap = new HashMap<>();
						}
						Branch currBranch = daoHelper.mapResultSetToGivenClassObject(branches, Branch.class,
								settersMap);
						branchMap.put(currBranch.getId(), currBranch);
					}
					return branchMap;
				}
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	// update
	@Override
	public boolean updateBranch(Branch branch) throws CustomBankException {
		Validators.checkNull(branch);
		DAOHelper helper = new DAOHelper();
		StringBuilder updateBranchQuery = new StringBuilder("update Branch ");
		updateBranchQuery.append(helper.generateUpdateQuery(branch));
		updateBranchQuery.append(" where id = ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(updateBranchQuery.toString())) {
				int noOfParametersAdded = helper.setFields(statement, branch);
				statement.setObject(noOfParametersAdded, branch.getId());
				int noOfRowsAffected = statement.executeUpdate();
				return noOfRowsAffected > 0;
			} 
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

}
