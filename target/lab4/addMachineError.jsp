<%@ page import="java.util.Objects" %>
<%--
  Created by Suvorov Alexey
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Ошибка создания оборудования</title>
    <link rel="stylesheet" type="text/css" href="styles/main.css">
</head>
<body>
    <div class="all_center">
    <p style="font-size: 30px;">
        Ошибка при вводе данных, введите данные заново
    <br>
    <%
        if (Objects.equals(request.getParameter("error"), "last")) {
            out.println("Время должно быть записано в формате <h4>ДД.ММ.ГГГГ</h4>");
        } else if (Objects.equals(request.getParameter("error"), "max")) {
            out.println("Максимальный входной поток записывается в формате <h4>12 или 12.12, или 12,12</h4>");
        } else if (Objects.equals(request.getParameter("error"), "rej")) {
            out.println("Процент брака записывается в формате <h4>12 или 12.12, или 12,12</h4>");
        } else if (Objects.equals(request.getParameter("error"), "sql_req")) {
            out.println("Запись не была добавлена из-за неверного запроса<br>");
        } else if (Objects.equals(request.getParameter("error"), "id")) {
            out.println("Указан неверный номер оборудования <h4>Номер устройства должен быть больше 0</h4><br>");
        } else if (Objects.equals(request.getParameter("error"), "stage")) {
            out.println("Указан неверный номер этапа <h4>Номер этапа должен быть в диапазоне от 1 до 7 включительно</h4><br>");
        } else if (Objects.equals(request.getParameter("error"), "noid")) {
            out.println("Указан несуществующий номер оборудования");
        } else {
            out.println("Несанкционированный доступ, данные о посещении записаны и будут переданы администраторам");
        }
    %>
    </p>
    <form action="${pageContext.request.contextPath}/addMachine" method="post">
        <button class="cool-button" type="submit">НАЗАД</button>
    </form>
    </div>
</body>
</html>
