package com.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class StaticFilter implements javax.servlet.Filter {

    public StaticFilter() {
        // TODO Auto-generated constructor stub
    }

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		System.out.println("From staticfilter!  FULL PATH : " + req.getRequestURI());
		String path = req.getRequestURI().substring(req.getContextPath().length());
		System.out.println("path after deleting contxtPath : " + path);
		if (path.equals("/")) {
			request.getRequestDispatcher("/static/index.jsp").forward(request, response);
		}
		else if(path.equals("/login")) {
			request.getRequestDispatcher("/static/login.jsp").forward(request, response);
		}
		else if(path.startsWith("/home")) {
			request.getRequestDispatcher("/pages" + path).forward(request, response);
		}
		else {
		    chain.doFilter(request, response);
		}
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
