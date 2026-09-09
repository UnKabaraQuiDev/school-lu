/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class PrintObject {
    
    private String title;
    private double time;
    private int totalTime;      // in seconds

    public PrintObject(String name, int time) {
        this.title = name;
        this.time = time;
        this.totalTime = time;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTime(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }
    
    public int getTotalTime() {
        return totalTime;
    }

    @Override
    public String toString() {
        return title + " [" + totalTime + ']';
    }
}
