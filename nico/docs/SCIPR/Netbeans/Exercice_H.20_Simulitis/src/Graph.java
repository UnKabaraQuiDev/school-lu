
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Graph {
    private int healthy = 0;
    private int infected = 0;
    private int cured = 0;
    private int dead = 0;
    
    private ArrayList<Graph> alHistory = new ArrayList<>();
    
    @Override
    public Graph clone() {
        return null;
    }
    
    public void draw2(Graphics g, int width, int height) {
        int offset = Math.max(0, alHistory.size() - width);
        
        for (int i = 0; i < alHistory.size(); i++) {
            Graph s = alHistory.get(i);
            
        }
    }
    
    public void newCycle(int healthy, int cured, int dead, int simulitised) {
        this.healthy = healthy;
        this.cured = cured;
        this.dead = dead;
        this.infected = simulitised;
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

    public int getInfected() {
        return infected;
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

    public void setInfected(int infected) {
        this.infected = infected;
    }

    public Object[] toArray() {
        Graph get = alHistory.get(alHistory.size()-1);
        String[] temp = new String[4];
        temp[0] = "Healthy: " + get.getHealthy();
        temp[1] = "Simulitised: " + get.getInfected();
        temp[2] = "Cured: " + get.getCured();
        temp[3] = "Dead: " + get.getDead();

        return temp;
    }
    
    public void reset() {
        Graph graph = new Graph();
        graph.setCured(cured);
        graph.setDead(dead);
        graph.setHealthy(healthy);
        graph.setInfected(infected);
        alHistory.add(graph);
        cured = 0;
        dead = 0;
        healthy = 0;
        infected = 0;
    }
    
    public void draw(Graphics g, int width, int height) {
        if (alHistory == null || alHistory.isEmpty()) {
            return;
        }

        Graph latest = alHistory.get(alHistory.size() - 1);
        double total = latest.getCured() + latest.getDead() + latest.getHealthy() + latest.getInfected();
        if (total == 0) {
            return;
        }

        double ppp = (double) height / total;

        int startIndex = Math.max(0, alHistory.size() - width);

        int x = 0;
        for (int i = startIndex; i < alHistory.size(); i++, x++) {
            Graph graph = alHistory.get(i);

            int yDead = (int) (graph.getDead() * ppp);
            int yHealthy = (int) (graph.getHealthy() * ppp);
            int yCured = (int) (graph.getCured() * ppp);
            int ySimulitised = (int) (graph.getInfected() * ppp);

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
