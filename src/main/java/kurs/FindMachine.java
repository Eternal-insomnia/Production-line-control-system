package kurs;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Suvorov Alexey
 */

@WebServlet("/findServlet")
public class FindMachine extends HttpServlet {

    public FindMachine() {super();}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print("<link rel='stylesheet' type='text/css' href='styles/main.css'>");

        Factory factory = new Factory();
        factory.initFactory();
        String filter = req.getParameter("id");

        // заголовок таблицы
        printWriter.print("<div class='scroll-table'>" +
                "<table border='2'><thead><tr>" +
                "<th>Номер оборудования</th>" +
                "<th>Этап</th>" +
                "<th>Входной поток, м³</th>" +
                "<th>Максимальный поток, м³</th>" +
                "<th>Процент брака</th>" +
                "<th>Дата последнего ТО</th>" +
                "<th>Эффективность оборудования</th>" +
                "<th>Статус</th>" +
                "</tr></thead>" +
                "</table><div class='scroll-table-body' style='height: 100px'><table><tbody>");

        // Устанавливаю формат даты
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        // Устанавливаю часовой пояс
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        dateFormat.setTimeZone(tz);

        int count = 0;
        for (Stage i : factory.getStages()) {
            for (Machine j : i.getMachines()) {
                if (j.getId() == Integer.parseInt(filter)) {
                    count++;
                    // Эффективность станка
                    double efficiency = (j.getInputVolume()*(1-j.getRejectionRate())/j.getMaxVolume());

                    // Дата следующего ТО
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(j.getLastTM());
                    cal.add(Calendar.YEAR, 1);
                    Date warranty = cal.getTime();

                    // Сегодняшняя дата
                    Date today = Calendar.getInstance().getTime();

                    DecimalFormat df = new DecimalFormat("#.##");

                    printWriter.print("<tr>");
                    printWriter.print("<td>" + j.getId() + "</td>" +
                            "<td>" + j.getStage() + "</td>" +
                            "<td>" + df.format(j.getInputVolume()) + "</td>" +
                            "<td>" + df.format(j.getMaxVolume()) + "</td>" +
                            "<td>" + df.format(j.getRejectionRate()*100) + "%</td>" +
                            "<td>" + dateFormat.format(j.getLastTM()) + "</td>");

                    // Ячейки таблицы выбеляются цветом, если эффективность ниже положенной
                    // Нормальня эффективность >0.95, ниже нормальной 0.95 > n >= 0.9,
                    // Оборудование простаивает <0.9
                    // Особая пометка для оборудования, которое не обслуживали больше года
                    if (today.after(warranty))
                        printWriter.print("<td style='background-color: #ff00004d;'>" + df.format(efficiency*100) + "%</td>" +
                                "<td style='background-color: #ff00004d;'>Оборудование требует срочного ТО</td>");
                    else if(efficiency < 0.95 && efficiency >= 0.9)
                        printWriter.print("<td style='background-color: #ffe7004d;'>" + df.format(efficiency*100) + "%</td>" +
                                "<td style='background-color: #ffe7004d;'>Работатет не в полную мощность</td>");
                    else if (efficiency < 0.9)
                        printWriter.print("<td style='background-color: #ff62004d;'>" + df.format(efficiency*100) + "%</td>" +
                                "<td style='background-color: #ff62004d;'>Простаивает</td>");
                    else if (j.getInputVolume() > j.getMaxVolume())
                        printWriter.print("<td style='background-color: #ff00714d;'>" + df.format((1 - j.getRejectionRate())*100) + "%</td>" +
                                "<td style='background-color: #ff00714d;'>Оборудованию не хватает мощности</td>");
                    else printWriter.print("<td>" + df.format(efficiency*100) + "%</td>" +
                                "<td>Работает штатно</td>");
                    printWriter.print("</tr>");
                }
            }
        }
        printWriter.print("</table></div></div>");

        if (count == 0) {
            printWriter.print("<p style='color: #ff9600; font-size: 30px; padding-left: 20px'>" +
                    "По вашему запросу ничего не найдено</p><br>");
        }

        printWriter.print("<br><form action='/kurs/main' method='post'>" +
                "<button class='cool-button' type='submit'>НАЗАД</button>" +
                "</form>");

        printWriter.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();

        printWriter.println("??");

        // при использовании GET запроса на этой странице, пользователь преренаправляется на главную
        resp.setStatus(302); // moved temporarily
        resp.setHeader("Location","http://localhost:8080/kurs/");

        printWriter.close();
    }
}
