<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<link rel="shortcut icon" href="" type="image/x-icon" />
<meta http-equiv="Expires" CONTENT="-1">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Pragma" CONTENT="no-cache">
<link name="css" rel="stylesheet" href="" />
<title>华中工业品</title>
</head>
<body>
	<div class="body">
		<div>
			<div class="container"></div>
			<footer class="footer"></footer>
		</div>
	</div>
	<script type="text/javascript">
	  (function(doc,win){
		  var dpr = 1;
			var scale = 1;
			var devicePixelRatio = win.devicePixelRatio;
			dpr = 1;
			if (devicePixelRatio>=2) {dpr = 2;}
			scale = 1 / dpr;
			var metaEl = doc.querySelector('meta[name="viewport"]');
			metaEl.setAttribute('content', 'width=device-width, initial-scale=' + scale + ',maximum-scale=' + scale + ', minimum-scale=' + scale + ',user-scalable=no'); 
			doc.documentElement.setAttribute('data-dpr', dpr);
			doc.documentElement.setAttribute('style', "font-size:"+dpr*625+"%");
			win.dpr = dpr;
	  })(document,window)
		var isTest = 1;
		var baseUrl = '/mybatisDemo/v3/';
		var serverUrl = 'http://localhost/mybatisDemo/';
		document.write('<script data-main="'+baseUrl+'main.js" src="' + baseUrl + 'libs/require.js" id="main"><\/script>');
	</script>
</body>
</html>