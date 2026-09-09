package model;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Line extends Element {
    
    public Line(Point start, Point end, Color outlineColor, Color fillColor) {
        super(start, end, outlineColor, fillColor);
    }
    
    public Line(int x1, int y1, int x2, int y2, Color outlineColor, Color fillColor) {
        super(x1, y1, x2, y2, outlineColor, fillColor);
    }
    
    @Override
    public boolean contains(Point p) {
        double distance = pointToLineDistance(p, start, end);
        return distance <= 5;   // 5px tolerance
    }
    
    private double pointToLineDistance(Point p, Point a, Point b) {
        double A = p.x - a.x;
        double B = p.y - a.y;
        double C = b.x - a.x;
        double D = b.y - a.y;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = lenSq != 0 ? dot / lenSq : -1;

        double xx, yy;
        if (param < 0) {
            xx = a.x;
            yy = a.y;
        } else if (param > 1) {
            xx = b.x;
            yy = b.y;
        } else {
            xx = a.x + param * C;
            yy = a.y + param * D;
        }

        double dx = p.x - xx;
        double dy = p.y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(outlineColor);
        g2.drawLine(start.x, start.y, end.x, end.y);
    }
}
