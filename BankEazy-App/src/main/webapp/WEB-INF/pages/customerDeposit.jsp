<%@page import="enums.UserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Deposit</title>
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/accountOperationsStyle.css'>
</head>
<body>
	<div id="deposit-body">
		<div class="operation-body">
			<form action="deposit" method="post" autocomplete="off" class="dataForm">
				<%@ page import="java.util.Map"%>
				<%@ page import="model.Account"%>
				<%@ page import="enums.AccountStatus"%>
				<%@ page import="org.json.JSONObject"%>
				
				<%
				if((int)session.getAttribute("userType") == UserType.Customer.getType()){
				Map<Long, JSONObject> customerAccounts = (Map<Long, JSONObject>) request.getAttribute("customerAccounts");
				%>
				<div>
					<label for="selected-account">Select Account</label> <select
						name="selected-account" id="accounts-select" required>
						<option value="null" selected hidden disabled>Select Account</option>
						<%
						for (Long accountNumber : customerAccounts.keySet()) {
							Account currAccount = ((Account) (customerAccounts.get(accountNumber).get("account")));
							if (currAccount.getStatus() != AccountStatus.INACTIVE.getStatus()) {
						%>
						<option value="<%=accountNumber%>"><%=accountNumber%></option>
						<%
						}
						}
						%>
					</select>
				</div>
				<%}else{ %>
					<div>
						<label for="selected-account" class="label">Select Account</label>
						<input name="selected-account" placeholder="Account No" value="${param.selected-account }">
					</div>
				<%} %>
				<div>
					<label for="amount">Amount *</label> <input type="number" min="100"
						max="200000" step="0.01" name="amount" id="deposit-input" required placeholder="Enter amount">
				</div>
				<div>
					<label for="password">Password *</label> <input type="password"
						name="password" id="deposit-password" required placeholder="Enter password">
				</div>

				<%
				if (request.getAttribute("failure-info") != null) {
				%>
				<span class="info-bar failure"> <span class="info-icon">&#9888;</span><span>
						<%=request.getAttribute("failure-info")%></span></span>
				<%
				} else if (request.getAttribute("success-info") != null) {
				%>
				<span class="info-bar success"><span class="info-icon">&check;</span><span>
						<%=request.getAttribute("success-info")%></span></span>
				<%
				}
				%>
				<button type="submit">Deposit</button>
			</form>
		</div>
	</div>
</body>
</html>