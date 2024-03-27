<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Inter-Bank-Transfer</title>
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
	<div id="inter-bank-transfer-body">
		<div class="operation-body">
			<form action="inter-bank-transfer" method="post" autocomplete="off">
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
					<label for="recipientAccountNo">Recipient Account Number *</label>
					<input type="number" name="recipientAccountNo"
						id="recipient-acc-no" required>
				</div>
				<div>
					<label for="recipientIfsc">Recipient Account IFSC *</label> <input
						type="text" name="recipientIfsc" id="recipient-ifsc" required>
				</div>
				<div>
					<label for="transactionAmount">Amount *</label> <input
						type="number" name="transactionAmount" id="trans-amt" step="0.1"
						min="0.1" max="20000" required>
				</div>
				<div>
					<label for="description">Transaction Description</label> <input
						type="text" name="description" id="description" maxlength="50">
				</div>
				<div>
					<label for="password">Password *</label> <input type="password"
						name="password" id="deposit-password" required>
				</div>

				<%if(request.getAttribute("failure-info") != null){ %>
                    <span class="info-bar failure"> <span class="info-icon">&#9888;</span><span> <%=request.getAttribute("failure-info") %></span></span>
                            <%}else if(request.getAttribute("success-info") != null){ %>
                    <span class="info-bar success"><span class="info-icon">&check;</span><span> <%=request.getAttribute("success-info") %></span></span>
                            <%} %> <br>
				<button type="submit">Send</button>
			</form>
		</div>
	</div>
	</div>
</body>
</html>