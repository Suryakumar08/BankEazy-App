package com.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


public class ApiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public ApiController() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String path = request.getPathInfo();
		JSONObject responseObject = null;
		String blocks[] = path.split("/");
		if(blocks.length == 0) {
			response.getWriter().print("Invalid url!");
		}
		else if(blocks[0].equals("customer")) {
			
		}
		else if(blocks[0].equals("employee")) {
			
		}
		else if(blocks[0].equals("admin")) {
			
		}
		else {
			response.getWriter().print("Invalid url!");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
