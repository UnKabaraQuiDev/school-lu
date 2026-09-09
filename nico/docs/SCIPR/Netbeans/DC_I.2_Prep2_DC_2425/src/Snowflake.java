
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Snowflake implements Comparable<Snowflake> {

    private int depth;
    private Point center;
    private int diameter;

    public Snowflake(int levels, Point center, int radius) {
        this.depth = levels;
        this.center = center;
        this.diameter = radius * 2;
    }
    
    public void draw(Graphics g) {
        draw(g, depth, center.x, center.y, diameter);
    }
    
    private void draw(Graphics g, int depth, double x, double y, double diameter) {
        if (depth > 0) {
            Color color = Color.WHITE;   // default
            if (getRandomInteger(1, 100) <= 5) {
                int red = getRandomInteger(200, 255);
                int green = getRandomInteger(200, 255);
                int blue = 200;
                color = new Color(red, green, blue);
                g.setColor(color);
                g.fillOval((int) (x - diameter / 2), (int) (y - diameter / 2), (int) diameter, (int) diameter);
            } else {
                g.setColor(color);
                g.drawOval((int) (x - diameter / 2), (int) (y - diameter / 2), (int) diameter, (int) diameter);
            }
            
            double radius = diameter / 2;
            double smallDiameter = diameter / 3;
            double angle = Math.toRadians(45);
            
            draw(g, depth - 1, x, y - radius, smallDiameter);     // N (nord)
            draw(g, depth - 1, x + radius, y, smallDiameter);     // E ...
            draw(g, depth - 1, x, y + radius, smallDiameter);     // S
            draw(g, depth - 1, x - radius, y, smallDiameter);     // W
            
            draw(g, depth - 1, x + Math.cos(angle) * radius, y - Math.sin(angle) * radius, smallDiameter);     // NE
            draw(g, depth - 1, x + Math.cos(angle) * radius, y + Math.sin(angle) * radius, smallDiameter);     // SE
            draw(g, depth - 1, x - Math.cos(angle) * radius, y - Math.sin(angle) * radius, smallDiameter);     // SW
            draw(g, depth - 1, x - Math.cos(angle) * radius, y + Math.sin(angle) * radius, smallDiameter);     // NW
            
            draw(g, depth - 1, x, y, smallDiameter);     // middle
        }
    }
    
    private int getRandomInteger(int min, int max) {    // added to avoid redundant code
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public int getDepth() {
        return depth;
    }

    public Point getCenter() {
        return center;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }
    
    @Override
    public int compareTo(Snowflake that) {
        return Integer.compare(that.depth, this.depth);
    }
}
