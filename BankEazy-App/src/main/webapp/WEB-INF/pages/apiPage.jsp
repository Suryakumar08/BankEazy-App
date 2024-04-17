<%@page import="utilities.Utilities"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Api Page</title>
</head>
<body>
	<div id="api-body">
		<div id="api-body-head">
			<form id="addApiForm" method="post">
				<button type="submit" onclick="addReadApi()">+ Read Api</button>
				<button type="submit" onclick="addWriteApi()">+ Write Api</button>
			</form>
		</div>
		<div id="api-body-body">
			<%@ page import="java.util.List,model.ApiData"%>
			<%
			@SuppressWarnings("unchecked")
			List<ApiData> apiDatas = (List<ApiData>) request.getAttribute("apiDatas");
			if (apiDatas != null && request.getAttribute("api-failure") == null) {
			%>
			<table id="api-table">
			<tr id="individual-api-data" class="api-table-heads">
				<td>Api Key</td>
				<td>Validity</td>
				<td>Scope</td>
				<td>Delete Key</td>
			</tr>
				<%
				for (ApiData data : apiDatas) {
				%>
				<tr id="individual-api-data">
					<td><%=data.getApiKey()%></td>
					<td><%=Utilities.getDateTimeString(data.getCreatedAt() + (data.getValidity() * 86400000l))%></td>
					<td><%=data.getScope() == 1 ? "Read Only" : "Read and Write"%></td>
					<td><button type="button" onclick="removeKey('<%=data.getApiKey()%>')">Remove</button></td>
				</tr>
				<%
				}
				%>
			</table>
			<%
			} else {
			%>
			<div class="api-failure-div">
				<div>
					<p><%=request.getAttribute("api-failure")%></p>
				</div>
			</div>
			<%
			}
			%>

		</div>
	</div>
	<script>
	<%@ include file="../../scripts/apiScripts.js" %>
	</script>
</body>
</html>