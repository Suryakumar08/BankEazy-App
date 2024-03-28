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
                <%@ page import="java.util.Map" %>
                <%@ page import="model.Account" %>
                <%@ page import="enums.AccountStatus" %>
                <%@ page import="org.json.JSONObject" %>
                <%
                    Map<Long, JSONObject> customerAccounts = (Map<Long, JSONObject>)request.getAttribute("customerAccounts");
                    %>
                    <div>
                        <label for="selected-account">Select Account</label>
                        <select name="selected-account" id="accounts-select" required>
                            <option value="null" selected hidden>Select Account</option>
                            <%
                            for(Long accountNumber : customerAccounts.keySet()){
                            	Account currAccount = ((Account)(customerAccounts.get(accountNumber).get("account")));
                            	if(currAccount.getStatus() != AccountStatus.INACTIVE.getStatus()){
                            %>
                            <option value="<%= accountNumber%>"><%=accountNumber%></option>
                            <%}} %>
                        </select>
                    </div>
                    <div>
                        <label for="amount">Amount *</label>
                        <input type="number" min="100" max="20000" step="100" name="amount" id="withdraw-input" required>
                    </div>
                    <div>
                        <label for="password">Password *</label>
                        <input type="password" name="password" id="withdraw-password" required>
                    </div>
					
					<%if(request.getAttribute("failure-info") != null){ %>
                    <span class="info-bar failure"> <span class="info-icon">&#9888;</span><span> <%=request.getAttribute("failure-info") %></span></span>
                            <%}else if(request.getAttribute("success-info") != null){ %>
                    <span class="info-bar success"><span class="info-icon">&check;</span><span> <%=request.getAttribute("success-info") %></span></span>
                            <%} %>
                    <br>
                    <button type="submit">Withdraw</button>
                </form>
            </div>
        </div>
	
</body>
</html>