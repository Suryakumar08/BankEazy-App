<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>View form</title>
</head>
<body>
	<%@ page import="model.User"%>
	<%@ page import="model.Customer"%>
	<%@ page import="model.Employee"%>
	<%@ page import="utilities.*" %>
	<%@ page import="enums.UserStatus" %>
	<%@ page import="enums.UserType" %>
	<%
	User user = (User) request.getAttribute("searched-user");
	String userType = user.getTypeAsString();
	%>
	<form action="updateUserDetails" method="post" class="update-user-form">
	<div>
	<label for="id" class="label">Id:</label>
        <input type="text" name="userId" value="<%= user.getId() %>" readOnly required>
        </div>
        <!-- Name -->
        <div>
            <label for="name" class="label">Name:</label>
            <input type="text" name="name" class="input" value="<%= user.getName() %>" required autofocus="autofocus">
        </div>
        
        <!-- Phone -->
        <div>
            <label for="phone" class="label">Phone:</label>
            <input type="text" name="phone" class="input" value="<%= user.getMobile()%>" required>
        </div>
        
        <!-- Gender -->
        <div>
            <label for="gender" class="label">Gender:</label>
            <select name="gender" class="select" required>
                    <option value="Male"
                        <%= user.getGender() == "Male" ? "selected" : "" %>>
                        Male
                    </option>
                    <option value="Male"
                        <%= user.getGender() == "Female" ? "selected" : "" %>>
                        Female
                    </option>
                    <option value="Male"
                        <%= user.getGender() == "Other" ? "selected" : "" %>>
                        Other
                    </option>
            </select>
        </div>
        
        <!-- Date of Birth -->
        <div>
            <label for="dob" class="label">Date of Birth:</label>
            <input type="date" name="dob" class="input" value="<%= user.getDob() != null ? Utilities.getDOBString(user.getDob()) : "" %>" required>
        </div>
        
        <!-- Status -->
        <div>
            <label for="status" class="label">Status:</label>
            <select name="status" class="select" required>
                    <% for (UserStatus userStatus : UserStatus.values()) { %>
                        <option value="<%= userStatus.toString() %>"
                            <%= (userStatus.getStatus() == user.getStatus()) ? "selected" : ""%>>
                            <%= userStatus.toString() %>
                        </option>
                    <% } %>
                </select>
        </div>
        
        <!-- Type -->
        <div>
            <label for="type" class="label">Type:</label>
            <select name="type" class="select" readOnly required disabled="disabled">
                    <% for (UserType currUserType : UserType.values()) { %>
                        <option value="<%= currUserType.toString() %>"
                            <%= (currUserType.getType() == user.getType()) ? "selected" : ""%>>
                            <%= currUserType.toString() %>
                        </option>
                    <% } %>
                </select>
        </div>
        
        <!-- Display specific fields based on UserType -->
        <% if (userType.equals(UserType.Customer.toString())) { %>
            <!-- PAN -->
            <div>
                <label for="pan" class="label">PAN:</label>
                <input type="text" name="pan" class="input" value="<%= ((Customer) user).getPan() %>" required>
            </div>
            
            <!-- Aadhar -->
            <div>
                <label for="aadhar" class="label">Aadhar:</label>
                <input type="text" name="aadhar" class="input" value="<%= ((Customer) user).getAadhar() %>" required>
            </div>
        <% } else { %>
            <!-- Salary -->
            <div>
                <label for="salary" class="label">Salary:</label>
                <input type="text" name="salary" class="input" value="<%= ((Employee) user).getSalary() %>" required>
            </div>
            
            <!-- Joining Date -->
            <div>
                <label for="joiningDate" class="label">Joining Date:</label>
                <input type="date" name="joiningDate" class="input" value="<%= user.getDob() != null ? Utilities.getDOBString(((Employee)user).getJoiningDate()) : "" %>" required>
            </div>
            
            <!-- Branch ID -->
            <div class="employee-tags" style="display: none;">
			<%@ page import="java.util.Map" %>
			<%@ page import="model.Branch" %>
			<%Map<Integer, Branch> branchMap = (Map<Integer, Branch>)request.getAttribute("branchMap");
			%>
			<label class="label" for="branch">Branch*</label>
			<select class="select" id="select-branch" name="branch" required disabled="disabled">
			<%for(Map.Entry<Integer, Branch> branch : branchMap.entrySet()){
				int currBranchId = branch.getKey();
				String currBranchIdString = "" + currBranchId;
				Branch currBranch = branch.getValue();%>
				<option value="<%=currBranchIdString %>" <%=currBranchIdString.equals("" + ((Employee)user).getBranchId()) ? "selected" : "" %>><%=currBranch.getName() %></option>
				<%} %>
			</select>
			</div>
        <% } %>
        <%if(request.getAttribute("edit-result") != null){ %>
        <div style="display: flex; align-items: center;"><span style="color: red;">${ edit-result ne null ? edit-result : ''}</span></div>
        <%} %>
        
        <!-- Submit Button -->
        <input type="submit" value="edit">
        <input type="button" value="cancel" id="cancel-button">
    </form>
    <script>
    	document.getElementById("cancel-button").addEventListener('click', function(){
    		location.reload();
    	});
    </script>
</body>
</html>