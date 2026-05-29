<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>School List</title>
<link rel="stylesheet"
	href="<c:url value='/resources/css/general.css' />" />
<link rel="stylesheet"
	href="<c:url value='/resources/css/activity.css' />" />
</head>
<body>
	<div><%@ include file="../navbar.jsp"%></div>
	<div class="content">
		<div class="header">
			<h1>School List</h1>
			<c:if test="${sessionScope.loggedInClient.role == 2}">
				<button>
					<a>My School</a>
				</button>
			</c:if>

			<c:if test="${sessionScope.loggedInClient.role == 1}">
				<button>
					<a href="/TVPSSHub/school/addSchool">Add School</a>
				</button>
			</c:if>
		</div>
		<div class="container">
			<!-- Functionalities Part -->
			<div class="filters">
				<div class="input-wrapper">
					<input type="text" placeholder="School Name" />
				</div>
				<select>
					<option value="all">All</option>
					<option value="district">District</option>
				</select>
				<button>Filter</button>
			</div>

			<div class="feedback-container">
				<table class="feedback-table">
					<thead>
						<tr>
							<th>School Name</th>
							<th>District</th>
							<th>Representative</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<!-- Iterate over schools list and display each school details -->
						<c:forEach var="school" items="${schools}">
							<tr>
								<td><a href="/TVPSSHub/school/viewDetails/${school.id}">${school.name}</a></td>
								<td>${school.district}</td>
								<td>${school.representative}</td>
								<td>
									<!-- Action Links --> <c:if
										test="${sessionScope.loggedInClient.role == 1}">
										<a href="/TVPSSHub/school/editSchool/${school.id}">Edit</a> |
					                <a
											href="/TVPSSHub/school/deleteSchool/${school.id}"
											onclick="return confirmDeleteSchool()">Delete</a>
									</c:if> <c:if test="${sessionScope.loggedInClient.role == 2}">
										<a
											href="/TVPSSHub/school/teacher/schoolDetail?name=${school.name}">View
											Details</a>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<script>
		function confirmDeleteSchool() {
			return confirm("Are you sure you want to delete this school? This action cannot be undone.");
		}
	</script>
</body>
</html>
