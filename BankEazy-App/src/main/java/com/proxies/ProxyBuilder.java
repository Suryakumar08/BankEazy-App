package com.proxies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.cache.ICache;
import com.dynamicManager.DynamicManager;

import daos.AccountDaoInterface;
import daos.CustomerDaoInterface;
import exception.CustomBankException;
import model.Account;
import model.Customer;
import utilities.Validators;

public class ProxyBuilder {

	private static class AccountProxyHandler<K, V> implements InvocationHandler {
		private ICache<Long, Account> accountCache;
		private final AccountDaoInterface dbAccountRetriver;

		Class<?> AccountCacheClass;
		Constructor<?> accountCacheConstructor;

		@SuppressWarnings("unchecked")
		public AccountProxyHandler(AccountDaoInterface dbAccountRetriever) {
			this.dbAccountRetriver = dbAccountRetriever;

			if (accountCache == null) {
				try {
					AccountCacheClass = Class.forName(DynamicManager.getAccountCachePath());
					accountCacheConstructor = AccountCacheClass.getDeclaredConstructor(int.class);
					accountCache = (ICache<Long, Account>) accountCacheConstructor.newInstance(6379);
				} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}

		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			System.out.println("\nProxy name in Account proxy ::: " + proxy.getClass().getName()
					+ "\nMethod name in Account proxy ::: " + method.getName() + "\n");

			Long accountNo = (Long) args[0];
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

	}

	public static <K, V> IDataRetriever createAccountProxy(AccountDaoInterface dbDataRetriever) {
		return (IDataRetriever) Proxy.newProxyInstance(AccountProxyHandler.class.getClassLoader(),
				new Class[] { AccountDaoInterface.class },
				new AccountProxyHandler<K, V>(dbDataRetriever));
	}

	private static class CustomerProxyHandler<K, V> implements InvocationHandler {
		private final ICache<K, V> customerCache;
		private final CustomerDaoInterface customerDataRetriever;

		public CustomerProxyHandler(ICache<K, V> customerCache, CustomerDaoInterface customerDataRetriever) {
			this.customerCache = customerCache;
			this.customerDataRetriever = customerDataRetriever;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Integer customerId = (Integer) args[0];
			System.out.println("\nProxy name in customer proxy ::: " + proxy.getClass().getName()
					+ "\nMethod name in customer proxy ::: " + method.getName() + "\n");
			Customer myCustomer;
			try {
				if ((myCustomer = (Customer) customerCache.get((K) customerId)) != null) {
					return myCustomer;
				}
			} catch (CustomBankException ex) {
			}
			Customer dummyCustomer = new Customer();
			dummyCustomer.setId(customerId);
			Map<Integer, Customer> customerMap = customerDataRetriever.getCustomers(dummyCustomer, 1, 0);
			Validators.checkNull(customerMap, "Customer Not found!");
			myCustomer = customerMap.get(customerId);
			try {
				customerCache.set((K) customerId, (V) myCustomer);
			} catch (CustomBankException ex) {
			}
			return myCustomer;
		}
	}

	public static <K, V> IDataRetriever createCustomerProxy(ICache<K, V> customerCache,
			CustomerDaoInterface customerDataRetriever) {
		return (IDataRetriever) Proxy.newProxyInstance(CustomerProxyHandler.class.getClassLoader(),
				new Class[] { IDataRetriever.class },
				new CustomerProxyHandler<K, V>(customerCache, customerDataRetriever));
	}

}
