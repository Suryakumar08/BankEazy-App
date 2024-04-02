<%@page import="enums.UserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>View User</title>
</head>
<body>
	
	<div class="view-user-body">
		<%
		if (request.getAttribute("searched-user") == null) {
		%>
		<div>
			<form class="view-user-form" method="get" action="getUser">
				<div class="search-bar-div">
					<input class="search-input" type="number" name="viewUserId">
				</div>
				<div>
					<button type="submit" class="button">Search</button>
				</div>
			</form>
		</div>
		<%
		if (request.getAttribute("failure-message") != null) {
		%>
		<div class="message-div">
			<span style="color: red; width: 70%"><%= (String)request.getAttribute("failure-message") %></span>
		</div>
		<%
		}
		if (request.getAttribute("success-message") != null) {
		%>
		<div class="message-div">
			<span style="color: green; width: 70%"><%= (String)request.getAttribute("success-message") %></span>
		</div>
		<%
		}
		}
		 else if (request.getAttribute("searched-user") != null) {
		%>
		<jsp:include page="viewForm.jsp"></jsp:include>
		<%
		}
		%>
	</div>
</body>
</html>