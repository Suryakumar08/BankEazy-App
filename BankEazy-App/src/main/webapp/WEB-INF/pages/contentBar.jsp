<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ContentBar</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/basicStyles.css">
</head>
<body>
	<div id="contentBar">
			<ul id="contentList">
			<% String page_type = (String)pageContext.getAttribute("page_type"); %>
				<li id="withdraw" class="contentList_item ${ page_type eq 'customerWithdraw' ? 'selected' : ''}" ><a
					href="withdraw">Withdraw</a></li>
				<li id="deposit" class="contentList_item ${ page_type eq 'customerDeposit' ? 'selected' : ''}"><a
					href="deposit">Deposit</a></li>
				<li id="intra-bank-transfer" class="contentList_item ${ page_type eq 'customerIntraBankTransfer' ? 'selected' : ''}"><a
					href="intra-bank-transfer">Intra Bank Transfer</a></li>
				<li id="inter-bank-transfer" class="contentList_item ${ page_type eq 'customerInterBankTransfer' ? 'selected' : ''}"><a
					href="inter-bank-transfer">Inter Bank Transfer</a></li>
				<li id="transactionHistory" class="contentList_item ${ page_type eq 'customerTransactionHistory' ? 'selected' : ''}"><a
					href="transactionHistory">Transaction History</a></li>
			</ul>
		</div>
</body>
</html>