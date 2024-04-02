package com.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import enums.TransactionType;
import enums.UserStatus;
import enums.UserType;
import exception.CustomBankException;
import helpers.AccountHelper;
import helpers.BranchHelper;
import helpers.CustomerHelper;
import helpers.EmployeeHelper;
import helpers.TransactionHelper;
import helpers.UserHelper;
import model.Branch;
import model.Customer;
import model.Employee;
import model.Transaction;
import model.User;
import utilities.Sha_256;
import utilities.Utilities;
import utilities.Validators;

public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		System.out.println("Form Main Controller : " + path);
		switch (path) {
		case "/pages/login": {
//			response.sendRedirect(request.getContextPath() + "/static/login.jsp");
			request.getRequestDispatcher("/static/login.jsp").forward(request, response);
			break;
		}
		case "/pages/home": {
			try {
				System.out.println("Hii from /pages/home in MainController");
				int userId = Integer.parseInt(request.getParameter("userId"));
				String password = request.getParameter("password");
				UserHelper userHelper = new UserHelper();
				User user = userHelper.getUser(userId, password);
				HttpSession session = request.getSession(true);
				password = user.getPassword();
				session.setAttribute("userId", userId);
				session.setAttribute("userName", user.getName());
				int type = user.getType();
				session.setAttribute("userType", type);
				if (type == UserType.Customer.getType()) {
					response.sendRedirect(request.getContextPath() + "/user/home");
				} else if (type == UserType.Employee.getType()) {
					EmployeeHelper employeeHelper = new EmployeeHelper();
					Employee employee = employeeHelper.getEmployee(userId);
					session.setAttribute("employeeBranchId", employee.getBranchId());
					response.sendRedirect(request.getContextPath() + "/employee/home");
				} else if (type == UserType.Admin.getType()) {
					response.sendRedirect(request.getContextPath() + "/admin/home");
				} else {
					response.sendError(404, "Page not found!");
				}
			} catch (CustomBankException ex) {
				ex.printStackTrace();
				request.setAttribute("warning", ex.getMessage());
				request.getRequestDispatcher("/pages/login").forward(request, response);
			}
			break;
		}
		case "/pages/user/home": {
			try {
				HttpSession session = request.getSession(false);
				if (session == null) {
					throw new CustomBankException("Please Login to continue!");
				}
				setCustomerAccounts(request, response);
				request.setAttribute("page_type", "myAccounts");
				request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
			} catch (CustomBankException ex) {
				request.setAttribute("warning", ex.getMessage());
				request.getRequestDispatcher("/login").forward(request, response);
			}
			break;
		}
		case "/pages/admin/home":
		case "/pages/employee/home": {
			try {
				HttpSession session = request.getSession(false);
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "addUser");
				BranchHelper branchHelper = new BranchHelper();
				Map<Integer, Branch> branchMap = branchHelper.getAllBranches();
				request.setAttribute("branchMap", branchMap);
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			} catch (CustomBankException ex) {
				ex.printStackTrace();
				request.setAttribute("warning", ex.getMessage());
				request.getRequestDispatcher("/pages/login").forward(request, response);
			}
			break;
		}
		case "/pages/user/about":
		case "/pages/admin/about":
		case "/pages/employee/about": {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/static/index.jsp");
			dispatcher.forward(request, response);
			break;
		}
		case "/pages/user/profile":
		case "/pages/employee/profile":
		case "/pages/admin/profile": {
			try {
				HttpSession session = request.getSession(false);
				Object userIdObject = session.getAttribute("userId");
				if (userIdObject != null) {
					int userId = (int) userIdObject;
					int userType = (int) session.getAttribute("userType");
					if (userType == UserType.Customer.getType()) {
						CustomerHelper customerHelper = new CustomerHelper();
						Customer customer = customerHelper.getCustomer(userId);
						request.setAttribute("user", customer);
					} else if (userType == UserType.Employee.getType() || userType == UserType.Admin.getType()) {
						EmployeeHelper employeeHelper = new EmployeeHelper();
						Employee employee = employeeHelper.getEmployee(userId);
						request.setAttribute("user", employee);
					}
					request.getRequestDispatcher("/WEB-INF/pages/profile.jsp").forward(request, response);
				} else {
					throw new CustomBankException("Invalid User!");
				}
			} catch (CustomBankException ex) {
				response.sendError(403, ex.getMessage());
			}
			break;
		}
		case "/pages/user/changePassword":
		case "/pages/employee/changePassword":
		case "/pages/admin/changePassword": {
			HttpSession session = request.getSession(false);
			if (request.getParameter("currPassword") == null) {
				request.getRequestDispatcher("/WEB-INF/pages/changePassword.jsp").forward(request, response);
			} else {
				try {
					UserHelper userHelper = new UserHelper();
					String currPassword = request.getParameter("currPassword");
					String existingPassword = userHelper.getPassword((int) session.getAttribute("userId"));
					if (!Sha_256.getHashedPassword(currPassword).equals(existingPassword)) {
						request.setAttribute("failure-info", "Incorrect password!");
						request.getRequestDispatcher("/WEB-INF/pages/changePassword.jsp").forward(request, response);
						break;
					}
					String newPassword = request.getParameter("newPassword");
					Validators.validatePassword(newPassword);
					String confirmPassword = request.getParameter("confirmPassword");
					if (!newPassword.equals(confirmPassword)) {
						request.setAttribute("failure-info", "Password mismatch occurred!");
						request.getRequestDispatcher("/WEB-INF/pages/changePassword.jsp").forward(request, response);
						break;
					}
					int userId = (int) session.getAttribute("userId");
					userHelper.changePassword(newPassword, userId);

					request.setAttribute("success-info", "password changed successfully!");
					request.getRequestDispatcher("/WEB-INF/pages/changePassword.jsp").forward(request, response);
				} catch (CustomBankException ex) {
					request.setAttribute("failure-info", ex.getMessage());
					request.getRequestDispatcher("/WEB-INF/pages/changePassword.jsp").forward(request, response);
					break;
				}
			}
			break;
		}
		case "/pages/user/withdraw": {
			HttpSession session = request.getSession(false);
			try {
				setCustomerAccounts(request, response);
				if (request.getAttribute("failure-info") != null) {
					request.setAttribute("failure-info", (String) request.getAttribute("failure-info"));
					request.setAttribute("page_type", "customerWithdraw");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getAttribute("success-info") != null) {
					request.setAttribute("success-info", (String) request.getAttribute("success-info"));
					request.setAttribute("page_type", "customerWithdraw");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getParameter("amount") == null) {
					request.setAttribute("page_type", "customerWithdraw");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else {
					request.setAttribute("page_type", "customerWithdraw");
					request.getRequestDispatcher("/pages/user/doWithdraw").forward(request, response);
				}
			} catch (CustomBankException ex) {
				request.setAttribute("failure-info", ex.getMessage());
				request.setAttribute("page_type", "customerWithdraw");
				request.getRequestDispatcher(request.getContextPath() + "/WEB-INF/pages/userHome.jsp");
			}
			break;
		}
		case "/pages/user/doWithdraw": {
			HttpSession session = request.getSession(false);
			String selectedAccountString = request.getParameter("selected-account");
			String amountString = request.getParameter("amount");
			String passwordString = request.getParameter("password");
			try {
				if (selectedAccountString == null || selectedAccountString.equals("null")) {
					throw new CustomBankException("Please select Account!");
				} else if (amountString == null) {
					throw new CustomBankException("Please enter amount!");
				} else if (passwordString == null) {
					throw new CustomBankException("Please enter password!");
				}
				new UserHelper().checkPassword((int) session.getAttribute("userId"), passwordString);
				long selectedAccount = Long.parseLong(selectedAccountString);
				double amount = Double.parseDouble(amountString);
				TransactionHelper transactionHelper = new TransactionHelper();
				transactionHelper.withdrawAmount(selectedAccount, amount);
				request.setAttribute("success-info", "Withdraw successful! Please collect your cash!");
				request.setAttribute("page_type", "customerWithdraw");
				request.getRequestDispatcher("/pages/user/withdraw").forward(request, response);
			} catch (CustomBankException ex) {
				System.out.println("in catch block of /pages/user/doWithdraw...");
				request.setAttribute("failure-info", ex.getMessage());
				request.getRequestDispatcher("/pages/user/withdraw").forward(request, response);
			}
			break;
		}
		case "/pages/user/deposit": {
			HttpSession session = request.getSession(false);
			try {
				setCustomerAccounts(request, response);
				if (request.getAttribute("failure-info") != null) {
					request.setAttribute("failure-info", (String) request.getAttribute("failure-info"));
					request.setAttribute("page_type", "customerDeposit");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getAttribute("success-info") != null) {
					request.setAttribute("success-info", (String) request.getAttribute("success-info"));
					request.setAttribute("page_type", "customerDeposit");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getParameter("amount") == null) {
					request.setAttribute("page_type", "customerDeposit");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else {
					request.setAttribute("page_type", "customerDeposit");
					request.getRequestDispatcher("/pages/user/doDeposit").forward(request, response);
				}
			} catch (CustomBankException ex) {
				request.setAttribute("failure-info", ex.getMessage());
				request.setAttribute("page_type", "customerDeposit");
				request.getRequestDispatcher(request.getContextPath() + "/WEB-INF/pages/userHome.jsp");
			}
			break;
		}
		case "/pages/user/doDeposit": {
			HttpSession session = request.getSession(false);
			String selectedAccountString = request.getParameter("selected-account");
			String amountString = request.getParameter("amount");
			String passwordString = request.getParameter("password");
			try {
				if (selectedAccountString == null || selectedAccountString.equals("null")) {
					throw new CustomBankException("Please select Account!");
				} else if (amountString == null) {
					throw new CustomBankException("Please enter amount!");
				} else if (passwordString == null) {
					throw new CustomBankException("Please enter password!");
				}
				new UserHelper().checkPassword((int) session.getAttribute("userId"), passwordString);
				long selectedAccount = Long.parseLong(selectedAccountString);
				double amount = Double.parseDouble(amountString);
				TransactionHelper transactionHelper = new TransactionHelper();
				transactionHelper.depositAmount(selectedAccount, amount);
				request.setAttribute("success-info", "Deposit successful!");
				request.setAttribute("page_type", "customerDeposit");
				request.getRequestDispatcher("/pages/user/deposit").forward(request, response);
			} catch (CustomBankException ex) {
				System.out.println("in catch block of /pages/user/doDeposit...");
				request.setAttribute("failure-info", ex.getMessage());
				request.getRequestDispatcher("/pages/user/deposit").forward(request, response);
			}
			break;
		}
		case "/pages/user/inter-bank-transfer": {
			HttpSession session = request.getSession(false);
			try {
				setCustomerAccounts(request, response);
				if (request.getAttribute("failure-info") != null) {
					request.setAttribute("failure-info", (String) request.getAttribute("failure-info"));
					request.setAttribute("page_type", "customerInterBankTransfer");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getAttribute("success-info") != null) {
					request.setAttribute("success-info", (String) request.getAttribute("success-info"));
					request.setAttribute("page_type", "customerInterBankTransfer");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getParameter("transactionAmount") == null) {
					request.setAttribute("page_type", "customerInterBankTransfer");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else {
					request.setAttribute("page_type", "customerInterBankTransfer");
					request.getRequestDispatcher("/pages/user/do-inter-bank-transfer").forward(request, response);
				}
			} catch (CustomBankException ex) {
				request.setAttribute("failure-info", ex.getMessage());
				request.setAttribute("page_type", "customerInterBankTransfer");
				request.getRequestDispatcher(request.getContextPath() + "/WEB-INF/pages/userHome.jsp");
			}

			break;
		}

		case "/pages/user/do-inter-bank-transfer": {
			HttpSession session = request.getSession(false);
			String selectedAccountString = request.getParameter("selected-account");
			String amountString = request.getParameter("transactionAmount");
			String passwordString = request.getParameter("password");
			String recipientAccountNo = request.getParameter("recipientAccountNo");
			String recipientIfsc = request.getParameter("recipientIfsc");
			String description = request.getParameter("description");
			try {
				if (selectedAccountString == null || selectedAccountString.equals("null")) {
					throw new CustomBankException("Please select Account!");
				} else if (amountString == null) {
					throw new CustomBankException("Please enter amount!");
				} else if (passwordString == null) {
					throw new CustomBankException("Please enter password!");
				} else if (recipientAccountNo == null) {
					throw new CustomBankException("Enter recipient Account Number!");
				} else if (recipientIfsc == null) {
					throw new CustomBankException("Enter recipient ifsc!");
				} else if (description == null) {
					description = "";
				}
				int userId = (int) session.getAttribute("userId");
				new UserHelper().checkPassword(userId, passwordString);
				long selectedAccount = Long.parseLong(selectedAccountString);
				long recipientAccount = Long.parseLong(recipientAccountNo);
				double amount = Double.parseDouble(amountString);
				TransactionHelper transactionHelper = new TransactionHelper();
				Transaction transaction = new Transaction();
				transaction.setAccountNo(selectedAccount);
				transaction.setCustomerId(userId);
				transaction.setTransactionAccountNo(recipientAccount);
				transaction.setDescription(description);
				transaction.setAmount(amount);
				transaction.setType(TransactionType.DEBIT.getType());

				long referenceNo = transactionHelper.makeBankTransaction(transaction, true);
				request.setAttribute("success-info", "Transaction Successful!");
				request.setAttribute("ReferenceNo", referenceNo);
				request.setAttribute("page_type", "customerInterBankTransfer");
				request.getRequestDispatcher("/pages/user/inter-bank-transfer").forward(request, response);
			} catch (CustomBankException ex) {
				System.out.println("in catch block of /pages/user/do-inter-bank-transfer...");
				request.setAttribute("failure-info", ex.getMessage());
				request.getRequestDispatcher("/pages/user/inter-bank-transfer").forward(request, response);
			}
			break;
		}

		case "/pages/user/intra-bank-transfer": {
			HttpSession session = request.getSession(false);

			try {
				setCustomerAccounts(request, response);
				if (request.getAttribute("failure-info") != null) {
					request.setAttribute("failure-info", (String) request.getAttribute("failure-info"));
					request.setAttribute("page_type", "customerIntraBankTransfer");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getAttribute("success-info") != null) {
					request.setAttribute("success-info", (String) request.getAttribute("success-info"));
					request.setAttribute("page_type", "customerIntraBankTransfer");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else if (request.getParameter("transactionAmount") == null) {
					request.setAttribute("page_type", "customerIntraBankTransfer");
					request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
				} else {
					request.setAttribute("page_type", "customerIntraBankTransfer");
					request.getRequestDispatcher("/pages/user/do-intra-bank-transfer").forward(request, response);
				}
			} catch (CustomBankException ex) {
				request.setAttribute("failure-info", ex.getMessage());
				request.setAttribute("page_type", "customerIntraBankTransfer");
				request.getRequestDispatcher(request.getContextPath() + "/WEB-INF/pages/userHome.jsp");
			}

			break;
		}

		case "/pages/user/do-intra-bank-transfer": {
			HttpSession session = request.getSession(false);
			String selectedAccountString = request.getParameter("selected-account");
			String amountString = request.getParameter("transactionAmount");
			String passwordString = request.getParameter("password");
			String recipientAccountNo = request.getParameter("recipientAccountNo");
			String description = request.getParameter("description");
			try {
				if (selectedAccountString == null || selectedAccountString.equals("null")) {
					throw new CustomBankException("Please select Account!");
				} else if (amountString == null) {
					throw new CustomBankException("Please enter amount!");
				} else if (passwordString == null) {
					throw new CustomBankException("Please enter password!");
				} else if (recipientAccountNo == null) {
					throw new CustomBankException("Enter recipient Account Number!");
				} else if (description == null) {
					description = "";
				}
				int userId = (int) session.getAttribute("userId");
				new UserHelper().checkPassword(userId, passwordString);
				long selectedAccount = Long.parseLong(selectedAccountString);
				long recipientAccount = Long.parseLong(recipientAccountNo);
				double amount = Double.parseDouble(amountString);
				TransactionHelper transactionHelper = new TransactionHelper();
				Transaction transaction = new Transaction();
				transaction.setAccountNo(selectedAccount);
				transaction.setCustomerId(userId);
				transaction.setTransactionAccountNo(recipientAccount);
				transaction.setDescription(description);
				transaction.setAmount(amount);
				transaction.setType(TransactionType.DEBIT.getType());

				long referenceNo = transactionHelper.makeBankTransaction(transaction, false);
				request.setAttribute("success-info", "Transaction Successful!");
				request.setAttribute("ReferenceNo", referenceNo);
				request.setAttribute("page_type", "customerIntraBankTransfer");
				request.getRequestDispatcher("/pages/user/intra-bank-transfer").forward(request, response);
			} catch (CustomBankException ex) {
				System.out.println("in catch block of /pages/user/do-intra-bank-transfer...");
				request.setAttribute("failure-info", ex.getMessage());
				request.getRequestDispatcher("/pages/user/intra-bank-transfer").forward(request, response);
			}
			break;
		}
		case "/pages/user/transactionHistory": {
			try {
				HttpSession session = request.getSession(false);
				setCustomerAccounts(request, response);
				String selectedAccountString = request.getParameter("selectedAccount");
				String fromDateString = request.getParameter("fromDate");
				String toDateString = request.getParameter("toDate");
				if (selectedAccountString == null && fromDateString == null && toDateString == null) {
					throw new CustomBankException("");
				}
				if (selectedAccountString == null) {
					throw new CustomBankException("Please select Account!");
				}
				long selectedAccount = Long.parseLong(selectedAccountString);
				if (fromDateString == null || toDateString == null) {
					throw new CustomBankException("Date not selected!");
				}
				long fromDate = Utilities.getDateInMillis(fromDateString);
				long toDate = Utilities.getDateInMillis(toDateString);
				String pageString = request.getParameter("page");
				int page = 1;
				if (pageString != null) {
					page = Integer.parseInt(pageString);
				}
				request.setAttribute("selected-account", selectedAccount);
				request.setAttribute("from-date", fromDateString);
				request.setAttribute("to-date", toDateString);
				request.setAttribute("selected-page", page);
				System.out.println("selected-account : " + selectedAccount + " // from-date : " + fromDateString
						+ " // to-date : " + toDateString + " // selested-page : " + page);
				TransactionHelper helper = new TransactionHelper();
				int totalNoOfTransactions = helper.getNoOfTransactions(selectedAccount, fromDate + (86400000l),
						toDate + (86400000l));
				if (totalNoOfTransactions == 0) {
					throw new CustomBankException("No transactions found");
				}
				System.out.println("Total no of transactions available : " + totalNoOfTransactions);
				int noOfRecordsPerPage = 11;
				int totalPages = (int) Math.ceil(totalNoOfTransactions / (double) noOfRecordsPerPage);
				System.out.println("In controller total pages : " + totalPages);
				request.setAttribute("totalPages", totalPages);
				List<Transaction> transactionsList = helper.getTransactionsList(selectedAccount, fromDate + (86400000l),
						toDate + (86400000l), noOfRecordsPerPage, (page - 1) * noOfRecordsPerPage);
				for (Transaction trans : transactionsList) {
					System.out.println(trans);
				}
				request.setAttribute("transactions", transactionsList);
				request.setAttribute("page_type", "customerTransactionHistory");
				request.removeAttribute("failure-info");
				request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
			} catch (CustomBankException ex) {
				ex.printStackTrace();
				request.setAttribute("failure-info", ex.getMessage());
				request.setAttribute("page_type", "customerTransactionHistory");
				request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
			}
			break;
		}

		case "/pages/admin/addCustomer":
		case "/pages/employee/addCustomer": {
			try {
				Customer customer = new Customer();
				int type = UserType.Customer.getType();
				String newUserName = request.getParameter("newUserName");
				String mobile = request.getParameter("newUserMobile");
				String newUserGender = request.getParameter("newUserGender");
				String newUserDob = request.getParameter("newUserDob");
				System.out.println(newUserDob);
				long newUserDobInMillis = Utilities.getDateInMillis(newUserDob);
				String newUserPan = request.getParameter("newUserPan");
				String newUserAadhar = request.getParameter("newUserAadhar");

				customer.setType(type);
				customer.setName(newUserName);
				customer.setMobile(mobile);
				customer.setStatus(UserStatus.ACTIVE.getStatus());
				customer.setPassword("Welcome@123");
				customer.setGender(newUserGender);
				customer.setDob(newUserDobInMillis);
				customer.setAadhar(newUserAadhar);
				customer.setPan(newUserPan);

				CustomerHelper customerHelper = new CustomerHelper();
				int newUserId = customerHelper.addCustomer(customer);
				request.setAttribute("success-message", "Customer Added Successfully! User Id : " + newUserId);
				if (path.startsWith("/pages/admin")) {
					request.getRequestDispatcher("/pages/admin/home").forward(request, response);
				} else {
					request.getRequestDispatcher("/pages/employee/home").forward(request, response);
				}
			} catch (CustomBankException ex) {
				request.setAttribute("failure-message", ex.getMessage());
				if (path.startsWith("/pages/admin")) {
					request.getRequestDispatcher("/pages/admin/home").forward(request, response);
				} else {
					request.getRequestDispatcher("/pages/employee/home").forward(request, response);
				}
			}
			break;
		}
		case "/pages/admin/addEmployee": {
			try {
				Employee employee = new Employee();
				int type = UserType.Employee.getType();
				String newUserName = request.getParameter("newUserName");
				String mobile = request.getParameter("newUserMobile");
				String newUserGender = request.getParameter("newUserGender");
				String newUserDob = request.getParameter("newUserDob");
				System.out.println(newUserDob);
				long newUserDobInMillis = Utilities.getDateInMillis(newUserDob);
				String newUserSalary = request.getParameter("newUserSalary");
				System.out.println("Employee salary : " + newUserSalary);
				int newUserSalaryInInt = Integer.parseInt(newUserSalary);
				String newUserBranch = request.getParameter("newUserBranch");
				System.out.println("Employee Branch : " + newUserBranch);
				int newUserBranchInInt = Integer.parseInt(newUserBranch);

				employee.setType(type);
				employee.setName(newUserName);
				employee.setMobile(mobile);
				employee.setStatus(UserStatus.ACTIVE.getStatus());
				employee.setPassword("Welcome@123");
				employee.setGender(newUserGender);
				employee.setDob(newUserDobInMillis);
				employee.setSalary(newUserSalaryInInt);
				employee.setBranchId(newUserBranchInInt);
				employee.setJoiningDate(Utilities.getCurrentTime());

				EmployeeHelper employeeHelper = new EmployeeHelper();
				int newUserId = employeeHelper.addEmployee(employee);
				request.setAttribute("success-message", "Employee Added Successfully! User Id : " + newUserId);
				request.getRequestDispatcher("/pages/admin/home").forward(request, response);
			} catch (CustomBankException ex) {
				request.removeAttribute("success-message");
				request.setAttribute("failure-message", ex.getMessage());
				request.getRequestDispatcher("/pages/admin/home").forward(request, response);
			}
			break;
		}
		case "/pages/admin/getUser": {
			try {
				String viewUserId = request.getParameter("viewUserId");
				if (viewUserId == null) {
					throw new CustomBankException("");
				}
				int currUserId = Integer.parseInt(viewUserId);
				UserHelper userHelper = new UserHelper();
				User user = userHelper.getUser(currUserId);
				System.out.println(user);
				if (user == null) {
					throw new CustomBankException("User not exists!");
				}
				if (user.getType() == UserType.Customer.getType()) {
					CustomerHelper customerHelper = new CustomerHelper();
					Customer currCustomer = customerHelper.getCustomer(currUserId);
					request.setAttribute("searched-user", currCustomer);
				} else {
					EmployeeHelper employeeHelper = new EmployeeHelper();
					Employee currEmployee = employeeHelper.getEmployee(currUserId);
					request.setAttribute("searched-user", currEmployee);
				}
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				BranchHelper branchHelper = new BranchHelper();
				Map<Integer, Branch> branchMap = branchHelper.getAllBranches();
				request.setAttribute("branchMap", branchMap);
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			} catch (CustomBankException ex) {
				request.setAttribute("failure-message", ex.getMessage());
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			}
			break;
		}
		case "/pages/employee/getUser": {
			try {
				String viewUserId = request.getParameter("viewUserId");
				if (viewUserId == null) {
					throw new CustomBankException("");
				}
				int currUserId = Integer.parseInt(viewUserId);
				UserHelper userHelper = new UserHelper();
				User user = userHelper.getUser(currUserId);
				System.out.println(user);
				if (user == null) {
					throw new CustomBankException("User not exists!!");
				}
				if (user.getType() == UserType.Customer.getType()) {
					CustomerHelper customerHelper = new CustomerHelper();
					Customer currCustomer = customerHelper.getCustomer(currUserId);
					request.setAttribute("searched-user", currCustomer);
				} else {
					throw new CustomBankException("Customer Not exists!");
				}
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				BranchHelper branchHelper = new BranchHelper();
				Map<Integer, Branch> branchMap = branchHelper.getAllBranches();
				request.setAttribute("branchMap", branchMap);
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			} catch (CustomBankException ex) {
				request.setAttribute("failure-message", ex.getMessage());
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			}
			break;
		}

		case "/pages/admin/updateUserDetails": {
			try {
				String currUserIdString = request.getParameter("userId");
				int currUserId = Integer.parseInt(currUserIdString);
				String currName = request.getParameter("name");
				String currPhone = request.getParameter("phone");
				String currGender = request.getParameter("gender");
				String currDob = request.getParameter("dob");
				String currStatus = request.getParameter("status");
				String currType = request.getParameter("type");

				if (currType.equals(UserType.Customer.toString())) {
					String currPan = request.getParameter("pan");
					String currAadhar = request.getParameter("aadhar");
					Customer currCustomer = new Customer();
					currCustomer.setId(currUserId);
					currCustomer.setUserId(currUserId);
					currCustomer.setName(currName);
					currCustomer.setMobile(currPhone);
					currCustomer.setGender(currGender);
					currCustomer.setDob((Utilities.getDateInMillis(currDob)));
					currCustomer.setStatus(UserStatus.valueOf(currStatus).getStatus());
					currCustomer.setType(UserType.valueOf(currType).getType());
					currCustomer.setAadhar(currAadhar);
					currCustomer.setPan(currPan);
					System.out.println("editing customer : " + currCustomer);
					CustomerHelper helper = new CustomerHelper();
					helper.updateCustomer(currCustomer, currUserId);
				} else {
					String currSalaryString = request.getParameter("salary");
					int currSalary = Integer.parseInt(currSalaryString);
					String joiningDateString = request.getParameter("joiningDate");
					Long joiningDate = Utilities.getDateInMillis(joiningDateString);
					String currBranchIdString = request.getParameter("branch");
					int currBranchId = Integer.parseInt(currBranchIdString);
					Employee currEmployee = new Employee();
					currEmployee.setId(currUserId);
					currEmployee.setUserId(currUserId);
					currEmployee.setName(currName);
					currEmployee.setMobile(currPhone);
					currEmployee.setGender(currGender);
					currEmployee.setDob((Utilities.getDateInMillis(currDob)));
					currEmployee.setStatus(UserStatus.valueOf(currStatus).getStatus());
					currEmployee.setType(UserType.valueOf(currType).getType());
					currEmployee.setSalary(currSalary);
					currEmployee.setJoiningDate(joiningDate);
					currEmployee.setBranchId(currBranchId);
					System.out.println("editing customer : " + currEmployee);
					EmployeeHelper helper = new EmployeeHelper();
					helper.updateEmployee(currEmployee, currUserId);
				}
				request.setAttribute("success-message", "User updation successful!");
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				request.removeAttribute("selected-user");
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			} catch (CustomBankException ex) {
				request.setAttribute("edit-result", ex.getMessage());
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			}
			break;
		}
		case "/pages/employee/updateUserDetails": {
			try {
				String currUserIdString = request.getParameter("userId");
				int currUserId = Integer.parseInt(currUserIdString);
				String currName = request.getParameter("name");
				String currPhone = request.getParameter("phone");
				String currGender = request.getParameter("gender");
				String currDob = request.getParameter("dob");
				String currStatus = request.getParameter("status");
				String currType = request.getParameter("type");
				String currPan = request.getParameter("pan");
				String currAadhar = request.getParameter("aadhar");
				Customer currCustomer = new Customer();
				currCustomer.setId(currUserId);
				currCustomer.setUserId(currUserId);
				currCustomer.setName(currName);
				currCustomer.setMobile(currPhone);
				currCustomer.setGender(currGender);
				currCustomer.setDob((Utilities.getDateInMillis(currDob)));
				currCustomer.setStatus(UserStatus.valueOf(currStatus).getStatus());
				currCustomer.setType(UserType.valueOf(currType).getType());
				currCustomer.setAadhar(currAadhar);
				currCustomer.setPan(currPan);
				System.out.println("editing customer : " + currCustomer);
				CustomerHelper helper = new CustomerHelper();
				helper.updateCustomer(currCustomer, currUserId);
				request.setAttribute("success-message", "User updation successful!");
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				request.removeAttribute("selected-user");
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			} catch (CustomBankException ex) {
				request.setAttribute("edit-result", ex.getMessage());
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			}
			break;
		}

		case "/pages/user/logout":
		case "/pages/employee/logout":
		case "/pages/admin/logout": {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			response.sendRedirect("/BankEazy-App");
			break;
		}
		default: {
			response.sendError(404, "Requested page " + request.getRequestURI() + " not found!!!!!!");
			break;
		}
		}

	}

	private void setCustomerAccounts(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, CustomBankException {
		HttpSession session = request.getSession(false);
		AccountHelper accountHelper = new AccountHelper();
		Object userIdObject = session.getAttribute("userId");
		if (userIdObject == null) {
			throw new CustomBankException("Please Login to continue!");
		}
		int userId = (int) userIdObject;
		Map<Long, JSONObject> customerAccounts = accountHelper.getCustomerAccountsWithBranch(userId);
		request.setAttribute("customerAccounts", customerAccounts);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
