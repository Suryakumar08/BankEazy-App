<%@page import="enums.UserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>EmployeeNav</title>
</head>
<body>
	<nav class="navBar">
	<%
	if((int)session.getAttribute("userType") == UserType.Employee.getType()){ %>
		<a class="nav-item ${page_type eq 'manage-user' ? 'selected' : '' }" href="<%=request.getContextPath() %>/employee/home">Customer</a>
		<%}else if((int)session.getAttribute("userType") == UserType.Admin.getType()){ %>
		<a class="nav-item ${page_type eq 'manage-user' ? 'selected' : '' }" href="<%=request.getContextPath() %>/admin/home">User</a>
		<%} %>
		<a class="nav-item ${page_type eq 'manage-accounts' ? 'selected' : '' }" href="<%=request.getContextPath() %>/employee/manage-accounts">Accounts</a>
		<a class="nav-item ${page_type eq 'manage-transactions' ? 'selected' : '' }" href="<%=request.getContextPath() %>/employee/view-transactions">Transactions</a>
		<a class="nav-item ${page_type eq 'manage-branches' ? 'selected' : '' }" href="<%=request.getContextPath() %>/employee/manage-branches">Branches</a>
		<a class="nav-item" href="about">About</a>
		<div class="dropdown">
			<button class="dropbtn" onclick="toggleDropDownContent()">
				<i class="fa-solid fa-user"></i> <i id="down-btn"
					class="fa fa-caret-down"></i><i id="up-btn"
					class="fa-solid fa-caret-up"></i>
			</button>
			<div class="dropdown-content">
				<a href="profile">
					<p>View Profile</p>
				</a> <a href="changePassword">
					<p>Change Passsword</p>
				</a> <a href="logout">
					<p>Logout</p>
				</a>

			</div>
		</div>
	</nav>
</body>
</html>