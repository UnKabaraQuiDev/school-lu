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
public class Ellipse extends Element {
    
    public Ellipse(Point start, Point end, Color outlineColor, Color fillColor) {
        super(start, end, outlineColor, fillColor);
    }
    
    public Ellipse(int x1, int y1, int x2, int y2, Color outlineColor, Color fillColor) {
        super(x1, y1, x2, y2, outlineColor, fillColor);
    }
    
    @Override
    public boolean contains(Point p) {
        int cx = Math.min(start.x, end.x) + Math.abs(start.x - end.x) / 2;
        int cy = Math.min(start.y, end.y) + Math.abs(start.y - end.y) / 2;
        int rx = Math.abs(start.x - end.x) / 2;
        int ry = Math.abs(start.y - end.y) / 2;

        if (rx == 0 || ry == 0) {
            return false;
        }
        double dx = (p.x - cx) / (double) rx;
        double dy = (p.y - cy) / (double) ry;
        return (dx * dx + dy * dy) <= 1.0;
    }
    
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int w = Math.abs(start.x - end.x);
        int h = Math.abs(start.y - end.y);
        g2.setColor(fillColor);
        g2.fillOval(x, y, w, h);
        g2.setColor(outlineColor);
        g2.drawOval(x, y, w, h);
    }
}
