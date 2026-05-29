<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Add New School</title>
<link rel="stylesheet"
	href="<c:url value='/resources/css/general.css' />" />
<link rel="stylesheet"
	href="<c:url value='/resources/css/activity.css' />" />
</head>
<body>
	<div><%@ include file="../navbar.jsp"%></div>
	<div class="content">
		<div class="header">
			<div class="back">
				<a onclick="history.back()"><i class="fa-solid fa-angle-left">Back</i></a>
				<h2>Add New School</h2>
			</div>
		</div>
		<div class="form-container">
			<form method="post" action="/TVPSSHub/school/addSchool"
				onsubmit="return confirmAddSchool()">
				  <input type="hidden" name="id" value=0 />
				<section class="info-section">
					<div class="form-group">
						<label>School Name:</label> <input type="text" name="name"
							required />
					</div>
					<div class="form-group">
						<label>School District:</label> <input type="text" name="district"
							required />
					</div>
					<div class="form-group">
						<label>School Representative:</label> <input type="text"
							name="representative" required />
					</div>
				</section>
				<button type="submit">Add School</button>
			</form>
		</div>
	</div>

	<script>
		function confirmAddSchool() {
			// Display confirmation alert
			var confirmation = confirm("Are you sure you want to add this school?");
			// Return true if user confirmed, otherwise prevent form submission
			return confirmation;
		}
	</script>
</body>
</html>
