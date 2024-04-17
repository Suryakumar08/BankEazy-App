package com.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import enums.UserStatus;
import enums.UserType;
import exception.CustomBankException;
import helpers.CustomerHelper;
import helpers.EmployeeHelper;
import model.Customer;
import model.Employee;
import utilities.Utilities;

public class ApiController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ApiController() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String path = request.getPathInfo();
		JSONObject responseObject = null;
		String blocks[] = path.split("/");
		System.out.println("Api Controller Get method Path blocks : ");
		for (String block : blocks) {
			System.out.println("Block Value : " + block);
		}
		System.out.println();
		response.setContentType("application/json");
		try {
			if (blocks.length == 1) {
				System.out.println("blocks length is 0!");
				response.getWriter().print("Invalid url!");
			}
			//customer
			else if (blocks[1].equals("customers")) {
				if (1 < blocks.length) {
					int customerId = Integer.parseInt(blocks[2]);
					JSONObject customerObj = getCustomerJsonObject(customerId);
					
					response.getWriter().print(customerObj);
				} else {
					throw new Exception("Invalid url!");
				}
			}
			//employee
			else if (blocks[1].equals("employees")) {
				if (1 < blocks.length) {
					int employeeId = Integer.parseInt(blocks[2]);
					JSONObject employeeObj = getEmployeeJsonObject(employeeId);
					if(employeeObj != null && employeeObj.get("Type").equals(UserType.Employee.toString())) {
						response.getWriter().print(employeeObj);						
					}
					else {
						JSONObject empJson = null;
						response.getWriter().print(empJson);
					}
				} else {
					throw new Exception("Invalid url!");
				}
			}
			//admin
			else if(blocks[1].equals("admins")){
				if (1 < blocks.length) {
					int employeeId = Integer.parseInt(blocks[2]);
					JSONObject employeeObj = getEmployeeJsonObject(employeeId);
					if(employeeObj != null && employeeObj.get("Type").equals(UserType.Admin.toString())) {
						response.getWriter().print(employeeObj);						
					}
					else {
						JSONObject adminJson = null;
						response.getWriter().print(adminJson);
					}
				} else {
					throw new Exception("Invalid url!");
				}
			}
			else {
				throw new Exception("Invalid url!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			response.getWriter().print("Invalid URL!");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		String blocks[] = path.split("/");
		System.out.println("Api Controller Post method path blocks : ");
		List.of(blocks).stream().forEach(x -> System.out.println("Block Value : " + x));
		System.out.println();
		
		response.setContentType("application/json");
		if(blocks[1].equals("customer")) {
			Customer customer = new Customer();
			
			StringBuilder requestParams = new StringBuilder();
			try(BufferedReader reader = request.getReader()){
				String line;
				while((line = reader.readLine()) != null) {
					requestParams.append(line);
				}
			}
			
			JSONObject customerParams = new JSONObject(requestParams.toString());
			
			int type = UserType.Customer.getType();
			String newUserName = customerParams.getString("userName");
			String mobile = customerParams.getString("mobile");
			String newUserGender = customerParams.getString("gender");
			String newUserDob = customerParams.getString("dob");
			long newUserDobInMillis = Utilities.getDateInMillis(newUserDob);
			String newUserPan = customerParams.getString("pan");
			String newUserAadhar = customerParams.getString("aadhar");

			customer.setType(type);
			customer.setName(newUserName);
			customer.setMobile(mobile);
			customer.setStatus(UserStatus.ACTIVE.getStatus());
			customer.setPassword("Welcome@123");
			customer.setGender(newUserGender);
			customer.setDob(newUserDobInMillis);
			customer.setAadhar(newUserAadhar);
			customer.setPan(newUserPan);
			
			customer.setLastModifiedBy(-1);
			customer.setLastModifiedOn(System.currentTimeMillis());
			try {
				CustomerHelper customerHelper = new CustomerHelper();
				int newUserId = customerHelper.addCustomer(customer);
				response.getWriter().print("Customer created successfully! User Id : " + newUserId);
			}catch (CustomBankException e) {
				response.getWriter().print(e.getMessage());
			}
		}
	}

	
	
	
	
	
	
	
	protected JSONObject getCustomerJsonObject(int customerId) {
		JSONObject customerObj = new JSONObject();
		Customer currCustomer = null;
		try {
			CustomerHelper helper = new CustomerHelper();
			currCustomer = helper.getCustomer(customerId);						
		}
		catch(CustomBankException ex) {
			return null;					
		}
		if (currCustomer != null) {
			customerObj.put("ID", currCustomer.getId());
			customerObj.put("Name", currCustomer.getName());
			customerObj.put("Mobile", currCustomer.getMobile());
			customerObj.put("Gender", currCustomer.getGender());
			customerObj.put("Date of Birth", Utilities.getDateString(currCustomer.getDob()));
			customerObj.put("Type", currCustomer.getTypeAsString());
			customerObj.put("Status", currCustomer.getStatusAsString());
			customerObj.put("Pan", currCustomer.getPan());
			customerObj.put("Aadhar", currCustomer.getAadhar());
		}
		return customerObj;
	}
	
	protected JSONObject getEmployeeJsonObject(int employeeId) {
		JSONObject employeeObj = new JSONObject();
		Employee currEmployee = null;
		try {
			EmployeeHelper helper = new EmployeeHelper();
			currEmployee = helper.getEmployee(employeeId);						
		}
		catch(CustomBankException ex) {
			return null;
		}
		if (currEmployee != null) {
			employeeObj.put("ID", currEmployee.getId());
			employeeObj.put("Name", currEmployee.getName());
			employeeObj.put("Mobile", currEmployee.getMobile());
			employeeObj.put("Gender", currEmployee.getGender());
			employeeObj.put("Date of Birth", Utilities.getDateString(currEmployee.getDob()));
			employeeObj.put("Type", currEmployee.getTypeAsString());
			employeeObj.put("Status", currEmployee.getStatusAsString());
			employeeObj.put("Salary", currEmployee.getSalary());
			employeeObj.put("Joining date", Utilities.getDateString(currEmployee.getJoiningDate()));
			employeeObj.put("Branch Id", currEmployee.getBranchId());
		}
		return employeeObj;
	}
	
}
