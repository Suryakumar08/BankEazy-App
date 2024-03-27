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
import model.Account;
import utilities.Validators;

public class AccountDAO implements AccountDaoInterface {
	private String dbName = "BankEazy";

	private String addAccountQuery = "Insert into Account(customerId, balance, branchId, status) values(?, ?, ?, ?)";
	private String selectAccountsQuery = "Select accountNo, customerId, balance, branchId, status from Account";

	// create
	@Override
	public long addAccount(Account account) throws CustomBankException {
		Validators.checkNull(account);
		long accNo = -1l;
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(addAccountQuery.toString(),
					Statement.RETURN_GENERATED_KEYS)) {
				statement.setObject(1, account.getCustomerId());
				statement.setObject(2, account.getBalance());
				statement.setObject(3, account.getBranchId());
				statement.setObject(4, account.getStatus());
				int noOfRowsAffected = statement.executeUpdate();
				if (noOfRowsAffected == 0) {
					throw new CustomBankException(CustomBankException.ERROR_OCCURRED);
				}

				try (ResultSet genKeys = statement.getGeneratedKeys()) {
					if (genKeys.next()) {
						accNo = genKeys.getLong(1);
					}
				}
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
		return accNo;
	}

	// read
	@Override
	public Map<Long, Account> getAccounts(Account account, int limit, long offset) throws CustomBankException {
		Validators.checkNull(account);
		Map<Long, Account> accountMap = null;
		DAOHelper daoHelper = new DAOHelper();
		StringBuilder query = new StringBuilder(selectAccountsQuery);
		daoHelper.addWhereConditions(query, account);
		query.append(" limit ? offset ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				int indexToAdd = daoHelper.setFields(statement, account);
				statement.setObject(indexToAdd++, limit);
				statement.setObject(indexToAdd++, offset);
				try (ResultSet accounts = statement.executeQuery()) {
					Map<String, Method> settersMap = daoHelper.getSettersMap(Account.class);
					while (accounts.next()) {
						if (accountMap == null) {
							accountMap = new HashMap<>();
						}
						Account currAccount = daoHelper.mapResultSetToGivenClassObject(accounts, Account.class,
								settersMap);
						accountMap.put(currAccount.getAccountNo(), currAccount);
					}
				}
				return accountMap;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	// update
	@Override
	public boolean updateAccount(Account account) throws CustomBankException {
		Validators.checkNull(account);
		DAOHelper helper = new DAOHelper();
		StringBuilder query = new StringBuilder("update Account");
		query.append(helper.generateUpdateQuery(account));
		query.append(" where accountNo = ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				int index = helper.setFields(statement, account);
				statement.setLong(index++, account.getAccountNo());

				int noOfRowsAffected = statement.executeUpdate();

				if (noOfRowsAffected == 0) {
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

}
