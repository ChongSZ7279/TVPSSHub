<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c"%> <%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${school.name} Detail</title>
    <link
      rel="stylesheet"
      href="<c:url value='/resources/css/general.css' />"
    />
    <link
      rel="stylesheet"
      href="<c:url value='/resources/css/activity.css' />"
    />
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
    <div><%@ include file="../navbar.jsp" %></div>
    <div class="content">
      <div class="header">
      
      <div class="back"><a onclick="history.back()"><i
					class="fa-solid fa-angle-left">Back</i></a>
				<h2>${school.name}</h2>
			</div>
      </div>
      <div class="container">
        <div>
          <img
            src="<c:url value='/resources/images/${school.name}.png' />"
            alt="School Logo"
          />
          <p><strong>School Code:</strong> ${school.code}</p>
          <p><strong>Address:</strong> ${school.address}</p>
          <p><strong>City:</strong> ${school.city}</p>
          <p><strong>State:</strong> ${school.state}</p>
          <p><strong>Postcode:</strong> ${school.postcode}</p>
          <p><strong>Phone:</strong> ${school.phone}</p>
          <p><strong>Brand:</strong> ${school.brand}</p>
          <p>
            <strong>Studio Available:</strong> ${school.studio ? "Yes" : "No"}
          </p>
          <p>
            <strong>School Recording Facility:</strong> ${school.schoolRecording
            ? "Yes" : "No"}
          </p>
          <p>
            <strong>Upload to YouTube:</strong> ${school.uploadYoutube ? "Yes" :
            "No"}
          </p>
          <p>
            <strong>Recording Capability:</strong> ${school.recording ? "Yes" :
            "No"}
          </p>
          <p>
            <strong>Collaboration Enabled:</strong> ${school.collaborate ? "Yes"
            : "No"}
          </p>
          <p>
            <strong>Green Screen Available:</strong> ${school.greenscreen ?
            "Yes" : "No"}
          </p>
          <p><strong>Social Media:</strong></p>
          <ul>
            <li><a href="${school.youtube}" target="_blank">YouTube</a></li>
            <li><a href="${school.instagram}" target="_blank">Instagram</a></li>
            <li><a href="${school.facebook}" target="_blank">Facebook</a></li>
            <li><a href="${school.telegram}" target="_blank">Telegram</a></li>
            <li><a href="${school.whatsapp}" target="_blank">WhatsApp</a></li>
          </ul>
        </div>
      </div>
    </div>
  </body>
</html>