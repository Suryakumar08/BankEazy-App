<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Form</title>
</head>
<body>
<%@ page import="enums.UserType" %>
	<form id="form" method="post">
			<%
			if ((int) session.getAttribute("userType") == UserType.Admin.getType()) {
				System.out.println("Success-message : " + request.getAttribute("success-message"));
			%>
			<div>
				<label for="newUserType" class="label">User Type*</label> <select
					id="select-type" name="newUserType" class="select" required
					onchange="handleFormView()">
					<%if(request.getAttribute("success-message") != null || request.getParameter("newUserType") == null){ %>
					<option value="null" selected hidden disabled>select
						user type</option>
						<%} %>
					<%
					for (UserType currType : UserType.values()) {
					%>
					<option value="<%=currType.toString()%>"
						<%=request.getAttribute("success-message") == null && request.getParameter("newUserType") != null && ((String)request.getParameter("newUserType")).equals(currType.toString()) ? "selected" : ""%>><%=currType.toString()%></option>
					<%
					}
					%>
				</select>
			</div>
			<%
			} else if ((int) session.getAttribute("userType") == UserType.Employee.getType()) {
			%>
			<div style="display: none;">
			<select id="select-type"><option selected value="Customer">Customer</option></select>
			</div>
			<%
			}
			%>
			<div>
				<label for="newUserName" class="label">User Name* : </label> <input
					type="text" class="input" name="newUserName"
					value="${success-message eq null ? param.newUserName : ''}" required>
			</div>
			<div>
				<label for="newUserMobile" class="label">Mobile*</label> <input
					type="number" class="input" name="newUserMobile"
					value="${success-message eq null ? param.newUserMobile : ''}" inputmode="numeric"
					required>
			</div>
			<div>
				<label for="newUserGender" class="label">Gender*</label> <select
					id="select-gender" name="newUserGender" class="select" required>
					<%
					if (request.getParameter("success-message") == null) {
					%>
					<option value="null" selected hidden disabled>select
						gender</option>
					<%
					}
					%>
					<option value="Male" ${success-message eq null and param.newUserGender eq 'Male' ? 'selected' : '' }>Male</option>
					<option value="Female" ${success-message eq null and param.newUserGender eq 'Female' ? 'selected' : '' }>Female</option>
					<option value="Other" ${success-message eq null and param.newUserGender eq 'Other' ? 'selected' : '' }>Other</option>
				</select>
			</div>
			<div>
				<label for="newUserDob" class="label">Date of Birth*</label> <input
					type="date" name="newUserDob" class="input"
					value="${success-message eq null ? param.newUserDob : ''}" required>
			</div>
			<div class="customer-tags" style="display: none;">
				<label for="newUserPan" class="label">Pan*</label> <input type="text"
					name="newUserPan" class="input" value="${success-message eq null ? param.newUserPan : ''}"
					<%= request.getAttribute("newUserPan") == null ? "" : "required"%>>
			</div>
			<div class="customer-tags" style="display: none;">
				<label for="newUserAadhar" class="label">Aadhar*</label> <input type="number"
					name="newUserAadhar" class="input" value="${success-message eq null ? param.newUserAadhar : ''}"
					<%= request.getAttribute("newUserAadhar") == null ? "" : "required"%>>
			</div>
			<div class="employee-tags" style="display: none;">
				<label for="newUserSalary" class="label">Salary*</label> <input type="number"
					name="newUserSalary" class="input" value="${success-message eq null ? param.newUserSalary : ''}"
					<%= request.getAttribute("newUserSalary") == null ? "" : "required"%>>
			</div>
			<div class="employee-tags" style="display: none;">
			<%@ page import="java.util.Map" %>
			<%@ page import="model.Branch" %>
			<%Map<Integer, Branch> branchMap = (Map<Integer, Branch>)request.getAttribute("branchMap");
			String branchIdObject = request.getParameter("newUserBranch");
			int branchId = -1;
			if(branchIdObject != null){
				branchId = Integer.parseInt(branchIdObject);
			}
			%>
			<label class="label" for="newUserBranch">Branch*</label>
			<select class="select" id="select-branch" name="newUserBranch" <%= branchIdObject == null ? "" : "required"%>>
			<%if(branchId == -1){ %>
			<option value="-1" selected hidden disabled>select branch</option>
			<%} %>
			<%for(Map.Entry<Integer, Branch> branch : branchMap.entrySet()){
				int currBranchId = branch.getKey();
				String currBranchIdString = "" + currBranchId;
				System.out.println(currBranchId);
				Branch currBranch = branch.getValue();%>
				<option value="<%=currBranchIdString %>" ${success-message eq null and currBranchIdString == param.newUserBranch ? 'selected' : ''} %><%=currBranch.getName() %></option>
				<%} %>
			</select>
			</div>
			<div class="info-bar" style="display: ${success-message eq null and failure-message eq null ? 'none' : 'flex'}">
			<%if(request.getAttribute("success-message") != null){ %>
				<span class="success"><%=(String)request.getAttribute("success-message") %></span>
				<%}else if(request.getAttribute("failure-message") != null){ %>
				<span class="failure"><%=(String)request.getAttribute("failure-message") %></span>
				<%} %>
			</div>
			<div class="submit-button-div">
				<button onclick="addUserFormSubmitter()" value="Add">Add</button>
			</div>
		</form>
</body>
</html>