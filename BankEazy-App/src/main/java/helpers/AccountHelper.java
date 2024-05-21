package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.cache.ICache;
import com.dynamicManager.DynamicManager;

import daos.AccountDaoInterface;
import enums.AccountStatus;
import exception.CustomBankException;
import model.Account;
import model.Branch;
import utilities.Validators;

public class AccountHelper {

	private static AccountDaoInterface accountDao = null;
	private static ICache<Long, Account> accountCache = null;

	@SuppressWarnings("unchecked")
	public AccountHelper() throws CustomBankException {
		Class<?> AccountDAO;
		Constructor<?> accDao;

		Class<?> AccountCacheClass;
		Constructor<?> accountCacheConstructor;
		try {
			if (accountDao == null) {
				AccountDAO = Class.forName("daos.AccountDAO");
				accDao = AccountDAO.getDeclaredConstructor();
				accountDao = (AccountDaoInterface) accDao.newInstance();
			}

			if (accountCache == null) {
				AccountCacheClass = Class.forName(DynamicManager.getAccountCachePath());
				accountCacheConstructor = AccountCacheClass.getDeclaredConstructor(int.class);
				accountCache = (ICache<Long, Account>) accountCacheConstructor.newInstance(6379);
			}


		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	// create
	public long addAccount(Account account) throws CustomBankException {
		Validators.checkNull(account);
		return accountDao.addAccount(account);
	}

	// read
	public Map<Long, Account> getAccounts(int customerId) throws CustomBankException {
		Account dummyAccount = new Account();
		dummyAccount.setCustomerId(customerId);
		Map<Long, Account> accountMap = accountDao.getAccounts(dummyAccount, 10, 0);
		return accountMap;
	}

	public Map<Long, Account> getAccounts(Account account) throws CustomBankException {
		Validators.checkNull(account);
		Map<Long, Account> accountMap = accountDao.getAccounts(account, 20, 0);
		return accountMap;
	}

	public Account getAccount(long accountNo) throws CustomBankException {

		Account resultAccount;
		if ((resultAccount = accountCache.get(accountNo)) != null) {
			return resultAccount;
		}
		Account dummyAccount = new Account();
		dummyAccount.setAccountNo(accountNo);
		Map<Long, Account> accountMap = accountDao.getAccounts(dummyAccount, 1, 0);
		try {
			Validators.checkNull(accountMap);
		} catch (CustomBankException ex) {
			throw new CustomBankException("Account not found!");
		}
		resultAccount = accountMap.get(accountNo);
		accountCache.set(accountNo, resultAccount);
		return resultAccount;
	}

	public Map<Integer, Map<Long, Account>> getCustomersAccounts() throws CustomBankException {
		Map<Integer, Map<Long, Account>> customersAccounts = new HashMap<>();
		for (Map.Entry<Long, Account> element : getAccounts(new Account()).entrySet()) {
			Account currAccount = element.getValue();
			int currCustomerId = currAccount.getCustomerId();
			Map<Long, Account> custAccounts = customersAccounts.get(currCustomerId);
			if (custAccounts == null) {
				custAccounts = new HashMap<>();
				customersAccounts.put(currCustomerId, custAccounts);
			}
			custAccounts.put(currAccount.getAccountNo(), currAccount);
		}
		return customersAccounts;
	}

	public Map<Long, JSONObject> getCustomerAccountsWithBranch(int customerId) throws CustomBankException {
		Validators.checkRangeBound(customerId, 1, Integer.MAX_VALUE, "Invalid Customer Id!");
		Map<Long, JSONObject> accountBranchMap = new HashMap<>();
		Account dummyAccount = new Account();
		dummyAccount.setCustomerId(customerId);
		Map<Long, Account> accountMap = getAccounts(dummyAccount);
		if (accountMap == null) {
			return null;
		}
		BranchHelper branchHelper = new BranchHelper();
		Map<Integer, Branch> branchMap = branchHelper.getAllBranches();
		for (Map.Entry<Long, Account> element : accountMap.entrySet()) {
			Account currAccount = element.getValue();
			Branch currAccountBranch = branchMap.get(currAccount.getBranchId());
			JSONObject accountBranchObj = new JSONObject();
			accountBranchObj.put("account", currAccount);
			accountBranchObj.put("branch", currAccountBranch);
			accountBranchMap.put(currAccount.getAccountNo(), accountBranchObj);
		}
		return accountBranchMap;
	}

	public Map<Long, JSONObject> getAccountsWithBranch(Account account) throws CustomBankException {
		Validators.checkNull(account);
		Map<Long, JSONObject> accountBranchMap = new HashMap<>();
		Map<Long, Account> accountMap = getAccounts(account);
		BranchHelper branchHelper = new BranchHelper();
		Map<Integer, Branch> branchMap = branchHelper.getAllBranches();
		for (Map.Entry<Long, Account> element : accountMap.entrySet()) {
			Account currAccount = element.getValue();
			Branch currAccountBranch = branchMap.get(currAccount.getBranchId());
			JSONObject accountBranchObj = new JSONObject();
			accountBranchObj.put("account", currAccount);
			accountBranchObj.put("branch", currAccountBranch);
			accountBranchMap.put(currAccount.getAccountNo(), accountBranchObj);
		}
		return accountBranchMap;
	}

	// update
	public boolean updateAmount(long accountNo, double amount) throws CustomBankException {
		Account account = new Account();
		account.setAccountNo(accountNo);
		account.setBalance(amount);
		return updateAccount(account);
	}

	public void updateCacheBalance(long accountNo, double newBalance) throws CustomBankException {
		try {
			Account tempAccount = accountCache.get(accountNo);
			if (tempAccount != null) {
				tempAccount.setBalance(newBalance);
				accountCache.set(accountNo, tempAccount);
			}
		} catch (CustomBankException ex) {
		}
	}

	public boolean updateAccount(Account account) throws CustomBankException {
		Validators.checkNull(account);
		Validators.checkNull(account.getAccountNo());
		try {
			accountCache.remove(account.getAccountNo());
		} catch (CustomBankException ex) {
		}
		return accountDao.updateAccount(account);
	}

	public boolean inActivateAccount(long accountNo, long lastModifiedOn, int lastModifiedBy)
			throws CustomBankException {
		Account account = getAccount(accountNo);
		if (account == null) {
			throw new CustomBankException("Account not found!");
		}
		int customerId = account.getCustomerId();
		Account dummy = new Account();
		dummy.setCustomerId(customerId);
		Map<Long, Account> customerAccounts = getAccounts(dummy);
		boolean hasAnotherActiveAccount = false;
		for (Account currAccount : customerAccounts.values()) {
			if (currAccount.getAccountNo() != accountNo) {
				if (currAccount.getStatus() == AccountStatus.ACTIVE.getStatus()) {
					hasAnotherActiveAccount = true;
					break;
				}
			}
		}
		boolean isUpdated = false;
		if (account.getStatus() == AccountStatus.ACTIVE.getStatus()) {
			account.setStatus(AccountStatus.INACTIVE.getStatus());
			account.setLastModifiedBy(lastModifiedBy);
			account.setLastModifiedOn(lastModifiedOn);
			isUpdated = updateAccount(account);
			if (!hasAnotherActiveAccount) {
				CustomerHelper customerHelper = new CustomerHelper();
				customerHelper.inActivateCustomer(customerId);
			}
		}
		return isUpdated;
	}

	public boolean activateAccount(long accountNo, long lastModifiedOn, int lastModifiedBy) throws CustomBankException {
		Account account = getAccount(accountNo);
		if (account == null) {
			throw new CustomBankException("Account not found!");
		}
		if (account.getStatus() == AccountStatus.ACTIVE.getStatus()) {
			return true;
		}
		int customerId = account.getCustomerId();
		new CustomerHelper().activateIfInactive(customerId);
		account.setStatus(AccountStatus.ACTIVE.getStatus());
		account.setLastModifiedBy(lastModifiedBy);
		account.setLastModifiedOn(lastModifiedOn);
		return updateAccount(account);
	}

	// checking
	public boolean isActiveBankAccount(Account account) throws CustomBankException {
		if (account.getStatus() == AccountStatus.INACTIVE.getStatus()) {
			throw new CustomBankException(CustomBankException.ACCOUNT_INACTIVE);
		}
		return true;
	}

}
