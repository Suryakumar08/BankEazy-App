package daos;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.CustomBankException;
import jdbc.JDBCConnector;
import model.Transaction;
import utilities.Utilities;
import utilities.Validators;

public class TransactionDAO implements TransactionDaoInterface {

	private String dbName = "BankEazy";
	private String addTransactionQuery = "insert into Transaction(customerId, accountNo, transactionAccountNo, description, type, status, time, closingBalance, transactionId, amount, lastModifiedOn, lastModifiedBy) values(?,?,?,?,?,?,?,?,?,?,?,?)";
	private String lastTransactionIdQuery = "select MAX(transactionId) as maxId from Transaction";
	private String getTransactionQuery = "select customerId, accountNo, transactionAccountNo, description, type, status, time, closingBalance, transactionId, amount, referenceNo from Transaction";
	private String updateAmountQuery = "update Account set balance = ?, lastModifiedOn = ?, lastModifiedBy = ? where accountNo = ?";
	private String getNoOfTransactionsQuery = "select count(*) from Transaction where Transaction.accountNo = ? and Transaction.time between ? and ? order by time desc";

	// create
	@Override
	public long addTransactions(Transaction... transactions) throws CustomBankException {
		long firstTransactionReferenceNo = -1l;
		Connection connection = null;
		connection = JDBCConnector.getConnection(dbName);
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
		}
		try {
			for (Transaction transaction : transactions) {
				Validators.checkNull(transaction);
				try (PreparedStatement statement = connection.prepareStatement(addTransactionQuery,
						Statement.RETURN_GENERATED_KEYS)) {
					statement.setObject(1, transaction.getCustomerId());
					statement.setObject(2, transaction.getAccountNo());
					statement.setObject(3, transaction.getTransactionAccountNo());
					statement.setObject(4, Utilities.sanitizeString(transaction.getDescription(), "Description is null!"));
					statement.setObject(5, transaction.getType());
					statement.setObject(6, transaction.getStatus());
					statement.setObject(7, transaction.getTime());
					statement.setObject(8, transaction.getClosingBalance());
					statement.setObject(9, transaction.getTransactionId());
					statement.setObject(10, transaction.getAmount());
					statement.setObject(11, transaction.getLastModifiedOn());
					statement.setObject(12, transaction.getLastModifiedBy());

										
					int noOfRowsAffected = statement.executeUpdate();
					if (noOfRowsAffected < 1) {
						throw new CustomBankException(CustomBankException.TRANSACTION_FAILED);
					}

					if (firstTransactionReferenceNo == -1) {
						try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
							if (generatedKeys.next()) {
								firstTransactionReferenceNo = generatedKeys.getLong(1);
							}
						}
					}

					if (noOfRowsAffected < 1) {
						throw new CustomBankException(CustomBankException.TRANSACTION_FAILED);
					}
				}

				try (PreparedStatement amountUpdateStatement = connection.prepareStatement(updateAmountQuery)) {
					amountUpdateStatement.setObject(1, transaction.getClosingBalance());
					amountUpdateStatement.setObject(2, transaction.getLastModifiedOn());
					amountUpdateStatement.setObject(3, transaction.getLastModifiedBy());
					amountUpdateStatement.setObject(4, transaction.getAccountNo());

					int affectedRows = amountUpdateStatement.executeUpdate();
					if (affectedRows < 1) {
						throw new CustomBankException(CustomBankException.ERROR_OCCURRED);
					}
				}
			}
		} catch (SQLException | CustomBankException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new CustomBankException(CustomBankException.TRANSACTION_FAILED, e);
		} finally {
			try {
				connection.setAutoCommit(true);
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return firstTransactionReferenceNo;

	}

	// read

	@Override
	public int getNoOfTransactions(long accountNo, long from, long to) throws CustomBankException {
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(getNoOfTransactionsQuery)) {
				statement.setObject(1, accountNo);
				statement.setObject(2, from);
				statement.setObject(3, to);
				ResultSet result = statement.executeQuery();
				if(result.next()) {
					return result.getInt(1);
				}
				return 0;
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	@Override
	public long getLastTransactionId() throws CustomBankException {
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet maxIdSet = statement.executeQuery(lastTransactionIdQuery)) {
					if (maxIdSet.next()) {
						return maxIdSet.getLong("maxId");
					} else {
						return 0;
					}
				}
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	@Override
	public Map<Long, Transaction> getTransactions(Transaction transaction, long from, long to, int limit, long offset)
			throws CustomBankException {
		Validators.checkNull(transaction);
		DAOHelper daoHelper = new DAOHelper();
		StringBuilder query = new StringBuilder(getTransactionQuery);
		daoHelper.addWhereConditions(query, transaction);
		query.append(" and time between ? and ? order by time DESC limit ? offset ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				int parameterIndexToAdd = daoHelper.setFields(statement, transaction);
				statement.setObject(parameterIndexToAdd++, from);
				statement.setObject(parameterIndexToAdd++, to);
				statement.setObject(parameterIndexToAdd++, limit);
				statement.setObject(parameterIndexToAdd++, offset);
				Map<Long, Transaction> transactionsMap = null;
				try (ResultSet transactions = statement.executeQuery()) {
					Map<String, Method> settersMap = daoHelper.getSettersMap(Transaction.class);
					while (transactions.next()) {
						if (transactionsMap == null) {
							transactionsMap = new HashMap<>();
						}
						Transaction currTransaction = daoHelper.mapResultSetToGivenClassObject(transactions,
								Transaction.class, settersMap);
						transactionsMap.put(currTransaction.getReferenceNo(), currTransaction);
					}
					return transactionsMap;
				}
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	@Override
	public List<Transaction> getTransactionsList(Transaction transaction, long from, long to, int limit, long offset)
			throws CustomBankException {
		Validators.checkNull(transaction);
		DAOHelper daoHelper = new DAOHelper();
		StringBuilder query = new StringBuilder(getTransactionQuery);
		daoHelper.addWhereConditions(query, transaction);
		query.append(" and time between ? and ? order by time DESC limit ? offset ?");
		try (Connection connection = JDBCConnector.getConnection(dbName)) {
			try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
				int parameterIndexToAdd = daoHelper.setFields(statement, transaction);
				statement.setObject(parameterIndexToAdd++, from);
				statement.setObject(parameterIndexToAdd++, to);
				statement.setObject(parameterIndexToAdd++, limit);
				statement.setObject(parameterIndexToAdd++, offset);
				List<Transaction> transactionsList = null;
				try (ResultSet transactions = statement.executeQuery()) {
					Map<String, Method> settersMap = daoHelper.getSettersMap(Transaction.class);
					while (transactions.next()) {
						if (transactionsList == null) {
							transactionsList = new ArrayList<>();
						}
						Transaction currTransaction = daoHelper.mapResultSetToGivenClassObject(transactions,
								Transaction.class, settersMap);
						transactionsList.add(currTransaction);
					}
					return transactionsList;
				}
			}
		} catch (SQLException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

}
