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
import enums.UserType;
import exception.CustomBankException;
import helpers.AccountHelper;
import helpers.CustomerHelper;
import helpers.TransactionHelper;
import helpers.UserHelper;
import model.Customer;
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
			response.sendRedirect("/static/login.jsp");
			break;
		}
		case "/pages/home": {
			try {
				int userId = Integer.parseInt(request.getParameter("userId"));
				String password = request.getParameter("password");
				UserHelper userHelper = new UserHelper();
				User user = userHelper.getUser(userId, password);
				HttpSession session = request.getSession(true);
				password = user.getPassword();
				int type = user.getType();
				if (type == UserType.Customer.getType()) {
					session.setAttribute("userId", userId);
					session.setAttribute("userName", user.getName());
					session.setAttribute("userType", UserType.getUserType(user.getType()).toString());
					response.sendRedirect(request.getContextPath() + "/user/home");
				} else if (type == UserType.Employee.getType()) {
					// todo...
				}
			} catch (CustomBankException ex) {
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
				request.getRequestDispatcher("/pages/login").forward(request, response);
			}
			break;
		}
		case "/pages/user/about": {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/static/index.jsp");
			dispatcher.forward(request, response);
			break;
		}
		case "/pages/user/profile": {
			try {
				CustomerHelper customerHelper = new CustomerHelper();
				HttpSession session = request.getSession(false);
				if (session == null) {
					request.setAttribute("warning", "Login to continue!");
					request.getRequestDispatcher("/pages/login").forward(request, response);
				} else {
					Object userIdObject = session.getAttribute("userId");
					if (userIdObject != null) {
						int userId = (int) userIdObject;
						Customer customer = customerHelper.getCustomer(userId);
						request.setAttribute("customer", customer);
						request.getRequestDispatcher("/WEB-INF/pages/profile.jsp").forward(request, response);
					} else {
						throw new CustomBankException("Invalid User!");
					}
				}
			} catch (CustomBankException ex) {
				response.sendError(403, ex.getMessage());
			}
			break;
		}
		case "/pages/user/changePassword": {
			HttpSession session = request.getSession(false);
			if (session == null) {
				request.getRequestDispatcher("/pages/login").forward(request, response);
			} else if (request.getParameter("currPassword") == null) {
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
			if (session == null) {
				response.sendRedirect("/static/login.jsp");
			} else {
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
			if (session == null) {
				response.sendRedirect("/static/login.jsp");
			} else {
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
			if (session == null) {
				response.sendRedirect("/static/login.jsp");
			} else {
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
			if (session == null) {
				response.sendRedirect("/static/login.jsp");
			} else {
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
				setCustomerAccounts(request, response);
				HttpSession session = request.getSession(false);
				String selectedAccountString = request.getParameter("account");
				if (selectedAccountString == null) {
					System.out.println("from selectedAccountString is null!!! from maincontroller");
					throw new CustomBankException("Please select Account!");
				}
				long selectedAccount = Long.parseLong(selectedAccountString);
				String fromDateString = request.getParameter("from_date");
				String toDateString = request.getParameter("to_date");
				if (fromDateString == null || toDateString == null) {
					throw new CustomBankException("Date not selected!");
				}
				System.out.println("fromdatestring : " + fromDateString);
				System.out.println("todatestring : " + toDateString);
				long fromDate = Utilities.getDateInMillis(fromDateString);
				long toDate = Utilities.getDateInMillis(toDateString);
				String pageString = request.getParameter("page");
				int page = 1;
				if (pageString != null) {
					page = Integer.parseInt(pageString);
				}
				request.setAttribute("selected-account", selectedAccount);
				request.setAttribute("from-date", fromDate);
				request.setAttribute("to-date", toDate);
				request.setAttribute("selected-page", page);
				System.out.println(selectedAccount + " " + fromDate + " " + toDate + " " + page + " from maincontroller /pages/user/transactionHistory");
				TransactionHelper helper = new TransactionHelper();
				int totalNoOfTransactions = helper.getNoOfTransactions(selectedAccount, fromDate, toDate);
				System.out.println("NoOf Transactions : " + totalNoOfTransactions);
				if (totalNoOfTransactions == 0) {
					throw new CustomBankException("No transactions found");
				}
				int totalPages = (int) Math.ceil(totalNoOfTransactions / 10);
				request.setAttribute("totalPages", totalPages);
				System.out.println("totalPages : " + totalPages);
				List<Transaction> transactionsList = helper.getTransactionsList(selectedAccount, fromDate, toDate, 10,
						(page - 1) * 10);
				for(Transaction trans : transactionsList) {
					System.out.println(trans.toString());
				}
				request.setAttribute("transactions", transactionsList);
				request.setAttribute("page_type", "customerTransactionHistory");
				request.setAttribute("failure-info", null);
				request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
//				response.sendRedirect(request.getContextPath() + "/WEB-INF/pages/userHome.jsp");
			} catch (CustomBankException ex) {
				ex.printStackTrace();
				System.out.println("In exception!!");
				request.setAttribute("failure-info", ex.getMessage());
				request.setAttribute("page_type", "customerTransactionHistory");
				request.getRequestDispatcher("/WEB-INF/pages/userHome.jsp").forward(request, response);
			}
			break;
		}

		case "/pages/user/logout": {
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
		if(session == null) {
			request.setAttribute("warning", "Please Login to continue!");
			request.getRequestDispatcher("/pages/login").forward(request, response);
		}
		AccountHelper accountHelper = new AccountHelper();
		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			throw new CustomBankException("Please Login to continue!");
		}
		Map<Long, JSONObject> customerAccounts = accountHelper.getCustomerAccountsWithBranch(userId);
		request.setAttribute("customerAccounts", customerAccounts);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
