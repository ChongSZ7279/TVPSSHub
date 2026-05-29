<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Edit Resource</title>
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
                <h2>Edit Resource Request</h2>
            </div>
        </div>
		<div class="form-container">
			<form action="/TVPSSHub/resource/editResource" method="post">
				<input type="hidden" id="id" name="id" value="${resource.id}" />
				<section class="info-section">

					<div class="form-group">
						<label for="request">Request:</label> <input type="text"
							id="request" name="request" value="${resource.request}" required />
					</div>

					<div class="form-group">
						<label for="type">Type:</label> <input type="text" id="type"
							name="type" value="${resource.type}" required /> <label
							for="description">Description:</label>
						<textarea id="description" name="description" required>${resource.description}</textarea>
					</div>

					<div class="form-group">
						<label for="state">State:</label> <input type="text" id="state"
							name="state" value="${resource.state}" required /> <label
							for="updatedDate">Updated Date:</label> <input type="date"
							id="updatedDate" name="updatedDate"
							value="${resource.updatedDate}" required />
					</div>
				</section>

				<button type="submit">Update Resource</button>
			</form>
		</div>
	</div>
</body>
</html>
