<%--
  Created by Suvorov Alexey
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Новое оборудование</title>
    <link rel="stylesheet" type="text/css" href="styles/main.css">
</head>
<body>
    <p style="font-size: 45px; color: #ff9600;">Добавление нового оборудования  </p>
    <form action="${pageContext.request.contextPath}/addServlet" method="post">
      <input type="number" name="id" required> Номер оборудования<br>
      <input type="number" name="stage" required> Номер этапа<br>
      <input type="text" name="max" required> Максимальный входной поток, м³<br>
      <input type="text" name="rej" required> Процент брака<br>
      <input type="text" name="last" required> Дата последнего ТО в формате дд.мм.гггг<br><br>
      <button class="cool-button" type="submit" name="action" value="ADD">ДОБАВИТЬ НОВОЕ ОБОРУДОВАНИЕ</button><br>
      <button class="cool-button" type="submit" name="action" value="UPDATE">ОБНОВИТЬ СУЩЕСТВУЮЩЕЕ ОБОРУДОВАНИЕ</button>
    </form>
</body>
</html>
