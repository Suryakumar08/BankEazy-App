<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Inter-Bank-Transfer</title>
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/accountOperationsStyle.css'>
</head>
<body>
	<div id="inter-bank-transfer-body">
	<%@ page import="enums.UserType" %>
	<%if((int)session.getAttribute("userType") == UserType.Customer.getType() && request.getAttribute("customerAccounts") == null){ %>
		
			<div>
				<p style="color: red;">You don't have Accounts! Contact nearby Branch for further
					enquiries!</p>
			</div>
			</div>
		<%} else{%>
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
						name="selected-account" id="accounts-select" required autofocus="autofocus">
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
				<div>
					<label for="recipientAccountNo">Recipient Account Number *</label>
					<input type="number" name="recipientAccountNo"
						id="recipient-acc-no" required placeholder="Recipient Account No">
				</div>
				<div>
					<label for="recipientIfsc">Recipient Account IFSC *</label> <input
						type="text" name="recipientIfsc" id="recipient-ifsc" required placeHolder="Recipient IFSC">
				</div>
				<div>
					<label for="transactionAmount">Amount *</label> <input
						type="number" name="transactionAmount" id="trans-amt" step="0.01"
						min="0.01" max="20000" required placeholder="Amount">
				</div>
				<div>
					<label for="description">Transaction Description</label> <input
						type="text" name="description" id="description" maxlength="50" placeholder="Description">
				</div>
				<div>
				<label for="password">Password *</label> <input type="password"
						name="password" id="deposit-password" required placeholder="Password">
				</div>

				<%if(request.getAttribute("failure-info") != null){ %>
                    <span class="info-bar failure"> <span class="info-icon">&#9888;</span><span> <%=request.getAttribute("failure-info") %></span></span>
                            <%}else if(request.getAttribute("success-info") != null){ 
                            %>
                            <script type="text/javascript">
                            	var referenceNo = "<%= request.getAttribute("ReferenceNo") %>";
                            	alert("Your Transaction reference no is : " + referenceNo);
                            </script>
                    <span class="info-bar success"><span class="info-icon">&check;</span><span> <%=request.getAttribute("success-info") %></span></span>
                            <%} %>
				<button type="submit">Send</button>
			</form>
		</div>
	</div>
	<%} %>
</body>
</html>