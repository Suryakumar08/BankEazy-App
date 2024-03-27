package daos;

import java.util.Map;

import exception.CustomBankException;
import model.Customer;

public interface CustomerDaoInterface {

	//create
	int addCustomer(Customer customer) throws CustomBankException;
	
	//read
	Map<Integer, Customer> getCustomers(Customer customer, int limit, long offset) throws CustomBankException;

	//update
	boolean updateCustomer(Customer customer, int customerId) throws CustomBankException;
	
}
