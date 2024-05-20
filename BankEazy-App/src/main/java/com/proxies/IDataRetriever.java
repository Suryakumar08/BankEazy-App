package com.proxies;

import exception.CustomBankException;
import model.Account;
import model.Customer;

public interface IDataRetriever {
	public Account getAccount(Long accountNo) throws CustomBankException;
	
	public Customer getCustomer(int userId) throws CustomBankException;
}
