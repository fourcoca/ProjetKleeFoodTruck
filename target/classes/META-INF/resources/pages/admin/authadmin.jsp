<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%@ include file="nav.jsp" %>
<h1>CONNECTION ADMIN</h1>
<c:url value="/admin" var="admin"/>
		
		<form:form action="" method="post">
			<label>Username:</label> <input type="text" name="username" />
			<label>Password:</label> <input type="text" name="password" />
			<input type="submit"/>
		</form:form>
</body>
</html>