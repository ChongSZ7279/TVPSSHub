<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit School</title>
    <link rel="stylesheet" href="<c:url value='/resources/css/general.css' />" />
    <link rel="stylesheet" href="<c:url value='/resources/css/activity.css' />" />
</head>
<body>
    <div><%@ include file="../navbar.jsp"%></div>
    <div class="content">
        <div class="header">
            <div class="back">
                <a onclick="history.back()"><i class="fa-solid fa-angle-left">Back</i></a>
                <h2>Edit School</h2>
            </div>
        </div>
        <div class="form-container">
            <form method="post" action="/TVPSSHub/school/editSchool" onsubmit="return confirmEditSchool()">
                <!-- Hidden input to pass the ID of the school being edited -->
                <input type="hidden" name="id" value="${school.id}" />
                <section class="info-section">
                    <div class="form-group">
                        <label>School Name:</label>
                        <input type="text" name="name" value="${school.name}" required />
                    </div>
                    <div class="form-group">
                        <label>School District:</label>
                        <input type="text" name="district" value="${school.district}" required />
                    </div>
                    <div class="form-group">
                        <label>School Representative:</label>
                        <input type="text" name="representative" value="${school.representative}" required />
                    </div>
                </section>
                <button type="submit">Update School</button>
            </form>
        </div>
    </div>

    <script>
        function confirmEditSchool() {
            // Display confirmation alert
            var confirmation = confirm("Are you sure you want to update this school?");
            // Return true if user confirmed, otherwise prevent form submission
            return confirmation;
        }
    </script>
</body>
</html>
