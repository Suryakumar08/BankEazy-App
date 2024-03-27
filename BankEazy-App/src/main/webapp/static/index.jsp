<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset='utf-8'>
<meta http-equiv='X-UA-Compatible' content='IE=edge'>
<title>BankEazy</title>
<link rel="icon" href="<%=request.getContextPath()%>/static/images/favicon.ico" type="image/x-icon">
<meta name='viewport' content='width=device-width, initial-scale=1'>
<link rel='stylesheet' type='text/css' media='screen'
	href='<%=request.getContextPath()%>/static/styles/indexStyle.css'>
</head>
<body>
<body>
	<div id="logoDiv">
		<img src="<%=request.getContextPath()%>/static/images/logo.png" alt="BankEazy Logo">
	</div>
	<nav id="index_nav">
		<%
		if (session.getAttribute("userId") != null) {
		%>
		<div class="back-button-div">
			<a class="back-button-a" href="Javascript:history.back()"><img
				class="back-button" src="<%=request.getContextPath()%>/static/images/back.png" alt="Back button"></a>
		</div>
		<%
		}
		%>
		<%
		if(session.getAttribute("userId") == null){%>
		<div class="nav-items-div">
			<ul>
				<li class="nav-item"><a href="<%= request.getContextPath()%>/login"> Login </a></li>
			</ul>
		</div>
		<%} %>
	</nav>
	<div class="page_body">
		<div>
			<h1>Welcome to BankEazy Bank of India!</h1>
		</div>
		<div>
			<p>BankEazy Bank of India is committed to providing convenient
				and reliable banking services to individuals, businesses, and
				communities across India. With a focus on innovation, integrity, and
				customer satisfaction, we strive to empower our customers to achieve
				their financial goals.</p>
		</div>
		<div>
			<h2>Mission</h2>
			<p>To be the preferred bank of choice, delivering exceptional
				value to our customers, employees, and stakeholders.</p>

			<h2>Vision</h2>
			<p>To be a trusted partner in the financial success and
				prosperity of every individual and business we serve.</p>

			<h2>Values</h2>
			<p>Integrity, Transparency, Innovation, Customer Focus, and
				Community Engagement.</p>
		</div>
		<div>
			<h2>Community Involvement</h2>
			<p>At BankEazy Bank of India, we believe in giving back to the
				communities we serve. Through our corporate social responsibility
				initiatives, we support education, healthcare, environmental
				sustainability, and community development projects across India.</p>
		</div>
		<div>
			<h2>Financial Performance</h2>
			<p>As of the latest fiscal year, BankEazy Bank of India reported
				total assets of INR 1.5 billion, serving over 50,000 customers
				through our network of 150 branches and digital channels. We are
				proud to have received recognition for our financial strength and
				customer service excellence.</p>
		</div>
		<div>
			<h2>Call to Action:</h2>
			<p>Explore our website to learn more about our products and
				services, open an account by visiting us in person to experience the
				BankEazy difference.</p>
		</div>
	</div>

</body>
</body>

</html>