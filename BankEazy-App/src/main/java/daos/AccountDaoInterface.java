package daos;

import java.util.Map;

import exception.CustomBankException;
import model.Account;

public interface AccountDaoInterface {

	//create
	long addAccount(Account account) throws CustomBankException;

	//read
	Map<Long, Account> getAccounts(Account account, int limit, long offset) throws CustomBankException;

	//update
	boolean updateAccount(Account account) throws CustomBankException;

}
