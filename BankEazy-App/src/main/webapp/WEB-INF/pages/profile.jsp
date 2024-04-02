<%@page import="enums.UserType"%>
<%@page import="utilities.Utilities"%>
<%@page import="model.Customer"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Profile</title>
<link rel="icon"
	href="<%=request.getContextPath()%>/static/images/favicon.ico"
	type="image/x-icon">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/basicStyles.css">
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/profileStyle.css'>
	<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<script>
	
<%@ include file="/scripts/script.js" %>
	
</script>
</head>
<body>
<%
	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");//http 1.1
	response.setHeader("pragma", "no-cache"); //http 1.0
	response.setHeader("Expires", "0"); //proxies
	%>
	<div class="logo">
		<img src="<%=request.getContextPath()%>/static/images/logo.png"
			alt="BankEazy Logo">
	</div>
	<%
	if ((int) session.getAttribute("userType") == UserType.Customer.getType()) {
	%>
	<jsp:include page="navbar.jsp"></jsp:include>
	<%
	} else {
	%>
	<jsp:include page="employeeNav.jsp"></jsp:include>
	<%
	}
	%>
	<div id="page_body">
		<div id="main_body">
			<div id="profile_container">
				<%@ page import="model.User"%>
				<%@ page import="model.Customer"%>
				<%@ page import="model.Employee"%>
				<%
				User user = (User) request.getAttribute("user");
				%>
				<div id="profile_box">
					<img src="<%=request.getContextPath()%>/static/images/profile.png"
						alt="User Profile">

					<table class="profile-table">
						<thead>
							<tr>
								<th colspan="2">User Profile</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>User ID</td>
								<td><strong><%=user.getId()%></strong></td>
							</tr>
							<tr>
								<td>Name</td>
								<td><strong><%=user.getName()%></strong></td>
							</tr>
							<tr>
								<td>Phone</td>
								<td><strong><%=user.getMobile()%></strong></td>
							</tr>
							<tr>
								<td>Gender</td>
								<td><strong><%=user.getGender()%></strong></td>
							</tr>
							<tr>
								<td>DOB</td>
								<td><strong><%=Utilities.getDateString(user.getDob())%></strong></td>
							</tr>
							<%
							if ((int) session.getAttribute("userType") == UserType.Customer.getType()) {
								Customer customer = (Customer) user;
							%>
							<tr>
								<td>PAN</td>
								<td><strong><%=customer.getPan()%></strong></td>
							</tr>
							<tr>
								<td>Aadhar</td>
								<td><strong><%=customer.getAadhar()%></strong></td>
							</tr>
							<%
							} else if ((int) session.getAttribute("userType") == UserType.Employee.getType()) {
							Employee employee = (Employee) user;
							%>
							<tr>
								<td>Salary</td>
								<td><strong>&#x20B9;<%=employee.getSalary()%></strong></td>
							</tr>
							<tr>
								<td>Joined Date</td>
								<td><strong><%=Utilities.getDateString(employee.getJoiningDate())%></strong></td>
							</tr>
							<tr>
								<td>Branch id</td>
								<td><strong><%=employee.getBranchId()%></strong></td>
							</tr>
							<%
							} else if ((int) session.getAttribute("userType") == UserType.Admin.getType()) {
							Employee employee = (Employee) user;
							%>
							<tr>
								<td>Salary</td>
								<td><strong>&#x20B9;<%=employee.getSalary()%></strong></td>
							</tr>
							<tr>
								<%
								}
								%>
							
						</tbody>
					</table>
				</div>
			</div>
		</div>



	</div>

</body>
</html>