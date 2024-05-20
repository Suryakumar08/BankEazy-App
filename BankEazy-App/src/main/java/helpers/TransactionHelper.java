package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.lock.Locker;

import daos.TransactionDaoInterface;
import enums.AccountStatus;
import enums.TransactionStatus;
import enums.TransactionType;
import exception.CustomBankException;
import helpers.InterBankProto.InterBank;
import model.Account;
import model.Audit;
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
	public long depositAmount(long accountNo, double amount, long lastModifiedOn, int lastModifiedBy)
			throws CustomBankException {
		Validators.checkRangeBound(amount, TransactionHelper.MIN_DEPOSIT, TransactionHelper.MAX_DEPOSIT,
				"Invalid Deposit Amount!");
		Transaction currTransaction = setSelfTransactionDetails(accountNo, amount);
		currTransaction.setLastModifiedBy(lastModifiedBy);
		currTransaction.setLastModifiedOn(lastModifiedOn);
		currTransaction.setTypeFromEnum(TransactionType.DEPOSIT);
		return makeBankTransaction(currTransaction, false);
	}

	public long withdrawAmount(long accountNo, double amount, long lastModifiedOn, int lastModifiedBy)
			throws CustomBankException {
		Validators.checkRangeBound(amount, TransactionHelper.MIN_WITHDRAW, TransactionHelper.MAX_WITHDRAW,
				"Invalid Withdraw Amount!");
		Transaction currTransaction = setSelfTransactionDetails(accountNo, amount);
		currTransaction.setLastModifiedBy(lastModifiedBy);
		currTransaction.setLastModifiedOn(lastModifiedOn);
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
	
	
	@SuppressWarnings("finally")
	public boolean creditAmount(InterBank currTransaction) throws CustomBankException{
		long referenceNo = -1;
		Audit audit = new Audit();
		try {
		Long currentTime = Utilities.getCurrentTime();
		audit.setTime(currentTime);
		audit.setAction("Inter bank Transfer");
		Transaction transaction = new Transaction();
		transaction.setAccountNo(currTransaction.getReceiverAccNo());
		Account account = accHelper.getAccount(currTransaction.getReceiverAccNo());
		if(account == null) {
			throw new CustomBankException("No Account exists");
		}
		if(account.getStatus() == AccountStatus.INACTIVE.getStatus()) {
			throw new CustomBankException("Account is inactive!");
		}
		int userId = account.getCustomerId();
		audit.setUserId(userId);
		transaction.setCustomerId(userId);
		transaction.setTransactionAccountNo(currTransaction.getSenderAccNo());
		audit.setTargetId("" + currTransaction.getSenderAccNo());
		transaction.setDescription(currTransaction.getDescription());
		transaction.setAmount(currTransaction.getAmount());
		transaction.setType(TransactionType.CREDIT.getType());
		transaction.setLastModifiedBy(userId);
		transaction.setLastModifiedOn(currentTime);

		referenceNo = makeBankTransaction(transaction, true);
		audit.setStatus("success");
		return referenceNo != -1;
		}catch(CustomBankException ex) {
			audit.setDescription("Interbank transaction failed!" + ex);
			audit.setStatus("failure");
			throw ex;
		}
		finally {
			new AuditHelper().insertAudit(audit);
		}
	}

	public long makeBankTransaction(Transaction currTransaction, boolean isInterBank) throws CustomBankException {
		Transaction recipientTransaction = null;
		Account currAccount = null;
		Validators.checkNull(currTransaction);
		Validators.checkRangeBound(currTransaction.getAmount(), TransactionHelper.MIN_TRANSACTION_AMOUNT,
				TransactionHelper.MAX_TRANSACTION_AMOUNT, "Invalid Amount!");

		long currAccountNo = currTransaction.getAccountNo();
		long transactionAccountNo = currTransaction.getTransactionAccountNo();

		long minLock = Math.min(currAccountNo, transactionAccountNo);
		long maxLock = Math.min(currAccountNo, transactionAccountNo);
		long transactionReferenceNo = -1;
		try {
			synchronized (Locker.lock("AccountNo" + minLock)) {
				synchronized (Locker.lock("AccountNo" + maxLock)) {

					double currAmount = currTransaction.getAmount();

					if (!isInterBank && currTransaction.getType() != TransactionType.DEPOSIT.getType()
							&& currTransaction.getType() != TransactionType.WITHDRAW.getType()) {
						try {
							Account receiverAccount = accHelper.getAccount(transactionAccountNo);
							if (receiverAccount == null) {
								throw new CustomBankException("Receiver Account not found!");
							}
						} catch (CustomBankException ex) {
							throw new CustomBankException("No Receiver Account Found!");
						}
					}

					isValidTransaction(currAccountNo, transactionAccountNo);

					currAccount = accHelper.getAccount(currAccountNo);
					Validators.checkNull(currAccount);
					accHelper.isActiveBankAccount(currAccount);

					currTransaction.setTime(Utilities.getCurrentTime());
					if(currTransaction.getStatus() == null) {
						currTransaction.setStatusFromEnum(TransactionStatus.SUCCESS);
					}

					double currBalance = currAccount.getBalance();

					long lastTransId = -1;
					lastTransId = getLastTransactionId();
					currTransaction.setTransactionId(lastTransId + 1);

					if (currTransaction.getType() == TransactionType.WITHDRAW.getType()
							|| currTransaction.getType() == TransactionType.DEBIT.getType()) {
						double closingBalance = currTransaction.getStatus() == TransactionStatus.SUCCESS.getStatus() ? (currBalance - currAmount) : currBalance;
						if (closingBalance < 0) {
							throw new CustomBankException(CustomBankException.NOT_ENOUGH_BALANCE);
						}
						currTransaction.setClosingBalance(closingBalance);

						if (currTransaction.getType() == TransactionType.DEBIT.getType() && isInterBank == false) {
							recipientTransaction = getRecipientTransactionDetails(currTransaction);
						}

					} else if (currTransaction.getType() == TransactionType.DEPOSIT.getType()
							|| currTransaction.getType() == TransactionType.CREDIT.getType()) {
						double closingBalance = currTransaction.getStatus() == TransactionStatus.SUCCESS.getStatus() ? (currBalance +  currTransaction.getAmount()) : currBalance;
						currTransaction.setClosingBalance(closingBalance);
					} else {
						throw new CustomBankException(CustomBankException.INVALID_TRANSACTION);
					}

					if (recipientTransaction != null) {
						transactionReferenceNo = transactionDao.addTransactions(currTransaction, recipientTransaction);
						accHelper.updateCacheBalance(currTransaction.getAccountNo(), currTransaction.getClosingBalance());
						accHelper.updateCacheBalance(recipientTransaction.getAccountNo(), recipientTransaction.getClosingBalance());
					} else {
						transactionReferenceNo = transactionDao.addTransactions(currTransaction);
						accHelper.updateCacheBalance(currTransaction.getAccountNo(), currTransaction.getClosingBalance());
					}
				}
				return transactionReferenceNo;
			}
		} finally {
			Locker.unLock("AccountNo" + minLock);
			Locker.unLock("AccountNo" + maxLock);
		}

	}

	private Transaction getRecipientTransactionDetails(Transaction currTransaction) throws CustomBankException {
		Transaction recipientTransaction = new Transaction();
		Account recipientAccount = accHelper.getAccount(currTransaction.getTransactionAccountNo());
		Validators.checkNull(recipientAccount);
		accHelper.isActiveBankAccount(recipientAccount);

		recipientTransaction.setAccountNo(recipientAccount.getAccountNo());
		recipientTransaction.setCustomerId(recipientAccount.getCustomerId());
		recipientTransaction.setTransactionAccountNo(currTransaction.getAccountNo());
		recipientTransaction.setTransactionId(currTransaction.getTransactionId());
		recipientTransaction.setAmount(Math.abs(currTransaction.getAmount()));
		recipientTransaction.setTypeFromEnum(TransactionType.CREDIT);
		recipientTransaction.setTime(Utilities.getCurrentTime());
		recipientTransaction.setStatusFromEnum(TransactionStatus.SUCCESS);
		recipientTransaction.setDescription(currTransaction.getDescription());
		recipientTransaction.setLastModifiedOn(currTransaction.getLastModifiedOn());
		recipientTransaction.setLastModifiedBy(currTransaction.getLastModifiedBy());

		double currBalance = recipientAccount.getBalance();
		double closingBalance = currBalance + Math.abs(currTransaction.getAmount());
		recipientTransaction.setClosingBalance(closingBalance);

		return recipientTransaction;
	}

	// read

	public int getNoOfTransactions(long accountNo, long from, long to) throws CustomBankException {
		return transactionDao.getNoOfTransactions(accountNo, from, to);
	}

	public List<Transaction> getTransactionsList(long accountNo, long from, long to, int limit, long offset)
			throws CustomBankException {
		Transaction transaction = new Transaction();
		transaction.setAccountNo(accountNo);
		return transactionDao.getTransactionsList(transaction, from, to, limit, offset);
	}

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
