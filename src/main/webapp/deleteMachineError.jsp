<%@ page import="java.util.Objects" %>
<%--
  Created by Suvorov Alexey
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Delete ticket error</title>
    <link rel="stylesheet" type="text/css" href="styles/main.css">
</head>
<body>
<div class="all_center">
<p style="font-size: 30px;">
Ошибка при удалении данных, попробуйте снова
<br>
<%
    if (Objects.equals(request.getParameter("error"), "value")) {
        out.println("Вы не можете удалить запись с таким ID");
    } else if (Objects.equals(request.getParameter("error"), "sql_req")) {
        out.println("Запись не была удалена из-за неверного запроса<br>Проверьте правильность вводимого номера<br>");
    } else {
        out.println("Несанкционированный доступ, данные о посещении записаны и будут переданы администраторам");
    }
%>
</p>
<form action="${pageContext.request.contextPath}/main" method="post">
    <button class="cool-button" type="submit">НАЗАД</button>
</form>
</div>
</body>
</html>
