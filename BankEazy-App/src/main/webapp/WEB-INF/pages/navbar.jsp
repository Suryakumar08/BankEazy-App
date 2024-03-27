<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>nav-bar</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/basicStyles.css">
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/accountsStyle.css'>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>

<script>
	
<%@ include file= "../../../scripts/script.js" %>
	
</script>

<body>
	<nav class="navBar">
		
		<a class="nav-item" href="<%=request.getContextPath() %>/user/home">My Accounts</a>
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