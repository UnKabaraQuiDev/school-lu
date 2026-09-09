
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
public class SierpinskiCurve {
    
    private int depth = 0;
    
    /*private ArrayList<Segment> base = new ArrayList<>();
    private ArrayList<Segment> alSegments = getSegments(new Segment(0, ), depth);*/
    
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    /*private ArrayList<Segment> getSegments(Segment base, int depth) {
        if (depth == 0) return base;
        
        int midX1 = base.getX1()
        int midY1 = 
        int midX2 = 0;
        int midY2 = 0;
        int midX3 = 0;
        int midY3 = 0;
        
        base.get(depth)
        
        ArrayList<Segment> result = new ArrayList<Segment>();
        return result;
    }*/
    
    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        //drawCurve(g, depth, 0, height * 4/5, width, height * 4/5, -60);
        
        //        2
        //       / \
        //      /   \
        //     /     \
        //    1 ----- 3
        
        int side = Math.min(width, height);
        int h = (int) (Math.sqrt(3) / 2 * side);
        int x1 = (width - side) / 2;
        int y1 = height - (height - h) / 2;
        //int x2 = width / 2;
        //int y2 = (height - h) / 2;
        int x3 = x1 + side;
        int y3 = y1;
        drawCurve(g, depth, x1, y1, x3, y3, -60);
    }
    
    public void drawCurve(Graphics g, int depth, double x1, double y1, double x2, double y2, int angle) {
        if (depth == 0) {
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        } else {
            
            //        B ----- C
            //       / \     / \
            //      /   \   /   \
            //     /     \ /     \
            //    A               D
            
            double xA = x1;
            double yA = y1;
            double xD = x2;
            double yD = y2;
            double dx = (xD - xA) / 2.0;
            double dy = (yD - yA) / 2.0;
            double rad = Math.toRadians(angle);
            double xB = xA + dx * Math.cos(rad) - dy * Math.sin(rad);
            double yB = yA + dx * Math.sin(rad) + dy * Math.cos(rad);
            double xC = xD + dx * Math.cos(Math.PI - rad)
                           - dy * Math.sin(Math.PI - rad);
            double yC = yD + dx * Math.sin(Math.PI - rad)
                           + dy * Math.cos(Math.PI - rad);
            drawCurve(g, depth - 1, xA, yA, xB, yB, -angle);
            drawCurve(g, depth - 1, xB, yB, xC, yC, +angle);
            drawCurve(g, depth - 1, xC, yC, xD, yD, -angle);
        }
    }
}
