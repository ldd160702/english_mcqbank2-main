<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Duc
  Date: 10/08/2023
  Time: 18:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <meta charset="UTF-8">
    <title>Upload CSV</title>
</head>
<body>
<h1>Upload CSV File</h1>
<c:if test="${not empty message}">
    <h2>${message}</h2>
</c:if>

<form:form method="POST" enctype="multipart/form-data" action="${pageContext.request.contextPath}/test/upload">
    <input type="file" name="file" accept=".csv,.xls,.xlsx" required/>
    <br/>
    <button type="submit">Upload</button>
</form:form>

</body>
</html>
