package daos;

import java.util.List;
import java.util.Map;

import exception.CustomBankException;
import model.Transaction;

public interface TransactionDaoInterface {
	
	//create
	long addTransactions(Transaction ...transactions) throws CustomBankException;
	
	//read
	int getNoOfTransactions(long accountNo, long from, long to) throws CustomBankException;
	
	long getLastTransactionId() throws CustomBankException;
	
	List<Transaction> getTransactionsList(Transaction  transaction, long from, long to, int limit, long offset) throws CustomBankException;
	
	Map<Long, Transaction> getTransactions(Transaction transaction, long from, long to, int limit, long offset) throws CustomBankException;

	
	
}
