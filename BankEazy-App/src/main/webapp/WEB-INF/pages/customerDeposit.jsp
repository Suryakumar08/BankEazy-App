<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Deposit</title>
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
	<div id="deposit-body">
		<div class="operation-body">
			<form action="deposit" method="post" autocomplete="off">
				<%@ page import="java.util.Map"%>
				<%@ page import="model.Account"%>
				<%@ page import="enums.AccountStatus"%>
				<%@ page import="org.json.JSONObject"%>
				<%
				Map<Long, JSONObject> customerAccounts = (Map<Long, JSONObject>) request.getAttribute("customerAccounts");
				%>
				<div>
					<label for="selected-account">Select Account</label> <select
						name="selected-account" id="accounts-select" required>
						<option value="null" selected hidden>Select Account</option>
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
				<div>
					<label for="amount">Amount *</label> <input type="number" min="100"
						max="200000" step="0.1" name="amount" id="deposit-input" required>
				</div>
				<div>
					<label for="password">Password *</label> <input type="password"
						name="password" id="deposit-password" required>
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
				<br>
				<button type="submit">Deposit</button>
			</form>
		</div>
	</div>
</body>
</html>