/*, 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package circlingcircles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author ben
 */
public class Circle {
    
    static class Line{
        int x1,y1,x2,y2;

        public Line(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        public boolean equals(Object o){
            return o instanceof Line l && x1 == l.x1 && x2 == l.x2 && y1 == l.y1 && y2 == l.y2;
        }
        @Override
        public int hashCode(){
            return Objects.hash(x1, y1, x2, y2);
        }
    }

    public static final int SPEED = 1;
    public static final float RADIUS_FACTOR = 3.5f;
    private static Point last;
    private static Set<Line> lines = new HashSet<>();
    public static final Random RAN = new Random();
            
    private double x, y, radius, speed;
    private double childAngle;

    private Circle child;
    
    public Circle(){
        childAngle = 0;
        child = null;
    }

    public Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = SPEED;
        childAngle = 0;
        child = null;
    }

    public void update() {
        childAngle += speed;
        setChildCoords();
        if(child != null){
            child.update();
        }
    }

    public void setChildCoords() {
        if (child != null) {
            var angle = Math.toRadians(childAngle);
            child.setX((int) (x+(radius + child.getRadius()) * Math.cos(angle)));
            child.setY((int) (y+(radius + child.getRadius()) * Math.sin(angle)));
            child.setChildCoords();
        }
    }

    public void draw(Graphics g2) {
        Graphics2D g = (Graphics2D) g2;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.GRAY);
        g.drawOval((int)(x - radius), (int)(y - radius), (int)(2 * radius), (int)(2 * radius));

        if (child == null) {
            if(last != null){
                lines.add(new Line((int)last.x, (int)last.y, (int)x, (int)y));
                drawLines(g);
            }
            last = new Point((int)x, (int)y);
        } else {
            child.draw(g);
        }
    }
    
    public void drawLines(Graphics2D g){
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(3));
        for(var l : lines){
            g.drawLine(l.x1, l.y1, l.x2, l.y2);
        }
    }

    public void clear(){
        lines.clear();
    }
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        if(child != null){
            child.setRadius(this.radius/3);
        }
    }

    public double getChildAngle() {
        return childAngle;
    }

    public void setChildAngle(double childAngle) {
        this.childAngle = childAngle;
    }

    public Circle getChild() {
        return child;
    }

    public void setChild(Circle childp) {
        if (child == null) {
            this.child = childp;
            this.child.setRadius(this.radius/RADIUS_FACTOR);
            this.child.speed = this.speed*RADIUS_FACTOR;

        } else {
            child.setChild(childp);
        }
        setChildCoords();
    }
    
    public void addChild(){
        setChild(new Circle());
    }

}
