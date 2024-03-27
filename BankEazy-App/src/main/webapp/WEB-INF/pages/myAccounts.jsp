<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MyAccounts</title>
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
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<script>
	
<%@ include file= "../../../scripts/script.js" %></script>
</head>
<body>
	<div id="accounts-container">
			<%@ page import="org.json.JSONObject"%>
			<%@ page import="java.util.Map"%>
			<%@ page import="java.util.Map.Entry"%>
			<%@ page import="model.Account"%>
			<%@ page import="model.Branch"%>
			<%@ page import="enums.AccountStatus" %>
			<%
			Map<Long, JSONObject> customerAccounts = (Map<Long, JSONObject>) request.getAttribute("customerAccounts");
			if (customerAccounts != null) {
				int index = 0;
				for (Map.Entry<Long, JSONObject> el : customerAccounts.entrySet()) {
					index++;
					Account account = (Account) el.getValue().get("account");
					Branch branch = (Branch) el.getValue().get("branch");
			%>
			<div class="account-card">
				<div class="table-div">
					<table class="table">
						<thead>
							<tr>
								<th colspan="3"><img
									src="<%=request.getContextPath()%>/static/images/logo.png"
									alt="BankEazy logo"></th>
							</tr>
						</thead>
						<tbody>
							<tr class="acc-no">
								<td colspan="3"><%=el.getKey()%></td>
							</tr>
							<tr class="acc-data">
								<td><%=session.getAttribute("userName")%></td>
								<td><%=AccountStatus.getAccountStatus(account.getStatus())%></td>
								<td rowspan="2"><img
									src="<%=request.getContextPath()%>/static/images/emvchip.png"
									alt="EMV Chip image"></td>

							</tr>
							<tr class="acc-data">
								<td class="view-balance<%=index%>">Balance <i
									onclick="toggleBalanceView(<%=index%>)" class="fa-solid fa-eye"></i></td>
								<td class="balance<%=index%>" style="display: none">&#x20B9;<%=account.getBalance()%>
									<i onclick="toggleBalanceView(<%=index%>)"
									class="fa-solid fa-eye-slash"></i></td>
								<td><%=branch.getName()%></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<%
			}
			} else {
			%>
			<div>
				<p>You don't have Accounts! Contact nearby Branch for further
					enquiries!</p>
			</div>
			<%
			}
			%>


		</div>
</body>
</html>