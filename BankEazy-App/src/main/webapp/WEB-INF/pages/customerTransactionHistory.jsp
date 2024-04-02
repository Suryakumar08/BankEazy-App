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
		<form id="form" action="transactionHistory" method="post">
		<div id="history-header">
			<div class="accounts">
				<%@ page import="java.util.Map"%>
				<%@ page import="model.Account"%>
				<%@ page import="enums.AccountStatus"%>
				<%@ page import="org.json.JSONObject"%>
				<%
				Map<Long, JSONObject> customerAccounts = (Map<Long, JSONObject>) request.getAttribute("customerAccounts");
				boolean isHiddenSelected = false;
				%>
				<div>
					<label for="selected-account">Select Account</label> <select
						name="selectedAccount" id="accounts-select" required>
						<%
						if (request.getAttribute("selected-account") == null) {
							isHiddenSelected = true;
						%>
						<option value="null" selected hidden disabled>Select Account</option>
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
			<label for="from_date">From</label>
			<%if(request.getAttribute("from-date") == null){ %>
				 <input type="date"
					name="fromDate" id="from-date" min="01/01/2020" required>
					<%}else{ %>
					<input type="date"
					name="fromDate" id="from-date" min="01/01/2020" value="<%=request.getAttribute("from-date") %>" required>
					<%} %>
			</div>
			<div class="date-to">
				<label for="to_date">To</label> 
				<%if(request.getAttribute("to-date") == null){ %>
				<input type="date" name="toDate"
					id="to-date" required min="" max="">
					<%}else{ %>
						<input type="date" name="toDate"
					id="to-date" required value="<%=request.getAttribute("to-date") %>" min="" max="">
					<%} %>
			</div>
			<div class="search-div">
			 	<button type="submit" style="text-align: center;"><i style="color:black; height:25px; width:25px; font-size: 20px" class="fa-solid fa-magnifying-glass search"></i></button>
			</div>
		</div>
		</form>
		<%if(request.getAttribute("failure-info") == null && request.getAttribute("transactions") != null){ 
			Object selectedPageObject = request.getAttribute("selected-page");
			int selectedPage = selectedPageObject != null ? (int) selectedPageObject : 1;
			Object totalPagesObject = request.getAttribute("totalPages");
			int totalPages = totalPagesObject != null ? (int) totalPagesObject : 0;
			System.out.println("total pages from customer transaction history : " + totalPages);
			if (totalPages > 1) {
			%>
			<div class="pagination-container">
				<div class="pagination">
					<%
					if (selectedPage != 1) {
					%>
					<button onclick="changePrevPage(<%=request.getAttribute("selected-page")%>)" class="prev"> <%="<<" %></button>
					<%
					}
					%>
					<div class="pageNumbers">
						<%
						int currPage = 1;
						for (; currPage <= totalPages; currPage++) {
							if (selectedPage == currPage) {
						%>
						<button class="pageNumber active"><%=currPage%></button>
						<%} else {%>
						<button onclick="changePage(this)" class="pageNumber"><%=currPage%></button>
						<%
						}
						}
						%>
					</div>
					<%
					if (selectedPage != totalPages) {
					%>
					<button onclick="changeNextPage(<%=selectedPage %>)" class="next"><%=">>" %></button>
					<%
					}
					%>
				</div>
			</div>
		<%} %>
		<jsp:include page="transactionTable.jsp"></jsp:include>
		<%} else if(request.getAttribute("failure-info") != null){%>
		<div style="height:70%; width:100%; margin:40px 0px 0px 40px; display: flex; flex-direction: column; justify-content: center; align-items: center;">
			<div style="color: red; font-size: 40px"><%=request.getAttribute("failure-info") %></div>
		</div>
		<%} %>
		</div>
</body>
</html>