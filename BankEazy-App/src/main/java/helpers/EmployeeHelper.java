package helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import daos.EmployeeDaoInterface;
import enums.UserType;
import exception.CustomBankException;
import model.Employee;
import utilities.Sha_256;
import utilities.Validators;

public class EmployeeHelper {

	private EmployeeDaoInterface employeeDao = null;
	

	public EmployeeHelper() throws CustomBankException {
		Class<?> EmployeeDAO;
		Constructor<?> empDao;
		try {
			EmployeeDAO = Class.forName("daos.EmployeeDAO");
			empDao = EmployeeDAO.getDeclaredConstructor();
			employeeDao = (EmployeeDaoInterface) empDao.newInstance();
			
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED, e);
		}
	}
	
	//create
	public int addEmployee(Employee employee) throws CustomBankException {
		Validators.checkNull(employee);
		String password = employee.getPassword();
		employee.setPassword(Sha_256.getHashedPassword(password));
		employee.setStatus(1);
		employee.setTypeFromEnum(UserType.Employee);
		return employeeDao.addEmployee(employee);
	}
	
	//read
	public Employee getEmployee(int userId) throws CustomBankException {
		Employee dummyEmployee = new Employee();
		dummyEmployee.setId(userId);
		Map<Integer, Employee> employeeMap = employeeDao.getEmployees(dummyEmployee, 1, 0);
		Validators.checkNull(employeeMap);
		return employeeMap.get(userId);
	}

	public Map<Integer, Employee> getEmployees(Employee employee, int limit, long offset) throws CustomBankException{
		Validators.checkNull(employee);
		Map<Integer, Employee> employeeMap = employeeDao.getEmployees(employee, limit, offset);
		return employeeMap;
	}
	
	//update
	public boolean updateEmployee(Employee employee, long employeeId) throws CustomBankException{
		Validators.checkNull(employee);
		return employeeDao.updateEmployee(employee, employeeId);
	}
	
}
