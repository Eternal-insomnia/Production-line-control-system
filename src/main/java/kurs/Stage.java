package kurs;

import java.util.Date;

/**
 * @author Suvorov Alexey
 */

public class Stage {

    private Machine[] machines; // массив станков на этапе
    private int count; // количество станков на этапе
    private double input; // входной объём древесины
    private double output; // выходной объём древесины
    private double rejection; // общий процент брака на этапе

    public Stage() {
        machines = new Machine[0];
        count = 0;
        input = 0;
        output = 0;
        rejection = 0;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setInput(double input) {
        this.input = input;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void setRejection(double rejection) {
        this.rejection = rejection;
    }

    public void setMachines(Machine[] machines) {
        this.machines = machines;
    }

    public int getCount() {
        return count;
    }

    public double getInput() {
        return input;
    }

    public double getOutput() {
        return output;
    }

    public double getRejection() {
        return rejection;
    }

    public Machine[] getMachines() {
        return machines;
    }

    public void addMachine(int index, int stg, double max, double rej, Date last) {
        double inp = 0;
        Machine[] tmpArr = new Machine[machines.length+1];
        System.arraycopy(machines, 0, tmpArr, 0, machines.length);
        tmpArr[machines.length] = new Machine(index, stg, inp, max, rej, last);
        machines = new Machine[tmpArr.length];
        System.arraycopy(tmpArr, 0, machines, 0, tmpArr.length);

        count++;
        rejection += rej;
    }

    public void ioCalculate(double inputVolume) {
        input = inputVolume/count;

        for (Machine i : machines) {
            if (input > i.getMaxVolume()) {
                input = i.getMaxVolume() + 0.001;
            }
            i.setInputVolume(input);
        }
        rejection /= count;
        output = input*(1-rejection)*count;
    }

}
