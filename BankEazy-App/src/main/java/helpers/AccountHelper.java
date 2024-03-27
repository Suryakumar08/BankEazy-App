package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import daos.AccountDaoInterface;
import exception.CustomBankException;
import model.Account;
import model.Branch;
import utilities.Validators;

public class AccountHelper {

	private AccountDaoInterface accountDao = null;

	public AccountHelper() throws CustomBankException {
		Class<?> AccountDAO;
		Constructor<?> accDao;

		try {
			AccountDAO = Class.forName("daos.AccountDAO");
			accDao = AccountDAO.getDeclaredConstructor();
			accountDao = (AccountDaoInterface) accDao.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	//create
	public long addAccount(Account account) throws CustomBankException {
		Validators.checkNull(account);
		return accountDao.addAccount(account);
	}

	//read
	public Map<Long, Account> getAccounts(int customerId) throws CustomBankException {
		Account dummyAccount = new Account();
		dummyAccount.setCustomerId(customerId);
		Map<Long, Account> accountMap = accountDao.getAccounts(dummyAccount,10,0);
		return accountMap;
	}
	
	public Map<Long, Account> getAccounts(Account account) throws CustomBankException {
		Validators.checkNull(account);
		Map<Long, Account> accountMap = accountDao.getAccounts(account,10,0);
		return accountMap;
	}
	

	public Account getAccount(long accountNo) throws CustomBankException {
		Account dummyAccount = new Account();
		dummyAccount.setAccountNo(accountNo);
		Map<Long, Account> accountMap = accountDao.getAccounts(dummyAccount, 1, 0);
		Validators.checkNull(accountMap);
		return accountMap.get(accountNo);
	}

	
	public Map<Integer, Map<Long,Account>> getCustomersAccounts() throws CustomBankException{
		Map<Integer, Map<Long, Account>> customersAccounts = new HashMap<>();
		for(Map.Entry<Long, Account> element : getAccounts(new Account()).entrySet()) {
			Account currAccount = element.getValue();
			int currCustomerId = currAccount.getCustomerId();
			Map<Long, Account> custAccounts = customersAccounts.get(currCustomerId);
			if(custAccounts == null) {
				custAccounts = new HashMap<>();
				customersAccounts.put(currCustomerId, custAccounts);
			}
			custAccounts.put(currAccount.getAccountNo(), currAccount);
		}
		return customersAccounts;
	}
	
	public Map<Long, JSONObject> getCustomerAccountsWithBranch(int customerId) throws CustomBankException{
		Validators.checkRangeBound(customerId, 1, Integer.MAX_VALUE, "Invalid Customer Id!");
		Map<Long, JSONObject> accountBranchMap = new HashMap<>();
		Account dummyAccount = new Account();
		dummyAccount.setCustomerId(customerId);
		Map<Long, Account> accountMap = getAccounts(dummyAccount);
		BranchHelper branchHelper = new BranchHelper();
		Map<Integer, Branch> branchMap = branchHelper.getAllBranches();
		for(Map.Entry<Long, Account> element : accountMap.entrySet()) {
			Account currAccount = element.getValue();
			Branch currAccountBranch = branchMap.get(currAccount.getBranchId());
			JSONObject accountBranchObj = new JSONObject();
			accountBranchObj.put("account", currAccount);
			accountBranchObj.put("branch", currAccountBranch);
			accountBranchMap.put(currAccount.getAccountNo(), accountBranchObj);
		}
		return accountBranchMap;
	}
	
	public Map<Long, JSONObject> getAccountsWithBranch(Account account) throws CustomBankException{
		Validators.checkNull(account);
		Map<Long, JSONObject> accountBranchMap = new HashMap<>();
		Map<Long, Account> accountMap = getAccounts(account);
		BranchHelper branchHelper = new BranchHelper();
		Map<Integer, Branch> branchMap = branchHelper.getAllBranches();
		for(Map.Entry<Long, Account> element : accountMap.entrySet()) {
			Account currAccount = element.getValue();
			Branch currAccountBranch = branchMap.get(currAccount.getBranchId());
			JSONObject accountBranchObj = new JSONObject();
			accountBranchObj.put("account", currAccount);
			accountBranchObj.put("branch", currAccountBranch);
			accountBranchMap.put(currAccount.getAccountNo(), accountBranchObj);
		}
		return accountBranchMap;
	}
	
	//update
	public boolean updateAmount(long accountNo, double amount) throws CustomBankException {
		Account account = new Account();
		account.setAccountNo(accountNo);
		account.setBalance(amount);
		return accountDao.updateAccount(account);
	}
	
	public boolean updateAccount(Account account) throws CustomBankException{
		Validators.checkNull(account);
		return accountDao.updateAccount(account);
	}

	
	
	//checking
	public boolean isActiveBankAccount(Account account) throws CustomBankException{
		if(account.getStatus() == 0) {
			throw new CustomBankException(CustomBankException.ACCOUNT_INACTIVE);
		}
		return true;
	}
	
	
	
}
