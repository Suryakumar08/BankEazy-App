<%@page import="model.Account"%>
<%@page import="enums.AccountStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset='utf-8'>
<meta http-equiv='X-UA-Compatible' content='IE=edge'>
<title>Accounts</title>
<meta name='viewport' content='width=device-width, initial-scale=1'>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js">
	
</script>

<link rel="icon"
	href="<%=request.getContextPath()%>/static/images/favicon.ico"
	type="image/x-icon">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/basicStyles.css">
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/accountsStyle.css'>
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/transactionHistory.css'>
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
	response.setHeader("Expires", "0"); //com.proxies
	%>
	<div class="logo">
		<img src="<%=request.getContextPath()%>/static/images/logo.png"
			alt="BankEazy Logo">
	</div>

		<%
		String requestingPageType = (String) request.getAttribute("page_type");
		pageContext.setAttribute("page_type", requestingPageType);
		%>

	<jsp:include page="navbar.jsp"></jsp:include>

	<div id="page_body">
		<jsp:include page="contentBar.jsp"></jsp:include>
	
		<%
		switch (requestingPageType) {
			case "myAccounts" : {
		%>
		<jsp:include page="myAccounts.jsp"></jsp:include>

		<%
		break;
		}
		case "customerWithdraw" : {
		%>
		<script type="text/javascript">
			changeTitle("Withdraw");
		</script>
		<jsp:include page="customerWithdraw.jsp"></jsp:include>
		<%
		break;
		}
		case "customerDeposit" : {
		%>
		<script type="text/javascript">
			changeTitle("Deposit");
		</script>
		<jsp:include page="customerDeposit.jsp"></jsp:include>
		<%
		break;
		}
		case "customerIntraBankTransfer" : {
		%>
		<script type="text/javascript">
			changeTitle("Intra-bank-transfer");
		</script>
		<jsp:include page="customerIntraBankTransfer.jsp"></jsp:include>
		<%
		break;
		}
		case "customerInterBankTransfer" : {
		%>
		<script type="text/javascript">
			changeTitle("Inter-bank-transfer");
		</script>
		<jsp:include page="customerInterBankTransfer.jsp"></jsp:include>
		<%
		break;
		}
		case "customerTransactionHistory" : {
		%>
		<script type="text/javascript">
			changeTitle("Transaction History");
		</script>
		<jsp:include page="customerTransactionHistory.jsp"></jsp:include>
		<%
		break;
		}
		default : {
		%>
		<jsp:include page="myAccounts.jsp"></jsp:include>

		<%
		}
		}
		%>

	</div>
	<script>
	<%@ include file="/scripts/historyScript.js" %>
	</script>
</body>
</html>