<%@page import="enums.UserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add User</title>
</head>
<body>
	<div class="employee-page-body">
		<jsp:include page="userForm.jsp"></jsp:include>
	</div>
	<script>
		
	<%@ include file="/scripts/formLoaderScript.js" %>
		
	</script>
</body>
</html>