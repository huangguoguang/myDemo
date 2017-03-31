<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>注册</title>
</head>
<body>
	<form action="register.action" method="post">
		用户名:<input name="username" type="text"/>
		年龄:<input name="age" type="text"/>
		地址:<input name="address" type="text"/>
		<input type="submit" value="注册"/>
	</form>
</body>
</html>