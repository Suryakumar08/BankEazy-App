package com.proxies;

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
	
	
	private static CustomerDaoInterface customerProxy = null;
	private static AccountDaoInterface accountProxy = null;


	public static AccountDaoInterface getAccountProxy(AccountDaoInterface accountRetriever) {
		if(accountProxy != null) {
			return accountProxy;
		}
		return accountProxy = (AccountDaoInterface) Proxy.newProxyInstance(AccountProxyHandler.class.getClassLoader(),
				new Class[] { AccountDaoInterface.class },
				new AccountProxyHandler(accountRetriever));
	}

	private static class CustomerProxyHandler<K, V> implements InvocationHandler {
		private static ICache<Integer, Customer> customerCache;
		private static CustomerDaoInterface customerDataRetriever;

		public CustomerProxyHandler(CustomerDaoInterface customerDataRetriever) {
			
			CustomerProxyHandler.customerDataRetriever = customerDataRetriever;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Integer customerId = (Integer) args[0];
			System.out.println("\nProxy name in customer proxy ::: " + proxy.getClass().getName()
					+ "\nMethod name in customer proxy ::: " + method.getName() + "\n");
			Customer myCustomer;
			try {
				if ((myCustomer = (Customer) customerCache.get(customerId)) != null) {
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
				customerCache.set(customerId, myCustomer);
			} catch (CustomBankException ex) {
			}
			return myCustomer;
		}
	}

	public static <K, V> CustomerDaoInterface getCustomerProxy(ICache<K, V> customerCache,
			CustomerDaoInterface customerDataRetriever) {
		if(customerProxy != null) {
			return customerProxy;
		}
		return customerProxy = (CustomerDaoInterface) Proxy.newProxyInstance(CustomerProxyHandler.class.getClassLoader(),
				new Class[] { CustomerDaoInterface.class },
				new CustomerProxyHandler<K, V>(customerDataRetriever));
	}

}
