<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Account</title>
</head>
<body>
	<%@ page import="enums.UserType"%>
	<%@ page import="java.util.Map"%>
	<%@ page import="model.Branch"%>
	<div class="add-account-page-body">
		<form class="add-account-form" action="addAccount" method="post">
			<div class="customer-id-div">
				<label class="label" for="customerId">Customer id :</label> <input
					type="number" class="input" name="customerId"
					placeholder="Enter customer Id" required autofocus="autofocus">
			</div>
			<%
			if ((int) session.getAttribute("userType") == UserType.Employee.getType()) {
			%>
			<div>
				<input name="branchId"
					value="<%=(int) session.getAttribute("employeeBranchId")%>" hidden>
			</div>
			<%
			} else {
			%>
			<%
			@SuppressWarnings("unchecked")
			Map<Integer, Branch> branchMap = (Map<Integer, Branch>) request.getAttribute("branchMap");
			%>
			<div>
				<label class="label" for="branch">Branch :</label> <select
					class="select" id="select-branch" name="branchId" required>
					<option value="-1" hidden>select branch</option>
					<%
					for (Map.Entry<Integer, Branch> branch : branchMap.entrySet()) {
						int currBranchId = branch.getKey();
						String currBranchIdString = "" + currBranchId;
						Branch currBranch = branch.getValue();
					%>
					<option value="<%=currBranchIdString%>"><%=currBranch.getName()%></option>
					<%
					}
					%>
				</select>
			</div>
			<%
			}
			%>
			<div>
				<label class="label" for="openingBalance">Opening balance :
				</label> <input type="number" step="0.01" min="0" max="20000" class="input"
					required placeholder="opening balance" name="openingBalance">
			</div>

			<%
			if (request.getAttribute("success") != null) {
			%>
			<div class="message-div">
				<span class="success"><%=(String)request.getAttribute("success")%></span>
			</div>
			<%
			}
			if (request.getAttribute("failure") != null) {
			%>
			<div class="message-div">
				<span class="failure"><%=(String)request.getAttribute("failure")%></span>
			</div>
			<%
			}
			%>
			<div class="submit-div">
				<input type="submit" value="Create Account" class="submit-input">
			</div>
		</form>
	</div>
</body>
</html>