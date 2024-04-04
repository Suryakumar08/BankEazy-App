<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Search account</title>
</head>
<body>
	<div class="view-account-body">
		<%
		if (request.getAttribute("searched-account") == null) {
		%>
		<div>
			<form class="view-account-form" method="get" action="viewAccount">
				<div class="search-bar-div">
					<input class="search-input" type="number" name="viewAccountNo" placeholder="Enter Account no" autofocus="autofocus">
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
		 else if (request.getAttribute("searched-account") != null) {
		%>
		<jsp:include page="viewAccountForm.jsp"></jsp:include>
		<%
		}
		%>
</body>
</html>