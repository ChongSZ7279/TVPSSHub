<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>School Details</title>
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
				<h2>School Details</h2>
			</div>
		</div>
		<div class="form-container">
			<section class="info-section">
				<div class="detail-group">
					<label>School Name:</label>
					<p>${school.name}</p>
				</div>
				<div class="detail-group">
					<label>School District:</label>
					<p>${school.district}</p>
				</div>
				<div class="detail-group">
					<label>School Representative:</label>
					<p>${school.representative}</p>
				</div>
			</section>
		</div>
	</div>
</body>
</html>
