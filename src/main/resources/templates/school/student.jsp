<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c"%> <%@ page isELIgnored="false"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>School List</title>
    <link
      rel="stylesheet"
      href="<c:url value='/resources/css/general.css' />"
    />
    <link
      rel="stylesheet"
      href="<c:url value='/resources/css/activity.css' />"
    />
    <style>
      body {
        font-family: Arial, sans-serif;
        margin: 250px 0 0 0;
        padding: 0;
      }
    
      /* Functionalities Styles */
      .functionalities {
        display: flex;
        justify-content: space-between;
        margin-bottom: 20px;
      }
    
      .functionalities input,
      .functionalities select,
      .functionalities button {
        padding: 8px 12px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 14px;
      }
    
      /* Table Styles */
      .table {
        width: 100%; /* Ensure table expands to fill container */
        overflow-x: auto; /* Add horizontal scroll if needed */
      }
    
      .table table {
        width: 100%; /* Ensure the actual table fills the container */
        table-layout: auto; /* Allow columns to adjust width dynamically */
        border-collapse: collapse;
      }
    
      .table th,
      .table td {
        padding: 10px;
        text-align: left;
        border-bottom: 1px solid #ddd;
        word-wrap: break-word; /* Prevent content from overflowing */
      }
    
      .table th {
        background-color: #f2f2f2;
      }
    
      .table a {
        color: #007bff;
        text-decoration: none;
      }</style>
  </head>
  <body>
    <div><%@ include file="../navbar.jsp"%></div>
    <div class="content">
      <div class="header">
        <h1>School List</h1>
        <button
          onclick="window.location.href='/TVPSSHub/school/student/schoolDetail?name=Sekolah Tinggi Segamat'"
        >
          My School
        </button>
      </div>
      <div class="container">
        <!-- Functionalities Part -->
        <div class="functionalities">
          <input type="text" placeholder="School Name" />
          <select>
            <option value="all">All</option>
            <option value="district">District</option>
          </select>
          <button>Filter</button>
        </div>

        <!-- Resources Table Part -->
        <div class="table">
          <table border="0">
            <thead>
              <tr>
                <th>School Name</th>
                <th>District</th>
                <th>Representative</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="school" items="${schools}">
                <tr>
                  <td>
                    <a
                      href="/TVPSSHub/school/student/schoolDetail?name=${school.name}"
                      >${school.name}</a
                    >
                  </td>
                  <td>${school.district}</td>
                  <td>${school.representative}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>