package com.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import enums.UserType;
import exception.CustomBankException;
import model.Customer;
import utilities.Utilities;
import utilities.Validators;


public class AddCustomerFilter implements Filter {

    public AddCustomerFilter() {
        // TODO Auto-generated constructor stub
    }

	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("In admin/addCustomerFilter");
		HttpServletRequest req = (HttpServletRequest)request;
		String path = (req.getRequestURI()).substring(req.getContextPath().length());
		String newUserName = request.getParameter("newUserName");
		String mobile = request.getParameter("newUserMobile");
		String newUserDob = request.getParameter("newUserDob");
		long newUserDobInMillis = Utilities.getDateInMillis(newUserDob);
		String newUserPan = request.getParameter("newUserPan");
		String newUserAadhar = request.getParameter("newUserAadhar");
		
		boolean doForward = true;
		
		try {
		Validators.validateNotNull(newUserName, "Name");
	    Validators.validateMobile(mobile);
	    Validators.validateDob(newUserDobInMillis);
	    Validators.validateAadhar(newUserAadhar);
	    Validators.validatePan(newUserPan);
		}
		catch(CustomBankException ex) {
			ex.printStackTrace();
			System.out.println("Error in addUser from addCustomer filter");
			request.setAttribute("failure-message", ex.getMessage());
			if(path.startsWith("/admin")) {
				doForward = false;
				req.getRequestDispatcher("/pages/admin/home").forward(request, response);
			}
			else {
				doForward = false;
				req.getRequestDispatcher("/pages/employee/home").forward(request, response);
			}
		}
		if(doForward) {
			req.getRequestDispatcher("/pages" + path).forward(request, response);			
		}
	}


	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}