<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<c:forEach items="${list}" var="user">
		${user.id}-----${user.username}-----${user.age}-----${user.address}<br>
	</c:forEach>
	<a href="toRegister">注册</a>
</body>
</html>