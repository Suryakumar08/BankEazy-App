<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>nav-bar</title>

</head>
<body>
	<nav class="navBar">
		<a class="nav-item ${page_type == 'myAccounts' ? 'selected' : '' }" href="<%=request.getContextPath() %>/user/home">My Accounts</a>
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