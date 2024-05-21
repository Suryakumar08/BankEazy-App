package com.proxies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.cache.ICache;
import com.dynamicManager.DynamicManager;

import daos.AccountDaoInterface;
import exception.CustomBankException;
import model.Account;
import utilities.Validators;

class AccountProxyHandler implements InvocationHandler {
	private static ICache<Long, Account> accountCache;
	private static AccountDaoInterface dbAccountRetriver;
	
	private static Class<?> accountCacheClass;
	private static Constructor<?> accountCacheConstructor;

	@SuppressWarnings("unchecked")
	public AccountProxyHandler(AccountDaoInterface dbAccountRetriever) {
		AccountProxyHandler.dbAccountRetriver = dbAccountRetriever;

		if (accountCache == null) {
			try {
				AccountProxyHandler.accountCacheClass = Class.forName(DynamicManager.getAccountCachePath());
				AccountProxyHandler.accountCacheConstructor = AccountProxyHandler.accountCacheClass.getDeclaredConstructor(int.class);
				accountCache = (ICache<Long, Account>) AccountProxyHandler.accountCacheConstructor.newInstance(6379);
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(method.getName().equals("getAccounts")) {
			Long accountNo = ((Account)args[0]).getAccountNo();
			Account resultAccount;
			try {
				if ((resultAccount = (Account) accountCache.get(accountNo)) != null) {
					return resultAccount;
				}
			} catch (CustomBankException ex) {
			}
			
			Account dummyAccount = new Account();
			dummyAccount.setAccountNo(accountNo);
			Map<Long, Account> accountMap = dbAccountRetriver.getAccounts(dummyAccount, 1, 0);
			try {
				Validators.checkNull(accountMap);
			} catch (CustomBankException ex) {
				throw new CustomBankException("Account not found!");
			}
			resultAccount = accountMap.get(accountNo);
			try {
				accountCache.set(accountNo, resultAccount);
			} catch (CustomBankException ex) {
			}
			return resultAccount;
		}
		return method.invoke(dbAccountRetriver, args);
	}

}