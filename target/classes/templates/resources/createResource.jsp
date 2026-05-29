<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Create Resource</title>
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
				<h2>Create New Resource</h2>
			</div>
		</div>
		<div class="form-container">
			<form action="/TVPSSHub/resource/addResource" method="post">
				<input type="hidden" id="id" name="id" value=0 />
				<section class="info-section">

					<div class="form-group">
						<label for="request">Request:</label> 
						<input type="text" id="request" name="request" placeholder="Enter Request" required />
					</div>

					<div class="form-group">
						<label for="type">Type:</label> 
						<input type="text" id="type" name="type" placeholder="Enter Type" required /> 
						<label for="description">Description:</label>
						<textarea id="description" name="description" placeholder="Enter Description" required></textarea>
					</div>

					<div class="form-group">
						<label for="state">State:</label> 
						<input type="text" id="state" name="state" placeholder="Enter State" required />
					</div>

				</section>
				<button type="submit">Create Resource</button>
			</form>
		</div>
	</div>
</body>
</html>
