package kurs;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

@WebServlet("/addServlet")
public class AddMachine extends HttpServlet {

    public AddMachine() {super();}

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
            throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();


        String action = req.getParameter("action");
        int id = Integer.parseInt(req.getParameter("id"));
        int stage = Integer.parseInt(req.getParameter("stage"));
        String max = req.getParameter("max");
        String rej = req.getParameter("rej");
        String last = req.getParameter("last");

        if ("ADD".equals(action)) {
            printWriter.print("ADD");
            String sqlreq = "insert into machine (id, stage, maxvolume, rejectionrate, lasttm) values ";
            sqlreq += ("(" + id + ", " + stage + ", " + max + ", " + (Double.parseDouble(rej)/100.0) + ", '" + last + "');");
            createReq(sqlreq, req, resp, printWriter, action);
        } else if ("UPDATE".equals(action)) {
            printWriter.print("UPDATE");
            String sqlreq = "update machine set ";
            sqlreq += "id = " + id + ", stage = " + stage + ", maxvolume = " + max + ", rejectionrate = " + (Double.parseDouble(rej)/100.0) + ", lasttm = '" + last + "' where id = " + id;
            createReq(sqlreq, req, resp, printWriter, action);
        }

        // если запрос выполнен успешно, пользователь отправляется смотреть на свои деяния
        resp.setStatus(307); // temporary redirect, сохрание POST-запроса
        resp.setHeader("Location","http://localhost:8080/kurs/main");

        printWriter.close();
    }


    public void createReq(String sqlreq, HttpServletRequest req, HttpServletResponse resp, PrintWriter printWriter, String action)
            throws IOException, ServletException {

        String user = "", pass = ""; // данные доступа для базы данных
        FileInputStream fileIn = null; // поток для чтения данных доступа
        boolean checkExc = false; // проверка на открытие файла

        // переделать проверки и получение параметров

        // получения параметров, переданных в запросе
        int id = Integer.parseInt(req.getParameter("id"));
        int stage = Integer.parseInt(req.getParameter("stage"));
        String max = req.getParameter("max");
        String rej = req.getParameter("rej");
        String last = req.getParameter("last");


        // Если пользователь вводит десятичное число через ",", то меняем её на "."
        max = max.replace(',', '.');
        rej = rej.replace(',', '.');

        // Regex для времени, места и цены
        String timeRegex = "(((0[1-9]|1\\d|2[0-8])\\.(0[1-9]|1[0-2])\\.20\\d\\d)|(29\\.02\\.20([02468][048]|[13579][26]))|((29|30)\\.(0[13456789]|1[0-2])\\.20\\d\\d)|(31\\.(0[13578]|1[02])\\.20\\d\\d))";
        String decimalRegex = "^\\d+(\\.\\d{1,2})?$";

        // если пользователь совершил ошибку при вводе, он перенаправляется на страницу ошибки
        // проверка на положительное число
        if (!last.matches(timeRegex)) {
            ServletContext servletContext = getServletContext();
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/addError?error=last");
            requestDispatcher.forward(req, resp);
        } else if (!max.matches(decimalRegex)) {
            ServletContext servletContext = getServletContext();
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/addError?error=max");
            requestDispatcher.forward(req, resp);
        } else if (!rej.matches(decimalRegex)) {
            ServletContext servletContext = getServletContext();
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/addError?error=rej");
            requestDispatcher.forward(req, resp);
        } else if (stage < 1 || stage > 7) {
            ServletContext servletContext = getServletContext();
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/addError?error=stage");
            requestDispatcher.forward(req, resp);
        } else if (id < 1) {
            ServletContext servletContext = getServletContext();
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/addError?error=id");
            requestDispatcher.forward(req, resp);
        }

        Factory factory = new Factory();
        factory.initFactory();
        if ("UPDATE".equals(action)) {
            int count = 0;
            for (int i = 0; i < factory.getStages()[stage].getMachines().length; i++) {
                if (id == factory.getStages()[stage].getMachines()[i].getId()) {
                    count++;
                    break;
                }
            }

            if (count == 0) {
                ServletContext servletContext = getServletContext();
                RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/addError?error=noid");
                requestDispatcher.forward(req, resp);
            }
        }

        // после проверки данных, производится запись в таблицу
        printWriter.println("Ваши данные<br>");
        printWriter.print("<table border='2'><tr>" +
                "<td>" + id + "</td>" +
                "<td>" + stage + "</td>" +
                "<td>" + max + "</td>" +
                "<td>" + rej + "%</td>" +
                "<td>" + last + "</td></tr></table>");

        try {
            // попытка открыть файл
            fileIn = new FileInputStream("C:/Java progs/kursovaya/src/main/resources/login");
        } catch (Exception e) {
            // если файл не открывается, вывод ошибки пользователю
            printWriter.println("Ошибка получения данных доступа. Не удалось открыть файл<br>");
            checkExc=true;
        }

        // если ошибок нет, работа продолжается
        if (!checkExc) {
            // данные доступа считываются из файла
            Scanner in = new Scanner(fileIn);
            user = in.nextLine();
            pass = in.nextLine();

            try {
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/javakurs", user, pass);
                Statement statement = connection.createStatement();
                int res = statement.executeUpdate(sqlreq);

                if (res <= 0) {
                    // если запрос не выполнен, но исключение не поймано, я не знаю в чём дело
                    resp.setStatus(302); // moved temporarily, простой GET-запрос
                    resp.setHeader("Location","http://localhost:8080/kurs/addError?error=sql_req");
                }

                connection.close();
            } catch (Exception e) {
                printWriter.print("Соединение с базой данных не установлено");
            }
        }
    }
}
