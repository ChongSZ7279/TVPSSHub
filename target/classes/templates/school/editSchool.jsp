<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Edit ${school.name}</title>
<link rel="stylesheet"
	href="<c:url value='/resources/css/general.css' />" />
<link rel="stylesheet"
	href="<c:url value='/resources/css/activity.css' />" />
<style>
/* Global Styles */
body {
	font-family: Arial, sans-serif;
	margin: 0;
	padding: 0;
}

/* Content Styles */
.content {
	padding: 20px;
}

      .container {
        margin-top: 20px;
        display: flex;
        justify-content: center;
      }

      .container > div {
        background-color: #fff;
        border: 1px solid #ddd;
        border-radius: 5px;
        padding: 20px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        max-width: 800px;
        width: 100%;
      }

      .container img {
        max-width: 100px;
        display: block;
        margin: 0 auto 20px;
      }

      .container p {
        margin: 10px 0;
        padding-bottom: 8px;
      }

      .container ul {
        list-style-type: none;
        padding: 0;
      }

      .container li {
        margin: 5px 0;
      }

      .container a {
        color: #007bff;
        text-decoration: none;
      }

      .container a:hover {
        text-decoration: underline;
      }


input[type="text"] {
	width: 100%;
	padding: 10px;
	margin-top: 10px;
	border: 1px solid #ddd;
	border-radius: 5px;
	font-size: 16px;
	font-family: Arial, sans-serif;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	border: none;
	outline: none;
	margin-bottom: 20px;
}

button {
	background-color: #4318ff;
	color: #fff;
	border: none;
	padding: 10px 20px;
	border-radius: 5px;
	cursor: pointer;
	margin-bottom: 20px;
}
</style>
</head>
<body>
	<div><%@ include file="../navbar.jsp"%></div>
	<div class="content">
		<div class="header">
			<div class="back">
				<a onclick="history.back()"><i class="fa-solid fa-angle-left">Back</i></a>
				<h2>Edit ${school.name}</h2>
			</div>
		</div>
		<div class="container">
			<form method="post"
				action="/TVPSSHub/school/teacher/saveSchoolDetail" class="form-container"
				>
				<div>
					<img src="<c:url value='/resources/images/${school.name}.png' />"
						alt="School Logo" />

					<p>School Name:</p>
					<input type="text" name="name" required value="${school.name}" />

					<p>School Code:</p>
					<input type="text" name="code" required value="${school.code}" />

					<p>Address:</p>
					<input type="text" name="address" required
						value="${school.address}" />

					<p>City:</p>
					<input type="text" name="city" required value="${school.city}" />

					<p>State:</p>
					<input type="text" name="state" required value="${school.state}" />

					<p>Postcode:</p>
					<input type="text" name="postcode" required
						value="${school.postcode}" />

					<p>Phone:</p>
					<input type="text" name="phone" required value="${school.phone}" />

					<p>Brand:</p>
					<input type="text" name="brand" required value="${school.brand}" />

					<p>Studio Available:</p>
					<input type="checkbox" name="studio"
						${school.studio ? "checked" : ""} />

					<p>School Recording Facility:</p>
					<input type="checkbox" name="schoolRecording"
						${school.schoolRecording ? "checked" : ""} />

					<p>Upload to YouTube:</p>
					<input type="checkbox" name="uploadYoutube"
						${school.uploadYoutube ? "checked" : ""} />

					<p>Recording Capability:</p>
					<input type="checkbox" name="recording"
						${school.recording ? "checked" : ""} />

					<p>Collaboration Enabled:</p>
					<input type="checkbox" name="collaborate"
						${school.collaborate ? "checked" : ""} />

					<p>Green Screen Available:</p>
					<input type="checkbox" name="greenscreen"
						${school.greenscreen ? "checked" : ""} />

					<p>YouTube Link:</p>
					<input type="text" name="youtube" value="${school.youtube}" />

					<p>Instagram Link:</p>
					<input type="text" name="instagram" value="${school.instagram}" />

					<p>Facebook Link:</p>
					<input type="text" name="facebook" value="${school.facebook}" />

					<p>Telegram Link:</p>
					<input type="text" name="telegram" value="${school.telegram}" />

					<p>WhatsApp Link:</p>
					<input type="text" name="whatsapp" value="${school.whatsapp}" />

					<button type="submit">Save Changes</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>