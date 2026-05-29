<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c"%>
<%@ page isELIgnored="false"%>


<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Request Resource</title>
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
    <div><%@ include file="../navbar.jsp" %></div>
    <div class="content">
        <div class="header">
      <div class="back"><a onclick="history.back()"><i
					class="fa-solid fa-angle-left">Back</i></a>
				<h2>Edit Requesting Resource</h2>
			</div>
			</div>
      <form action="/TVPSSHub/resource/teacher/updateRequest" method="post" class="form-container">
        <label for="request">Request:</label>
        <input
          type="text"
          id="request"
          name="request"
          placeholder="${resource.request}"
          required
        />

        <label for="type">Type:</label>
        <input
          type="text"
          id="type"
          name="type"
          placeholder="${resource.type}"
          required
        />

        <label for="description">Description:</label>
        <textarea
          id="description"
          name="description"
          placeholder="${resource.description}"
          required
        ></textarea>

        <button type="submit">Submit Request</button>
      </form>
    </div>
  </body>
</html>