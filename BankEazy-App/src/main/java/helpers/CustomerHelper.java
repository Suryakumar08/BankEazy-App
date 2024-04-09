package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.cache.CustomerLRUCache;

import daos.CustomerDaoInterface;
import enums.UserStatus;
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
		String aadhar = customer.getAadhar();
		String pan = customer.getPan();
		if(getCustomerFromAadhar(aadhar) != null || getCustomerFromPan(pan) != null) {
			System.out.println("Customer already exists!");
			throw new CustomBankException("Customer already exists!!!");
		}
		String password = customer.getPassword();
		customer.setPassword(Sha_256.getHashedPassword(password));
		customer.setStatus(1);
		customer.setTypeFromEnum(UserType.Customer);
		return customerDao.addCustomer(customer);
	}
	
	//read
	public Customer getCustomer(int customerId) throws CustomBankException{
		CustomerLRUCache customerCache = new CustomerLRUCache();
		Customer myCustomer;
		if((myCustomer = (Customer)customerCache.get(customerId)) != null) {
			return myCustomer;
		}
		Customer dummyCustomer = new Customer();
		dummyCustomer.setId(customerId);
		Map<Integer, Customer> customerMap =  customerDao.getCustomers(dummyCustomer, 1, 0);
		Validators.checkNull(customerMap, "Customer Not found!");
		myCustomer = customerMap.get(customerId);
		customerCache.set(customerId,myCustomer);
		return myCustomer;
	}
	
	public Customer getCustomerFromAadhar(String aadhar) throws CustomBankException{
		Validators.checkNull(aadhar, "Aadhar number should not be empty or null!");
		Customer dummyCustomer = new Customer();
		dummyCustomer.setAadhar(aadhar);
		Map<Integer, Customer> customerMap = customerDao.getCustomers(dummyCustomer, 1, 0);
		Customer returnCustomer = null;
		if(customerMap == null) {
			return returnCustomer;
		}
		else {
			for(Customer customer : customerMap.values()) {
				returnCustomer = customer;
				break;
			}
		}
		return returnCustomer;
	}
	
	public Customer getCustomerFromPan(String pan) throws CustomBankException{
		Validators.checkNull(pan, "pan number should not be empty or null!");
		Customer dummyCustomer = new Customer();
		dummyCustomer.setPan(pan);
		Map<Integer, Customer> customerMap = customerDao.getCustomers(dummyCustomer, 1, 0);
		Customer returnCustomer = null;
		if(customerMap == null) {
			return returnCustomer;
		}
		else {
			for(Customer customer : customerMap.values()) {
				returnCustomer = customer;
				break;
			}
		}
		return returnCustomer;
	}
	
	public Map<Integer, Customer> getCustomers(Customer customer, int limit, long offset) throws CustomBankException{
		Validators.checkNull(customer);
		return customerDao.getCustomers(customer, limit, offset);
	}
	
	//update
	public boolean updateCustomer(Customer customer, int customerId) throws CustomBankException{
		Validators.checkNull(customer);
		CustomerLRUCache customerCache = new CustomerLRUCache();
		customerCache.remove(customerId);
		return customerDao.updateCustomer(customer, customerId);
	}

	public void inActivateCustomer(int customerId) throws CustomBankException{
		Customer customer = getCustomer(customerId);
		if(customer.getStatus() == UserStatus.ACTIVE.getStatus()) {
			customer.setStatus(UserStatus.INACTIVE.getStatus());
			updateCustomer(customer, customerId);
		}
	}

	public void activateIfInactive(int customerId) throws CustomBankException{
		Customer customer = getCustomer(customerId);
		if(customer.getStatus() == UserStatus.INACTIVE.getStatus()) {
			customer.setStatus(UserStatus.ACTIVE.getStatus());
			updateCustomer(customer, customerId);
		}
	}
	
}
