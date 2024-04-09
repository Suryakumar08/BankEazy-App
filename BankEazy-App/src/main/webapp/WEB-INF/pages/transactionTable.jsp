<%@page import="utilities.Utilities"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Transaction Table</title>
</head>
<body>
	<div id="history-body">
			<table class="transaction-table">
				<thead>
				<tr>
					<th>Transaction ID</th>
					<th>Transaction Time</th>
					<th>Recipient Account</th>
					<th>Description</th>
					<th>Amount</th>
					<th>Status</th>
					<th>Closing Balance</th>
					<th>Reference No</th>
					</tr>
				</thead>
				<tbody>
					<%@ page import="model.Transaction"%>
					<%@ page import="java.util.List"%>
					<%@ page import="enums.TransactionType" %>
					<%@ page import="enums.TransactionStatus" %>
					<%
					for (Transaction transaction : ((List<Transaction>) request.getAttribute("transactions"))) {
					%>
					<tr>
						<td><%=transaction.getTransactionId()%></td>
						<td><%=Utilities.getDateTimeString(transaction.getTime())%></td>
						<%if(transaction.getTransactionAccountNo() == 0){ %>
						<td>-</td>
						<%}else{ %>
						<td><%=transaction.getTransactionAccountNo() %></td>
						<%} %>
						<%if(transaction.getDescription() == null || transaction.getDescription().equals("")){ %>
						<td>-</td>
						<%}else{ %>
						<td><%=transaction.getDescription()%></td>
						<%} %>
						<%
						if (TransactionType.getTransactionType(transaction.getType()) == TransactionType.CREDIT
								|| TransactionType.getTransactionType(transaction.getType()) == TransactionType.DEPOSIT) {
						%>
						<td style="color: green">+ &#x20B9;<%=transaction.getAmount()%></td>
						<%
						} else {
						%>
						<td style="color: red">- &#x20B9;<%=transaction.getAmount()%></td>
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
						<td>&#x20B9;<%=transaction.getClosingBalance() %></td>
						<td><%=transaction.getReferenceNo()%></td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
</body>
</html>