<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>View Account</title>
</head>
<body>
<%@ page import="model.Account" %>
<%@ page import="enums.AccountStatus" %>
<%
Account account = (Account)request.getAttribute("searched-account");
%>
	<form action="changeStatus" method="post" class="update-account-form">
		<div>
			<label for="id" class="label">Id:</label> <input type="text"
				name="customerId" value="<%=account.getCustomerId()%>" readOnly required>
		</div>
		<!-- Name -->
		<div>
			<label for="accountNo" class="label">Account No:</label> <input type="number"
				name="accountNo" class="input" value="<%=account.getAccountNo()%>" required readOnly>
		</div>
		<div>
			<label for="balance" class="label">Balance:</label>
			<input type="number" name="balance" class="input" value="<%=account.getBalance()%>" required readOnly>
		</div>
		<div>
			<label for="branchId" class="label">Branch Id:</label>
			<input type="number" readOnly name="branchId" class="input" value="<%=account.getBranchId() %>" required>
		</div>
		<div>
			<label for="status" class="label">Status:</label>
			<input type="text" readOnly name="status" class="input" value="<%= (AccountStatus.getAccountStatus(account.getStatus())).toString()%>">
		</div>
		
		<div class="button-div">
		<%if(account.getStatus() == AccountStatus.ACTIVE.getStatus()){ %>
		<button class="deactivate-button">Deactivate</button>
		<%}else{ %>
		<button class="activate-button">Activate</button>
		<%} %>
		</div>
	</form>
</body>
</html>