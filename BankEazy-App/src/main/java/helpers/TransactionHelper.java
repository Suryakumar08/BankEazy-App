package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import daos.TransactionDaoInterface;
import enums.TransactionStatus;
import enums.TransactionType;
import exception.CustomBankException;
import model.Account;
import model.Transaction;
import utilities.Utilities;
import utilities.Validators;

public class TransactionHelper {

	private static final double MIN_DEPOSIT = 1;
	private static final double MAX_DEPOSIT = 20000.01;
	private static final double MIN_WITHDRAW = 100;
	private static final double MAX_WITHDRAW = 20000.01;
	private static final double MIN_TRANSACTION_AMOUNT = 0.1;
	private static final double MAX_TRANSACTION_AMOUNT = 200000.01;
	private TransactionDaoInterface transactionDao = null;
	private AccountHelper accHelper = null;

	public TransactionHelper() throws CustomBankException {

		accHelper = new AccountHelper();

		Class<?> TransactionDAO;
		Constructor<?> transDao;

		try {
			TransactionDAO = Class.forName("daos.TransactionDAO");
			transDao = TransactionDAO.getDeclaredConstructor();
			transactionDao = (TransactionDaoInterface) transDao.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	// create
	public long depositAmount(long accountNo, double amount) throws CustomBankException {
		Validators.checkRangeBound(amount, TransactionHelper.MIN_DEPOSIT, TransactionHelper.MAX_DEPOSIT, "Invalid Deposit Amount!");
		Transaction currTransaction = setSelfTransactionDetails(accountNo, amount);
		currTransaction.setTypeFromEnum(TransactionType.DEPOSIT);
		return makeBankTransaction(currTransaction, false);
	}

	public long withdrawAmount(long accountNo, double amount) throws CustomBankException {
		Validators.checkRangeBound(amount, TransactionHelper.MIN_WITHDRAW, TransactionHelper.MAX_WITHDRAW, "Invalid Withdraw Amount!");
		Transaction currTransaction = setSelfTransactionDetails(accountNo, amount);
		currTransaction.setTypeFromEnum(TransactionType.WITHDRAW);
		return makeBankTransaction(currTransaction, false);
	}

	private Transaction setSelfTransactionDetails(long accountNo, double amount) throws CustomBankException {
		Account account = accHelper.getAccount(accountNo);
		Transaction currTransaction = new Transaction();
		currTransaction.setAccountNo(accountNo);
		currTransaction.setCustomerId(account.getCustomerId());
		currTransaction.setTransactionAccountNo(0);
		currTransaction.setDescription("");
		currTransaction.setAmount(amount);
		return currTransaction;
	}

	public long makeBankTransaction(Transaction currTransaction, boolean isInterBank) throws CustomBankException {
		Transaction recipientTransaction = null;
		Account currAccount = null;
		Validators.checkNull(currTransaction);
		Validators.checkRangeBound(currTransaction.getAmount(), TransactionHelper.MIN_TRANSACTION_AMOUNT, TransactionHelper.MAX_TRANSACTION_AMOUNT, "Invalid Amount!");

		double currAmount = currTransaction.getAmount();
		long currAccountNo = currTransaction.getAccountNo();
		long transactionAccountNo = currTransaction.getTransactionAccountNo();

		isValidTransaction(currAccountNo, transactionAccountNo);

		currAccount = accHelper.getAccount(currAccountNo);
		Validators.checkNull(currAccount);
		accHelper.isActiveBankAccount(currAccount);

		currTransaction.setTime(Utilities.getCurrentTime());
		currTransaction.setStatusFromEnum(TransactionStatus.SUCCESS);

		double currBalance = currAccount.getBalance();

		long lastTransId = -1;
		long transactionReferenceNo = -1;
		lastTransId = getLastTransactionId();
		currTransaction.setTransactionId(lastTransId + 1);
		
		if (currTransaction.getType() == TransactionType.WITHDRAW.getType()
				|| currTransaction.getType() == TransactionType.DEBIT.getType()) {
			double closingBalance = currBalance - currAmount;
			if (closingBalance < 0) {
				throw new CustomBankException(CustomBankException.NOT_ENOUGH_BALANCE);
			}
			currTransaction.setAmount(0 - currAmount);
			currTransaction.setClosingBalance(closingBalance);

			if (currTransaction.getType() == TransactionType.DEBIT.getType() && isInterBank == false) {
				recipientTransaction = getRecipientTransactionDetails(currTransaction);
			}
			
		} else if (currTransaction.getType() == TransactionType.DEPOSIT.getType()
				|| currTransaction.getType() == TransactionType.CREDIT.getType()) {
			double closingBalance = currBalance + currTransaction.getAmount();
			currTransaction.setClosingBalance(closingBalance);
		} else {
			throw new CustomBankException(CustomBankException.INVALID_TRANSACTION);
		}
		
		if(recipientTransaction != null) {
			transactionReferenceNo = transactionDao.addTransactions(currTransaction, recipientTransaction);
		}
		else {
			transactionReferenceNo = transactionDao.addTransactions(currTransaction);
		}
		return transactionReferenceNo;

	}

	private Transaction getRecipientTransactionDetails(Transaction currTransaction)
			throws CustomBankException {
		Transaction recipientTransaction = new Transaction();
		Account recipientAccount = accHelper.getAccount(currTransaction.getTransactionAccountNo());
		Validators.checkNull(recipientAccount);
		accHelper.isActiveBankAccount(recipientAccount);

		recipientTransaction.setAccountNo(currTransaction.getTransactionAccountNo());
		recipientTransaction.setCustomerId(recipientAccount.getCustomerId());
		recipientTransaction.setTransactionAccountNo(currTransaction.getAccountNo());
		recipientTransaction.setDescription(currTransaction.getDescription());
		recipientTransaction.setTransactionId(currTransaction.getTransactionId());
		recipientTransaction.setAmount(currTransaction.getAmount());
		recipientTransaction.setTypeFromEnum(TransactionType.CREDIT);
		recipientTransaction.setTime(Utilities.getCurrentTime());
		recipientTransaction.setStatusFromEnum(TransactionStatus.SUCCESS);

		double currBalance = recipientAccount.getBalance();
		double closingBalance = currBalance + currTransaction.getAmount();
		recipientTransaction.setClosingBalance(closingBalance);

		return recipientTransaction;
	}

	// read
	public Map<Long, Transaction> getAccountTransactions(long accountNo, long from, long to, int limit, long offset)
			throws CustomBankException {
		Transaction transaction = new Transaction();
		transaction.setAccountNo(accountNo);
		return getTransactions(transaction, from, to, limit, offset);
	}

	public Map<Long, Transaction> getCustomerTransactions(int customerId, long from, long to, int limit, long offset)
			throws CustomBankException {
		Transaction transaction = new Transaction();
		transaction.setCustomerId(customerId);
		return getTransactions(transaction, from, to, limit, offset);
	}

	public Map<Long, Transaction> getTransactions(Transaction transaction, long from, long to, int limit, long offset)
			throws CustomBankException {
		Map<Long, Transaction> transactions = transactionDao.getTransactions(transaction, from, to, limit, offset);
		if (transactions == null) {
			throw new CustomBankException("No Transactions Exist!");
		}
		return transactions;
	}

	private long getLastTransactionId() throws CustomBankException {
		return transactionDao.getLastTransactionId();
	}

	// checking
	public boolean isValidTransaction(long userAccountNo, long recipientAccountNo) throws CustomBankException {
		if (userAccountNo == recipientAccountNo) {
			throw new CustomBankException(CustomBankException.INVALID_TRANSACTION);
		}
		return true;
	}

}
