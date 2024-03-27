<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset='utf-8'>
<meta http-equiv='X-UA-Compatible' content='IE=edge'>
<title>Login</title>
<link rel="icon"
	href="<%=request.getContextPath()%>/static/images/favicon.ico"
	type="image/x-icon">
<meta name='viewport' content='width=device-width, initial-scale=1'>
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/loginStyle.css'>
</head>

<body>
	<div id="login_page_logo">
		<img src="<%=request.getContextPath()%>/static/images/logo.png"
			alt="BankEazy Logo">
	</div>
	<hr>
	<div id="login_page">
		<img id="login_image"
			src="<%=request.getContextPath()%>/static/images/login-image.jpg"
			alt="Login Image">
		<div id="login_container">
			<h1>Login</h1>
			<form action="<%= request.getContextPath()%>/home" method="post">
				<div>
					<label for="userId">User ID</label> <br> <input type="number"
						placeholder="Enter userId" maxlength="10" name="userId" required autofocus>
					<br> <br>
				</div>
				<div>
					<label for="password">Password</label> <br> <input
						type="password" name="password" placeholder="Enter password"
						required> <br> <br>
				</div>
				<%
				if (request.getAttribute("warning") != null) {
				%>
				<div class="warning_bar">
					<span class="warning-icon">&#9888;</span>
					<p><%=request.getAttribute("warning") %></p>
				</div>
				<%
				}
				%>
				<input type="submit" value="Login">
			</form>
		</div>
	</div>
</body>
</html>