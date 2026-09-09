
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
public class Printer {
    
    private String name;
    private boolean printing;
    private int angle;
    private PrintObject printObject;

    public Printer(String name, boolean printing) {
        this.name = name;
        this.printing = printing;
    }

    public void setPrinting(boolean printing) {
        this.printing = printing;
    }

    public boolean isPrinting() {
        return printing;
    }

    public void setPrintObject(PrintObject printObject) {
        this.printObject = printObject;
    }

    public PrintObject getPrintObject() {
        return printObject;
    }
    
    public void rotateCross() {
        if (angle >= 180) {
            this.angle = 0;
        } else {
            this.angle += 1;
        }
    }
    
    public void draw(Graphics g, Point topLeft, int printerWidth, int printerHeight) {
        g.setColor(Color.BLACK);
        g.drawRect(topLeft.x, topLeft.y, printerWidth, printerHeight);
        g.drawString(name, topLeft.x + 3, topLeft.y + 12);
        int x = topLeft.x + (int) (printerWidth / 2);
        int y = topLeft.y + (int) (printerHeight / 2);
        Point center = new Point(x, y);
        int radius = Math.min(printerWidth, printerHeight) / 2 - Math.min(printerWidth, printerHeight) / 8;
        g.setColor(Color.RED);
        g.drawOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
        if (printing) {
            // draw 
            double radian1 = Math.toRadians(angle);                     // --- ChatGPT ---
            double radian2 = radian1 + Math.PI / 2;                     //        |
            int x1 = center.x + (int) (Math.cos(radian1) * radius);     //        |
            int y1 = center.y + (int) (Math.sin(radian1) * radius);     //        |
            int x2 = center.x - (int) (Math.cos(radian1) * radius);     //        |
            int y2 = center.y - (int) (Math.sin(radian1) * radius);     //        |
            int x3 = center.x + (int) (Math.cos(radian2) * radius);     //        |
            int y3 = center.y + (int) (Math.sin(radian2) * radius);     //        |
            int x4 = center.x - (int) (Math.cos(radian2) * radius);     //        |
            int y4 = center.y - (int) (Math.sin(radian2) * radius);     //        |
            g.drawLine(x1, y1, x2, y2);                                 //        |
            g.drawLine(x3, y3, x4, y4);                                 // --- ChatGPT --- 
            // draw status bar
            Color color = new Color(255, 0, 0, 96);
            g.setColor(color);
            int percent = (int) (printerWidth - printerWidth * printObject.getTime() / printObject.getTotalTime());
            g.fillRect(topLeft.x, topLeft.y + printerHeight - 15, percent, 15);
            // draw object title
            g.setColor(Color.BLUE);
            g.drawString(printObject.getTitle(), topLeft.x + 3, topLeft.y + printerHeight - 3);
        }
    }
}
