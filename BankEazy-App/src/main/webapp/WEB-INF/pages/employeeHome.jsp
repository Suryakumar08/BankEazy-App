<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Employee Home</title>
<meta name='viewport' content='width=device-width, initial-scale=1'>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js">
	
</script>

<link rel="icon"
	href="<%=request.getContextPath()%>/static/images/favicon.ico"
	type="image/x-icon">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/basicStyles.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/styles/employeeStyles.css">
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
	String requestingPageType = (String)request.getAttribute("page_type");
	String currPage = (String)request.getAttribute("page_name");%>
	<jsp:include page="employeeNav.jsp"></jsp:include>
	<div id="page_body">
	<jsp:include page="employeeContentBar.jsp"></jsp:include>
	<%switch(currPage){ 
	case "addUser":{
	%>
	<jsp:include page="addUserPage.jsp"></jsp:include>
	<%
	break;}
	case "viewUser":{
		%>
		<jsp:include page="viewUserPage.jsp"></jsp:include>
		<%
	}
	} %>
	</div>
	
</body>
</html>