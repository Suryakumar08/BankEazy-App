package daos;

import java.util.Map;

import exception.CustomBankException;
import model.Transaction;

public interface TransactionDaoInterface {
	
	//create
	long addTransactions(Transaction ...transactions) throws CustomBankException;
	
	//read
	long getLastTransactionId() throws CustomBankException;
	
	Map<Long, Transaction> getTransactions(Transaction transaction, long from, long to, int limit, long offset) throws CustomBankException;

	
	
}
