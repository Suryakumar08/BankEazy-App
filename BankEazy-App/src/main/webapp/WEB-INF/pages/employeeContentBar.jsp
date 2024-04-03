<%@page import="enums.UserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Employee-Admin_contentBar</title>
</head>
<body>
	<div id="contentBar">
		<ul id="contentList">
			<%
			 String requestingPageType = (String)request.getAttribute("page_type");
			
			switch (requestingPageType) {
			case "manage-user": {
			%>
			<li id="add-user"
				class="contentList_item ${page_name eq 'addUser' ? 'selected' : ''}"><a
				href="home">Add User</a></li>
			<li id="view-user"
				class="contentList_item ${page_name eq 'viewUser' ? 'selected' : ''}"><a
				href="getUser">View & edit User</a></li>
			<%
			break;
			}
			case "manage-accounts":{
			%>
			<li id="add-account"
				class="contentList_item ${page_name eq 'addAccount' ? 'selected' : ''}"><a
				href="addAccount">Add Account</a></li>
			<li id="view-account"
				class="contentList_item ${page_name eq 'viewAccount' ? 'selected' : ''}"><a
				href="getAccount">View Account</a></li>
			<%
			break;
			}
			case "manage-transactions":{
			%>
			<li id="deposit"
				class="contentList_item ${page_name eq 'deposit' ? 'selected' : ''}"><a
				href="deposit">Deposit</a></li>
			<li id="withdraw"
				class="contentList_item ${page_name eq 'withdraw' ? 'selected' : ''}"><a
				href="withdraw">Withdraw</a></li>
			<li id="transactionHistory"
				class="contentList_item ${page_name eq 'transactionHistory' ? 'selected' : ''}"><a
				href="transactionHistory">Transaction History</a></li>
			<%
			break;
			}}
			%>
		</ul>
	</div>
</body>
</html>