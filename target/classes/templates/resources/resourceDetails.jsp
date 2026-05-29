<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Resource Details</title>
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
				<h2>Resource Details</h2>
			</div>
		</div>
		<div class="form-container">
			<section class="info-section">
				<div class="detail-group">
					<label>Request:</label>
					<p>${resource.request}</p>
				</div>
				<div class="detail-group">
					<label>Type:</label>
					<p>${resource.type}</p>
				</div>
				<div class="detail-group">
					<label>Description:</label>
					<p>${resource.description}</p>
				</div>
				<div class="detail-group">
					<label>State:</label>
					<p>${resource.state}</p>
				</div>
				<div class="detail-group">
					<label>Updated Date:</label>
					<p>${resource.updatedDate}</p>
				</div>
				<div class="detail-group">
					<label>Reply:</label>
					<p>${resource.reply}</p>
				</div>
			</section>
		</div>
	</div>
</body>
</html>
