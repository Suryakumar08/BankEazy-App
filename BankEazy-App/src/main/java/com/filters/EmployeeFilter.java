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

import enums.UserType;

public class EmployeeFilter implements Filter {

    public EmployeeFilter() {
        // TODO Auto-generated constructor stub
    }
	public void destroy() {
		// TODO Auto-generated method stub
	}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		String path = req.getRequestURI().substring(req.getContextPath().length());
		HttpSession session = req.getSession(false);
		System.out.println("In Employee filter !!!");
		if(session == null || session.getAttribute("userType") == null) {
			System.out.println("Hello from session null employee filter");
			request.setAttribute("warning", "Please Login to continue!!!");
			req.getRequestDispatcher("/static/login.jsp").forward(request, response);
		}
		else if((int)session.getAttribute("userType") != UserType.Employee.getType()) {
			System.out.println("hii from type check employee filter");
			request.setAttribute("warning", "Access denied! Please Login!");
			request.getRequestDispatcher(req.getContextPath() + "/static/login.jsp").forward(request, response);
		}
		else {
			request.getRequestDispatcher("/pages" + path).forward(request, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}