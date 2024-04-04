package com.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import exception.CustomBankException;
import helpers.UserHelper;
import utilities.Sha_256;
import utilities.Utilities;
import utilities.Validators;

public class ValidationFilter implements Filter {

    public ValidationFilter() {
        // TODO Auto-generated constructor stub
    }

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		String path = request.getRequestURI().substring(request.getContextPath().length());
		System.out.println("Debugging :: Request Path from Validation filter : " + path);
		
		switch(path) {
		case "/user/changePassword":
		case "/admin/changePassword":
		case "/employee/changePassword":{
			try {
			if(request.getParameter("currPassword") != null) {
				UserHelper userHelper = new UserHelper();
				HttpSession session = request.getSession(false);
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
			}
			}catch(CustomBankException ex) {
				request.setAttribute("failure-info", ex.getMessage());
				request.getRequestDispatcher("/WEB-INF/pages/changePassword.jsp").forward(request, response);
			}
			break;
		}
		case "/admin/addCustomer":
		case "/employee/addCustomer":{
			String newUserName = request.getParameter("newUserName");
			String mobile = request.getParameter("newUserMobile");
			String newUserDob = request.getParameter("newUserDob");
			long newUserDobInMillis = Utilities.getDateInMillis(newUserDob);
			String newUserPan = request.getParameter("newUserPan");
			String newUserAadhar = request.getParameter("newUserAadhar");
			
			try {
			Validators.validateNotNull(newUserName, "Name");
		    Validators.validateMobile(mobile);
		    Validators.validateDob(newUserDobInMillis);
		    Validators.validateAadhar(newUserAadhar);
		    Validators.validatePan(newUserPan);
			}
			catch(CustomBankException ex) {
				request.setAttribute("failure-message", ex.getMessage());
				if(path.startsWith("/admin")) {
					req.getRequestDispatcher("/pages/admin/home").forward(request, response);
				}
				else {
					req.getRequestDispatcher("/pages/employee/home").forward(request, response);
				}
			}
			break;
		}
		case "/admin/addEmployee":{
			String newUserName = request.getParameter("newUserName");
			String mobile = request.getParameter("newUserMobile");
			String newUserDob = request.getParameter("newUserDob");
			long newUserDobInMillis = Utilities.getDateInMillis(newUserDob);

			try {
				Validators.validateNotNull(newUserName, "Name");
				Validators.validateMobile(mobile);
				Validators.validateDob(newUserDobInMillis);
			} catch (CustomBankException ex) {
				request.setAttribute("failure-message", ex.getMessage());
				req.getRequestDispatcher("/pages/admin/home").forward(request, response);
			}
			break;
		}
		
		case "/user/transactionHistory":
		case "/employee/transactionHistory":
		case "/admin/transactionHistory":{
			try {
				String selectedAccountString = request.getParameter("selectedAccount");
				String fromDateString = request.getParameter("fromDate");
				String toDateString = request.getParameter("toDate");
				if (selectedAccountString == null && fromDateString == null && toDateString == null) {
					throw new CustomBankException("");
				}
				if (selectedAccountString == null || selectedAccountString.equals("")) {
					throw new CustomBankException("Please select Account!");
				}
				if (fromDateString == null || toDateString == null) {
					throw new CustomBankException("Date not selected!");
				}
			}
			catch(CustomBankException ex) {
				request.setAttribute("failure-info", ex.getMessage());
			}
			break;
		}
		
		case "/admin/getUser":
		case "/employee/getUser":{
			try {
				String viewUserId = request.getParameter("viewUserId");
				if (viewUserId == null || viewUserId.equals("")) {
					throw new CustomBankException("");
				}
			}catch(CustomBankException ex) {
				request.setAttribute("failure-message", ex.getMessage());
				request.setAttribute("page_type", "manage-user");
				request.setAttribute("page_name", "viewUser");
				request.getRequestDispatcher("/WEB-INF/pages/employeeHome.jsp").forward(request, response);
			}
			break;
		}
		case "/admin/addAccount":
		case "/employee/addAccount":{
			try {
				String customerIdString = request.getParameter("customerId");
				String branchIdString = request.getParameter("branchId");
				String openingBalanceString = request.getParameter("openingBalance");

				if (customerIdString == null && branchIdString == null && openingBalanceString == null) {
					throw new CustomBankException("");
				}
			}
			catch(CustomBankException ex) {
				request.setAttribute("failure", ex.getMessage());
				if (path.startsWith("/admin")) {
					request.getRequestDispatcher("/pages/admin/manage-accounts").forward(request, response);
				} else {
					request.getRequestDispatcher("/pages/employee/manage-accounts").forward(request, response);
				}
			}
			break;
		}
		case "/admin/viewAccount":
		case "/employee/viewAccount":{
			try {
				String accountNumberString = request.getParameter("viewAccountNo");
				if (accountNumberString.equals("")) {
					throw new CustomBankException("");
				}
			}catch(CustomBankException ex) {
				request.setAttribute("failure-message", ex.getMessage());
				if (path.startsWith("/admin")) {
					request.getRequestDispatcher("/pages/admin/getAccount").forward(request, response);
				} else {
					request.getRequestDispatcher("/pages/employee/getAccount").forward(request, response);
				}
			}
			break;
		}
			
		}
		
		request.getRequestDispatcher("/pages" + path).forward(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
