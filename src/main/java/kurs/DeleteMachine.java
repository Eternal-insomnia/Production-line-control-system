package kurs;

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

/**
 * @author Suvorov Alexey
 */

@WebServlet("/deleteServlet")
public class DeleteMachine extends HttpServlet {

    public DeleteMachine() {super();}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();

        printWriter.println("??");

        // при использовании GET запроса на этой странице, пользователь преренаправляется на главную
        resp.setStatus(302); // moved temporarily
        resp.setHeader("Location","http://localhost:8080/kurs/");

        printWriter.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter printWriter = resp.getWriter();

        int id = Integer.parseInt(req.getParameter("id"));
        printWriter.println("Вы удаляете строку " + id);
        // проверка введённого ID

        // запрос на удаление в таблицу MySQL
        String user = "", pass = ""; // данные доступа для базы данных
        FileInputStream fileIn = null; // поток для чтения данных доступа
        boolean checkExc = false; // проверка на открытие файла

        try {
            // попытка открыть файл
            fileIn = new FileInputStream("C:/Java progs/kursovaya/src/main/resources/login");
        } catch (Exception e) {
            // если файл не открывается, вывод ошибки пользователю
            printWriter.println("Ошибка получения данных доступа. Не удалось открыть файл<br>");
            checkExc=true;
        }

        if (id < 1) {
            resp.setStatus(302); // moved temporarily
            resp.setHeader("Location", "/kurs/deleteError?error=value");
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
                String sqlRequest = "delete from machine where id=" + id;
                int res = statement.executeUpdate(sqlRequest);

                if (res > 0) {
                    // если запрос выполнен успешно, пользователь отправляется смотреть на свои деяния
                    resp.setStatus(307); // temporary redirect, сохрание POST-запроса
                    resp.setHeader("Location","http://localhost:8080/kurs/main");
                } else {
                    // если запрос не выполнен, но исключение не поймано, я не знаю в чём дело
                    resp.setStatus(302); // moved temporarily, простой GET-запрос
                    resp.setHeader("Location","http://localhost:8080/kurs/deleteError?error=sql_req");
                }

                connection.close();
            } catch (Exception e) {
                printWriter.print("Соединение с базой данных не установлено");
            }
        }

        printWriter.close();
    }
}
