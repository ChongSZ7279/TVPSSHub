<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c"%> 
<%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Resource Management</title>
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
      textarea {
      width: 100%;
      height: 120px;
      padding: 10px;
      margin-top: 10px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 16px;
      font-family: Arial, sans-serif;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      resize: none;
      border:none;
      outline: none;
      margin-bottom: 20px;
  }

      button {
          background-color: #4318FF;
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
      <div class="back"><a onclick="history.back()"><i
					class="fa-solid fa-angle-left">Back</i></a>
				<h2>Resource Management</h2>
			</div>
      </div>
      <div class="form-container">
        <p><strong>Requirement:</strong> ${resource.request}</p>
        <p><strong>School:</strong> ${resource.school}</p>
        <p><strong>State:</strong> ${resource.state}</p>
        <p><strong>Updated Date:</strong> ${resource.updatedDate}</p>
        <p><strong>Level:</strong> ${resource.level}</p>
        <p><strong>Type:</strong> ${resource.type}</p>
        <p><strong>Description:</strong> ${resource.description}</p>
    <!-- Reply Form -->
    <form action="/TVPSSHub/resource/manageResource" method="post">
        <input type="hidden" name="index" value="${index}" />
        <label for="reply">Reply:</label>
        <textarea id="reply" name="reply">${resource.reply}</textarea>
        <button type="submit">Save</button>
    </form>
    </div>

    </div>
  </body>
</html>