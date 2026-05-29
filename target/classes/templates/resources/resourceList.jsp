<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Resources</title>
<link rel="stylesheet" href="<c:url value='/resources/css/general.css' />" />
<link rel="stylesheet" href="<c:url value='/resources/css/activity.css' />" />
</head>
<body>
	<div><%@ include file="../navbar.jsp"%></div>
	<div class="content">
		<div class="header">
			<h1>Resources</h1>
			<div class="button">
				<c:if test="${sessionScope.loggedInClient.role == 2 || sessionScope.loggedInClient.role == 3}">
					<a href="<c:url value='/resource/addResource' />">
						<button type="button">Request Resource</button>
					</a>
				</c:if>
			</div>
		</div>
		<div class="container">
			<!-- Functionalities Part -->
			<div class="filters">
				<div class="input-wrapper">
					<input type="text" placeholder="Search..." />
				</div>
				<select>
					<option value="all">All</option>
					<option value="replied">Replied</option>
					<option value="pending">Pending</option>
				</select>
				<button type="button">Filter</button>
			</div>

			<div class="feedback-container">
				<table class="feedback-table">
					<thead>
						<tr>
							<th>Request</th>
							<th>School</th>
							<th>State</th>
							<th>Updated Date</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="resource" items="${resources}" varStatus="status">
							<tr>
								<td>${resource.request}</td>
								<td>${resource.school}</td>
								<td>${resource.state}</td>
								<td>${resource.updatedDate}</td>
								<td>
								  <a href="/TVPSSHub/resource/editResource/${resource.id}">Edit</a> |
								  <a href="/TVPSSHub/resource/deleteResource/${resource.id}" onclick="return confirm('Are you sure?')">Delete</a> |
								  <a href="/TVPSSHub/resource/viewDetails/${resource.id}">View Details</a>
								</td>

							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>
