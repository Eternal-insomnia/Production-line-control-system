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

@WebServlet("/main")
public class MainServlet extends HttpServlet {

    public MainServlet() {super();}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();

        printWriter.println("??");

        // при использовании GET запроса на этой странице, пользователь преренаправляется на главную
        resp.setStatus(302); // moved temporarily
        resp.setHeader("Location","http://localhost:8080/kurs/");

        printWriter.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print("<link rel='stylesheet' type='text/css' href='styles/main.css'>");

        showDB(printWriter);

        printWriter.close();
    }



    // Метод выводит на экран таблицу оборудования производственной линии
    protected void showDB (PrintWriter printWriter)
            throws IOException {

        Factory factory = new Factory();
        factory.initFactory();

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
                "</table><div class='scroll-table-body'><table><tbody>");

        for (Stage i : factory.getStages()) {
            for (Machine j : i.getMachines()) {
                printTableRow(printWriter,
                        j.getId(),
                        j.getStage(),
                        j.getInputVolume(),
                        j.getMaxVolume(),
                        j.getRejectionRate(),
                        j.getLastTM()
                );
            }
        }
        printWriter.print("</tbody></table></div></div><br>");

        printWriter.print("<form action='/kurs/addMachine' method='post'>" +
                "<button class='cool-button' type='submit'>ИЗМЕНИТЬ ОБОРУДОВАНИЕ</button>" +
                "</form>");
        printWriter.print("<form action='/kurs/deleteServlet' method='post'>" +
                "<button class='cool-button' type='submit'>УДАЛИТЬ ОБОРУДОВАНИЕ ПО НОМЕРУ</button>" +
                "<input type='number' name='id' style='font-size: 30px' required>" +
                "</form>");
        printWriter.print("<form action='/kurs/findServlet' method='get'>" +
                "<button class='cool-button' type='submit'>НАЙТИ ОБОРУДОВАНИЕ ПО НОМЕРУ</button>" +
                "<input type='number' name='id' style='font-size: 30px' required>" +
                "</form>");
    }



    // Выводит на экран тело таблицы из массива объектов класса Machine
    protected void printTableRow(PrintWriter printWriter,
            Integer id, Integer name, Double inputVolume, Double maxVolume, Double rejectionRate, Date lastTM) {

        // Устанавливаю формат даты
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        // Устанавливаю часовой пояс
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        dateFormat.setTimeZone(tz);

        // Эффективность станка
        double efficiency = (inputVolume*(1-rejectionRate)/maxVolume);

        // Дата следующего ТО
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastTM);
        cal.add(Calendar.MONTH, 3);
        Date warranty = cal.getTime();

        // Сегодняшняя дата
        Date today = Calendar.getInstance().getTime();

        DecimalFormat df = new DecimalFormat("#.##");
        printWriter.print("<tr>");

        printWriter.print("<td>" + id + "</td>" +
                "<td>" + name + "</td>");
        if (inputVolume.isNaN()) {
            printWriter.print("<td>Не работает</td>");
        } else {
            printWriter.print("<td>" + df.format(inputVolume) + "</td>");
        }
        printWriter.print("<td>" + df.format(maxVolume) + "</td>" +
                "<td>" + df.format(rejectionRate*100) + "%</td>" +
                "<td>" + dateFormat.format(lastTM) + "</td>");

        // Ячейки таблицы выбеляются цветом, если эффективность ниже положенной
        // Нормальня эффективность >0.95, ниже нормальной 0.95 > n >= 0.9,
        // Оборудование простаивает <0.9
        // Особая пометка для оборудования, которое не обслуживали больше года
        if (inputVolume.isNaN()) {
            printWriter.print("<td style='background-color: #5050504d;'>Не работает</td>" +
                    "<td style='background-color: #5050504d;'>Неполная линия производства</td>");
        } else if (today.after(warranty))
            printWriter.print("<td style='background-color: #ff00004d;'>" + df.format(efficiency*100) + "%</td>" +
                    "<td style='background-color: #ff00004d;'>Оборудование требует срочного ТО</td>");
        else if(efficiency < 0.95 && efficiency >= 0.9)
            printWriter.print("<td style='background-color: #ffe7004d;'>" + df.format(efficiency*100) + "%</td>" +
                    "<td style='background-color: #ffe7004d;'>Работатет не в полную мощность</td>");
        else if (efficiency < 0.9)
            printWriter.print("<td style='background-color: #ff62004d;'>" + df.format(efficiency*100) + "%</td>" +
                    "<td style='background-color: #ff62004d;'>Простаивает</td>");
        else if (inputVolume > maxVolume)
            printWriter.print("<td style='background-color: #ff00714d;'>" + df.format((1 - rejectionRate)*100) + "%</td>" +
                    "<td style='background-color: #ff00714d;'>Оборудованию не хватает мощности</td>");
        else printWriter.print("<td>" + df.format(efficiency*100) + "%</td>" +
                    "<td>Работает штатно</td>");
        printWriter.print("</tr>");
    }
}
