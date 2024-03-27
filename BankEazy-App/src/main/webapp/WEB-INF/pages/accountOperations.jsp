<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Account Operations</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js">
	
</script>

<link rel="icon"
	href="<%=request.getContextPath()%>/static/images/favicon.ico"
	type="image/x-icon">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/basicStyles.css">
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/accountOperationsStyle.css'>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<script>
	
<%@ include file= "../../../scripts/script.js" %></script>
</head>
<body>
	<div class="logo">
		<img src="<%=request.getContextPath()%>/static/images/logo.png"
			alt="BankEazy Logo">
	</div>
	
	<jsp:include page="navbar.jsp"></jsp:include>

	<div id="page_body">
		<jsp:include page="contentBar.jsp"></jsp:include>
	</div>
	
</body>
</html>