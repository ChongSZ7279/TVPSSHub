<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c"%>
<%@ page isELIgnored="false" %>


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
  </head>
  <body>
    <div><%@ include file="../navbar.jsp" %></div>
    <div class="content">
      <button onclick="history.back()">Go Back</button><br />
      <div class="header">
        <h1>Edit Requested Resource</h1>
      </div>
      <form
        action="/TVPSSHub/resource/teacher/requestResource"
        method="post" 
      >
        <label for="request">Request:</label>
        <input
          type="text"
          id="request"
          name="request"
          placeholder="Camera"
          required
        />

        <label for="type">Type:</label>
        <input
          type="text"
          id="type"
          name="type"
          placeholder="Peralatan Rakaman Mudah Ali"
          required
        />

        <label for="description">Description:</label>
        <textarea
          id="description"
          name="description"
          placeholder="Recording equipment needed for events."
          required
        ></textarea>

        <button type="submit">Submit Request</button>
      </form>
    </div>
  </body>
</html>