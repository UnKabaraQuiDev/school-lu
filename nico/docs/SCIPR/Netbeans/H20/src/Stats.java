
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author matti
 */
public class Stats {

    private ArrayList<Stats> alStats = new ArrayList();

    private int healthy;
    private int cured;
    private int dead;
    private int simulitised;

    public void newCycle(int healthy, int cured, int dead, int simulitised) {
        this.healthy = healthy;
        this.cured = cured;
        this.dead = dead;
        this.simulitised = simulitised;
        reset();

    }

    public int getHealthy() {
        return healthy;
    }

    public int getCured() {
        return cured;
    }

    public int getDead() {
        return dead;
    }

    public int getSimulitised() {
        return simulitised;
    }

    public void setHealthy(int healthy) {
        this.healthy = healthy;
    }

    public void setCured(int cured) {
        this.cured = cured;
    }

    public void setDead(int dead) {
        this.dead = dead;
    }

    public void setSimulitised(int simulitised) {
        this.simulitised = simulitised;
    }

    public Object[] toArray() {
        Stats get = alStats.get(alStats.size()-1);
        String[] temp = new String[4];
        temp[0] = "Healthy: " + get.getHealthy();
        temp[1] = "Simulitised: " + get.getSimulitised();
        temp[2] = "Cured: " + get.getCured();
        temp[3] = "Dead: " + get.getDead();

        return temp;
    }

    public void reset() {
        Stats s = new Stats();
        s.setCured(cured);
        s.setDead(dead);
        s.setHealthy(healthy);
        s.setSimulitised(simulitised);
        alStats.add(s);
        cured = 0;
        dead = 0;
        healthy = 0;
        simulitised = 0;
    }

    public void draw(Graphics g, int width, int height) {
        if (alStats == null || alStats.isEmpty()) {
            return;
        }

        Stats latest = alStats.get(alStats.size() - 1);
        double total = latest.getCured() + latest.getDead() + latest.getHealthy() + latest.getSimulitised();
        if (total == 0) {
            return;
        }

        double ppp = (double) height / total;

        int startIndex = Math.max(0, alStats.size() - width);

        int x = 0;
        for (int i = startIndex; i < alStats.size(); i++, x++) {
            Stats s = alStats.get(i);

            int yDead = (int) (s.getDead() * ppp);
            int yHealthy = (int) (s.getHealthy() * ppp);
            int yCured = (int) (s.getCured() * ppp);
            int ySimulitised = (int) (s.getSimulitised() * ppp);

            int y = 0;

            g.setColor(Color.BLACK);
            g.fillRect(x, y, 1, yDead);
            y += yDead;

            g.setColor(Color.BLUE);
            g.fillRect(x, y, 1, yHealthy);
            y += yHealthy;

            g.setColor(Color.PINK);
            g.fillRect(x, y, 1, yCured);
            y += yCured;

            g.setColor(Color.RED);
            g.fillRect(x, y, 1, ySimulitised);
        }
    }

}
