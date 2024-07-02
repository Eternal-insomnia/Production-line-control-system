<%--
  Created by Suvorov Alexey
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Главная</title>
    <link rel="stylesheet" type="text/css" href="styles/main.css">
</head>
<body>
<div class="all_center">
    <p style="font-size: 45px">
        Система анализа данных для производства CLT-панелей
    </p>
    <form action="${pageContext.request.contextPath}/main" method="post">
        <button class="cool-button" type="submit">НАЧАТЬ</button>
    </form>
</div>
</body>
</html>
