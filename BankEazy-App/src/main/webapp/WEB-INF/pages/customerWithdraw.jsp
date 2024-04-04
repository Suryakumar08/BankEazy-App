<%@page import="enums.UserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Withdraw</title>
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/accountOperationsStyle.css'>
</head>
<body>
	<div id="withdraw-body">
		<div class="operation-body">
			<form action="withdraw" method="post" autocomplete="off">
				<%
				response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");//http 1.1
				response.setHeader("pragma", "no-cache"); //http 1.0
				response.setHeader("Expires", "0"); //proxies
				%>
				<%@ page import="java.util.Map"%>
				<%@ page import="model.Account"%>
				<%@ page import="enums.AccountStatus"%>
				<%if((int)session.getAttribute("userType") == UserType.Customer.getType()){ %>
				<%@ page import="org.json.JSONObject"%>
				<%
				Map<Long, JSONObject> customerAccounts = (Map<Long, JSONObject>) request.getAttribute("customerAccounts");
				%>
				<div>
					<label for="selected-account">Select Account</label> <select
						name="selected-account" id="accounts-select" required autofocus="autofocus">
						<option value="null" selected hidden disabled>Select
							Account</option>
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
						<%if(request.getParameter("selected-account") == null){ %>
						<input name="selected-account" type="number" placeholder="Enter account no" required autofocus="autofocus">
						<%}else{ %>
						<input name="selected-account" type="number" required autofocus="autofocus">
						<%} %>
					</div>
				<%} %>
				<div>
					<label for="amount">Amount *</label> <input type="number" min="100"
						max="20000" step="100" name="amount" id="withdraw-input" required placeholder="Enter amount">
				</div>
				<div>
					<label for="password">Password *</label> <input type="password"
						name="password" id="withdraw-password" required placeholder="Enter password">
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
				<button type="submit">Withdraw</button>
			</form>
		</div>
	</div>

</body>
</html>