
import java.awt.Color;
import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class KochCurve {
    
    private int depth = 0;
    private int angle = 0;      // Erweiderung vum Proff
    
    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
    
    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.BLACK); 
        drawCurve(g, depth, 0, height * 4/5, width, height * 4/5);
    }
    
    public void drawCurve(Graphics g, int depth, double x1, double y1, double x2, double y2) {
        if (depth == 0) {
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        } else {
            
            //        B
            //       / \
            //      /   \
            //     /     \
            //    A       C
            
            double xA = x1 + (x2 - x1) / 3;
            double yA = y1 + (y2 - y1) / 3;
            double xC = x1 + (x2 - x1) / 3 * 2;
            double yC = y1 + (y2 - y1) / 3 * 2;
            double dx = xC - xA;
            double dy = yC - yA;
            double angle = Math.toRadians(this.angle - 60);
            double xB = xA + dx * Math.cos(angle) - dy * Math.sin(angle);
            double yB = yA + dx * Math.sin(angle) + dy * Math.cos(angle);
            drawCurve(g, depth - 1, x1, y1, xA, yA);
            drawCurve(g, depth - 1, xA, yA, xB, yB);
            drawCurve(g, depth - 1, xB, yB, xC, yC);
            drawCurve(g, depth - 1, xC, yC, x2, y2);
        }
    }
}
