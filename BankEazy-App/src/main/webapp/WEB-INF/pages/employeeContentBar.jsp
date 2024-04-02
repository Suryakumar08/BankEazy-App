<%@page import="enums.UserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Employee-Admin_contentBar</title>
</head>
<body>
	<div id="contentBar">
		<ul id="contentList">
			<%
			 String requestingPageType = (String)request.getAttribute("page_type");
			
			switch (requestingPageType) {
			case "manage-user": {
			%>
			<li id="add-user"
				class="contentList_item ${page_name eq 'addUser' ? 'selected' : ''}"><a
				href="home">Add User</a></li>
			<li id="view-user"
				class="contentList_item ${page_name eq 'viewUser' ? 'selected' : ''}"><a
				href="getUser">View User</a></li>
			<%
			break;
			}
			
			}
			%>
		</ul>
	</div>
</body>
</html>