package daos;

import java.util.Map;

import exception.CustomBankException;
import model.Employee;

public interface EmployeeDaoInterface {

	//create
	int addEmployee(Employee employee) throws CustomBankException;
	
	//read
	Map<Integer, Employee> getEmployees(Employee employee,int limit, long offset) throws CustomBankException;
	
	Map<Integer, Employee> getEmployeesJoinedBetween(long from, long to, int limit, long offset) throws CustomBankException;
	
	//update
	boolean updateEmployee(Employee employee, long employeeId) throws CustomBankException;

}
