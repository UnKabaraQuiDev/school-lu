package model;


import com.google.gson.Gson;
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
public abstract class Element {
    
    protected Point start;
    protected Point end;
    protected transient Color outlineColor;
    protected transient Color fillColor;
    private transient boolean selected = false;
    
    private String type;
    private int outlineRGB, fillRGB;
    
    public Element(Point start, Point end, Color outlineColor, Color fillColor) {
        this.start = start;
        this.end = end;
        this.outlineColor = outlineColor;
        this.fillColor = fillColor;
        
        this.type = this.getClass().getSimpleName();
        this.outlineRGB = outlineColor.getRGB();
        this.fillRGB = fillColor.getRGB();
    }
    
    public Element(int x1, int y1, int x2, int y2, Color outlineColor, Color fillColor) {
        this.start = new Point(x1, y1);
        this.end = new Point(x2, y2);
        this.outlineColor = outlineColor;
        this.fillColor = fillColor;
        
        this.type = this.getClass().getSimpleName();
        this.outlineRGB = outlineColor.getRGB();
        this.fillRGB = fillColor.getRGB();
    }
    
    public void setStart(Point start) {
        this.start = start;
    }
    
    public void setEnd(Point end) {
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public Color getFillColor() {
        return fillColor;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean contains(Point p) {
        return false;
    }
    
    public void scale(double factor, Point center) {
        int newX1 = (int) (center.x + (start.x - center.x) * factor);
        int newY1 = (int) (center.y + (start.y - center.y) * factor);

        int newX2 = (int) (center.x + (end.x - center.x) * factor);
        int newY2 = (int) (center.y + (end.y - center.y) * factor);

        setStart(new Point(newX1, newY1));
        setEnd(new Point(newX2, newY2));
    }
    
    public abstract void draw(Graphics g);
    
    // for .json

    public String getType() {
        return type;
    }

    public int getOutlineRGB() {
        return outlineRGB;
    }

    public int getFillRGB() {
        return fillRGB;
    }
}
