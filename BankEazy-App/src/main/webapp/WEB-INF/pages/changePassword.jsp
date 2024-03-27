<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Change password</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/basicStyles.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/static/styles/changePasswordStyle.css">
</head>
<body>

	<div class="logo">
		<img src="<%=request.getContextPath()%>/static/images/logo.png"
			alt="BankEazy Logo">
	</div>
	
	
	<jsp:include page="navbar.jsp"></jsp:include>
	
	<div id="page_body">
        <div class="container">
            <div class="change-password-image">
                <img src="<%=request.getContextPath()%>/static/images/change-password1.png" alt="Change password image">
            </div>
            <form class="form" action="changePassword" method="post">
                <div>
                    <label for="currPassword">Enter current password</label>
                    <input type="text" name="currPassword" placeholder="Current password" autofocus required>
                </div>
                <div>
                    <label for="newPassword">Enter new password</label>
                    <input type="password" name="newPassword" placeholder="New password" required>
                </div>
                <div>
                    <label for="confirmPassword">Retype new password</label>
                    <input type="password" name="confirmPassword" placeholder="Re-type new password" required>
                </div>
				<%if(request.getAttribute("failure-info") != null){ %>
                <span class="info-bar failure"> <span class="info-icon">&#9888;</span><span><%=request.getAttribute("failure-info") %></span></span>
                <%} %>
                <%if(request.getAttribute("success-info") != null){ %>
                <span class="info-bar success"><span class="info-icon">&check;</span><span><%=request.getAttribute("success-info") %></span></span>
                   <%} %>     
                <input type="submit" value="Confirm">
            </form>
        </div>
    </div>
</body>
</html>