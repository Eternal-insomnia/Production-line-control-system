package kurs;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * @author Suvorov Alexey
 */

public class Factory {

    private final int N = 7;
    private Stage[] stages; // массив станков



    // Конструктор по умолчанию
    public Factory() {
        stages = new Stage[N];
        for (int i = 0; i < N; i++) {
            stages[i] = new Stage();
        }
    }

    public int getN() {
        return N;
    }

    public Stage[] getStages() {
        return stages;
    }

    public void setStages(Stage[] stages) {
        this.stages = stages;
    }

    public void initFactory() {

        String user = "", pass = ""; // данные доступа для базы данных
        FileInputStream fileIn = null;
        boolean checkExc = false;

        try {
            // попытка открыть файл
            fileIn = new FileInputStream("C:/Java progs/kursovaya/src/main/resources/login");
        } catch (Exception e) {
            // если файл не открывается, вывод ошибки пользователю
            System.out.println("Ошибка получения данных доступа. Не удалось открыть файл<br>");
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
                ResultSet resultSet = statement.executeQuery("select * from machine order by stage");

                // Устанавливаю формат даты
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                dateFormat.setLenient(false);
                // Устанавливаю часовой пояс
                TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
                dateFormat.setTimeZone(tz);

                // тело таблицы (цикл массива объектов класса Machine)
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int stage = resultSet.getInt(2);
                    double maxVolume = resultSet.getDouble(3);
                    double rejectionRate = resultSet.getDouble(4);
                    Date lastTM = dateFormat.parse(resultSet.getString(5));
                    // Заполнение фабрики
                    addMachine(id, stage, maxVolume, rejectionRate, lastTM);
                }
                connection.close();

                for (int i = 0; i < N; i++) {
                    if (i == 0) {
                        stages[i].ioCalculate(460);
                    } else {
                        stages[i].ioCalculate(stages[i-1].getOutput());
                    }
                }

            } catch (Exception e) {
                System.out.print("Соединение с базой данных не установлено");
            }
        }

    }

    // Добавление нового станка
    public void addMachine(int index, int stg, double max, double rej, Date last) {
        stages[stg-1].addMachine(index, stg, max, rej, last);
    }

}
