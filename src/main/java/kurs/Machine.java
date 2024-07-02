package kurs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Suvorov Alexey
 */
public class Machine {

    private int id; // идентификационный номер станка
    private int stage; // номер этапа производства
    private double inputVolume; // входной объём древесины на станок
    private double maxVolume; // выходной объём древесины из станка
    private double rejectionRate; // вероятность брака материала
    private Date lastTM; // дата последнего ТО

    // Конструкторы
    // По умолчанию
    Machine() {
        setId(0);
        setStage(0);
        setInputVolume("0.00");
        setMaxVolume("0.00");
        setRejectionRate("0.00");
        setLastTM("01.01.2000");
    }

    // С параметрами
    Machine(int ident, int stg, double inp, double max, double rej, Date last) {
        setId(ident);
        setStage(stg);
        setInputVolume(inp);
        setMaxVolume(max);
        setRejectionRate(rej);
        setLastTM(last);
    }

    // Копирования
    Machine(Machine obj) {
        setId(obj.getId());
        setStage(obj.getStage());
        setInputVolume(obj.getInputVolume());
        setMaxVolume(obj.getMaxVolume());
        setRejectionRate(obj.getRejectionRate());
        setLastTM(obj.getLastTM());
    }


    // Методы доступа к полям класса
    public int getId() {return id;}
    public int getStage() {return stage;}
    public double getInputVolume() {return inputVolume;}
    public double getMaxVolume() {return maxVolume;}
    public double getRejectionRate() {return rejectionRate;}
    public Date getLastTM() {return lastTM;}

    // Вызывать set-методы только после проверки введённых данных
    public void setId(int ident) {
        id = ident;
    }
    public void setStage(int stg) {
        stage = stg;
    }
    public void setInputVolume(double inputVolume) {
        this.inputVolume = inputVolume;
    }
    public void setInputVolume (String inp) {
        if (doubleCheck(inp)) {
            inputVolume = Double.parseDouble(inp);
        }
    }
    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }
    public void setMaxVolume (String max) {
        if (doubleCheck(max)) {
            maxVolume = Double.parseDouble(max);
        }
    }
    public void setRejectionRate(double rejectionRate) {
        this.rejectionRate = rejectionRate;
    }
    public void setRejectionRate (String rej) {
        if (doubleCheck(rej)) {
            rejectionRate = Double.parseDouble(rej);
        }
    }
    public void setLastTM(Date lastTM) {
        this.lastTM = lastTM;
    }
    public void setLastTM (String last) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); // Устанавливаю формат даты
        dateFormat.setLenient(false);
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow"); // Устанавливаю часовой пояс
        dateFormat.setTimeZone(tz);

        if(checkDate(last)) {
            try {
                lastTM = dateFormat.parse(last);
            } catch (ParseException e) {
                lastTM = null;
            }

        }
    }

    // Остальные методы

    // Проверка ввода десятичного числа
    private boolean doubleCheck(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода");
            return false;
        }
    }

    // Проверка ввода даты
    boolean checkDate(String x) {
        // Форма для проверки правильности даты
        // дата в формате ДД.ММ.ГГГГ (день.месяц.год)
        String regex = "(((0[1-9]|1\\d|2[0-8])\\.(0[1-9]|1[0-2])\\.20\\d\\d)|(29\\.02\\.20([02468][048]|[13579][26]))|((29|30)\\.(0[13456789]|1[0-2])\\.20\\d\\d)|(31\\.(0[13578]|1[02])\\.20\\d\\d))";

        if (!x.matches(regex)) {
            return false;
        } else {
            return true;
        }
    }
}
