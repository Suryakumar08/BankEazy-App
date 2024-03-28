<%@page import="enums.TransactionStatus"%>
<%@page import="enums.TransactionType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Transaction History</title>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
	<div id="page-container">
		<div id="history-header">
			<div class="accounts">
				<%@ page import="java.util.Map"%>
				<%@ page import="model.Account"%>
				<%@ page import="enums.AccountStatus"%>
				<%@ page import="org.json.JSONObject"%>
				<%
				Map<Long, JSONObject> customerAccounts = (Map<Long, JSONObject>) request.getAttribute("customerAccounts");
				System.out.println("In customerTransactionHistory ....");
				boolean isHiddenSelected = false;
				%>
				<div>
					<label for="selected-account">Select Account</label> <select
						name="selected-account" id="accounts-select" required>
						<%
						if (request.getAttribute("selected-account") == null) {
							isHiddenSelected = true;
						%>
						<option value="null" selected hidden>Select Account</option>
						<%
						}
						%>
						<%
						for (Long accountNumber : customerAccounts.keySet()) {
							Account currAccount = ((Account) (customerAccounts.get(accountNumber).get("account")));
							if (currAccount.getStatus() != AccountStatus.INACTIVE.getStatus()) {
								if (isHiddenSelected || accountNumber != (long) (request.getAttribute("selected-account"))) {
						%>
						<option value="<%=accountNumber%>"><%=accountNumber%></option>
						<%
						} else if (accountNumber == (long) (request.getAttribute("selected-account"))) {
						%>
						<option value="<%=accountNumber%>" selected><%=accountNumber%></option>
						<%
						}
						}
						}
						%>
					</select>
				</div>
			</div>
			<div class="date-from">
				<label for="from_date">From</label> <input type="date"
					name="from_date" id="from-date" min="01/01/2020" required>
			</div>
			<div class="date-to">
				<label for="to_date">To</label> <input type="date" name="to_date"
					id="to-date" required min="" max="">
			</div>
			<div class="search-div">
				<i style="color:white;" class="fa-solid fa-magnifying-glass search"></i>
			</div>


		</div>
		<%if(request.getAttribute("failure-info") == null && request.getAttribute("transactions") != null){ 
		System.out.println("No failure-info and there are transactions");
		%>
		<div id="history-body">
			<table class="transaction-table">
				<thead>
				<tr>
					<th>Transaction ID</th>
					<th>Account No</th>
					<th>Recipient Account</th>
					<th>Description</th>
					<th>Transaction type</th>
					<th>Amount</th>
					<th>Status</th>
					<th>Reference No</th>
					</tr>
				</thead>
				<tbody>
					<%@ page import="model.Transaction"%>
					<%@ page import="java.util.List"%>
					<%
					for (Transaction transaction : ((List<Transaction>) request.getAttribute("transactions"))) {
					%>
					<tr>
						<td><%=transaction.getTransactionId()%></td>
						<td><%=transaction.getAccountNo()%></td>
						<td><%=transaction.getTransactionAccountNo()%></td>
						<td><%=transaction.getDescription()%></td>
						<td><%=TransactionType.getTransactionType(transaction.getType()).toString()%></td>
						<%
						if (TransactionType.getTransactionType(transaction.getType()) == TransactionType.CREDIT
								|| TransactionType.getTransactionType(transaction.getType()) == TransactionType.DEPOSIT) {
						%>
						<td>+&#x20B9;<%=transaction.getAmount()%></td>
						<%
						} else {
						%>
						<td>-&#x20B9;<%=transaction.getAmount()%></td>
						<%
						}
						%>
						<%
						if (transaction.getStatus() == TransactionStatus.SUCCESS.getStatus()) {
						%>
						<td class="status-success"><%=TransactionStatus.getTransactionStatus(transaction.getStatus()).toString()%></td>
						<%
						} else {
						%>
						<td class="status-failure"><%=TransactionStatus.getTransactionStatus(transaction.getStatus()).toString()%></td>
						<%
						}
						%>
						<td><%=transaction.getReferenceNo()%></td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
		<%
		Object selectedPageObject = request.getAttribute("selected-page");
		int selectedPage = selectedPageObject != null ? (int) selectedPageObject : 1;
		Object totalPagesObject = request.getAttribute("totalPages");
		int totalPages = totalPagesObject != null ? (int) totalPagesObject : 0;
		if (totalPages > 1) {
		%>
		<div class="pagination-container">
			<div class="pagination">
				<%
				if (selectedPage != 1) {
				%>
				<a href="#" class="prev">&laquo;</a>
				<%
				}
				%>
				<div class="pageNumbers">
					<%
					int currPage = 1;
					for (; currPage <= totalPages; currPage++) {
						if (selectedPage == currPage) {
					%>
					<a href="#" class="pageNumber active" value="<%=currPage%>"><%=currPage%></a>
					<%} else {%>
					<a href="#" class="pageNumber" value="<%=currPage%>"><%=currPage%></a>
					<%
					}
					}
					%>
				</div>
				<%
				if (selectedPage != totalPages) {
				%>
				<a href="#" class="next">&raquo;</a>
				<%
				}
				%>
			</div>
		</div>
		<%} %>
		<%} else if(request.getAttribute("failure-info") != null){%>
		<div style="color :red;height:50px; width:100vw; font-size:40px; margin:40px 0px 0px 40px">
			<%=request.getAttribute("failure-info") %>
		</div>
		<%} %>
		</div>
</body>
</html>