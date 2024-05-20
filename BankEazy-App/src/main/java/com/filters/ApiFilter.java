package com.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import exception.CustomBankException;
import helpers.ApiHelper;
import model.ApiData;


public class ApiFilter implements Filter {

 
    public ApiFilter() {
        // TODO Auto-generated constructor stub
    }


	public void destroy() {
		// TODO Auto-generated method stub
	}
 

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		String path = req.getPathInfo();
		System.out.println("Api Filter Path : " + path);
		
		String apiKey = req.getHeader("Authentication");
		if(apiKey == null) {
			apiKey = req.getHeader("secretkey");
		}
		String requestMethod = req.getMethod();
		JSONObject failureJson = new JSONObject();
		if(apiKey == null) {
			failureJson.put("response-code", 401);
			failureJson.put("reason", "Authentication key not found!");
			response.getWriter().print(failureJson);
		}
		else {
			ApiHelper helper = new ApiHelper();
			ApiData apiData = null;
			try {
				apiData = helper.getApi(apiKey);
				if((apiData == null)) {
					failureJson.put("response-code", 401);
					failureJson.put("reason", "Authentication key invalid!");
					response.getWriter().print(failureJson);
				}
				else if((apiData.getCreatedAt() + (apiData.getValidity() * 86400000l)) < System.currentTimeMillis()) {
					failureJson.put("response-code", 401);
					failureJson.put("reason", "Authentication key expired!");
					helper.removeApiKey(apiKey);
					response.getWriter().print(failureJson);
				}
				else if(apiData.getScope() == 1 && requestMethod.equals("POST")) {
					failureJson.put("response-code", 401);
					failureJson.put("reason", "Unauthorized token!");
					response.getWriter().print(failureJson);
				}
				else {
					chain.doFilter(request, response);				
				}
			}catch(CustomBankException ex) {
				ex.printStackTrace();
				failureJson.put("response-code", 500);
				failureJson.put("reason", "Internal Server Error!");
				response.getWriter().print(failureJson);
			}
		}
	}


	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
