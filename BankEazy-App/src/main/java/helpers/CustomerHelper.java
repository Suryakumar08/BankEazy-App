package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import daos.CustomerDaoInterface;
import enums.UserType;
import exception.CustomBankException;
import model.Customer;
import utilities.Sha_256;
import utilities.Validators;

public class CustomerHelper {

	private CustomerDaoInterface customerDao;

	public CustomerHelper() throws CustomBankException {
		Class<?> CustomerDAO;
		Constructor<?> custDao;

		try {
			CustomerDAO = Class.forName("daos.CustomerDAO");
			custDao = CustomerDAO.getDeclaredConstructor();
			customerDao = (CustomerDaoInterface) custDao.newInstance();

		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}

	//create
	public int addCustomer(Customer customer) throws CustomBankException {
		Validators.checkNull(customer);
		String password = customer.getPassword();
		customer.setPassword(Sha_256.getHashedPassword(password));
		customer.setStatus(1);
		customer.setTypeFromEnum(UserType.Customer);
		return customerDao.addCustomer(customer);
	}
	
	//read
	public Customer getCustomer(int customerId) throws CustomBankException{
		Customer dummyCustomer = new Customer();
		dummyCustomer.setId(customerId);
		Map<Integer, Customer> customerMap =  customerDao.getCustomers(dummyCustomer, 1, 0);
		return customerMap.get(customerId);
	}
	
	public Map<Integer, Customer> getCustomers(Customer customer, int limit, long offset) throws CustomBankException{
		Validators.checkNull(customer);
		return customerDao.getCustomers(customer, limit, offset);
	}
	
	//update
	public boolean updateCustomer(Customer customer, int customerId) throws CustomBankException{
		Validators.checkNull(customer);
		return customerDao.updateCustomer(customer, customerId);
	}
	
}
