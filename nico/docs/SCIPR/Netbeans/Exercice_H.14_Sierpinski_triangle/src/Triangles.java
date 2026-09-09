
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
public class Triangles {
    
    private int depth = 0;
    private int count = 0;
    
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    public int getCount() {
        return count;
    }
    
    public int calculate() {
        return depth == 0 ? 1 : (int) Math.pow(3, depth);
    }
    
    public void draw(Graphics g, int width, int height) {
        count = 0;
        g.setColor(Color.BLUE);
        //drawTriangles(g, 0, height, width / 2, 0, width, height, depth);
        
        //        2
        //       / \
        //      /   \
        //     /     \
        //    1 ----- 3
        
        int side = Math.min(width, height);
        int h = (int) (Math.sqrt(3) / 2 * side);
        int x1 = (width - side) / 2;
        int y1 = height - (height - h) / 2;
        int x2 = width / 2;
        int y2 = (height - h) / 2;
        int x3 = x1 + side;
        int y3 = y1;
        drawTriangles(g, x1, y1, x2, y2, x3, y3, depth);
        g.drawString("Counted: " + count, 20, 20);
        g.drawString("Calculated: " + calculate(), 20, 40);
    }
    
    // Fisch: ArrayList mat Polygons
    
    private void drawTriangles(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3, int depth) {
        if (depth == 0) {
            int[] xPoints = { x1, x2, x3 };
            int[] yPoints = { y1, y2, y3 };
            g.fillPolygon(xPoints, yPoints, 3);
            count = 1;
        } else {
            // points in the middle
            int midX1 = (x1 + x2) / 2;
            int midY1 = (y1 + y2) / 2;
            int midX2 = (x2 + x3) / 2;
            int midY2 = (y2 + y3) / 2;
            int midX3 = (x3 + x1) / 2;
            int midY3 = (y3 + y1) / 2;
            // 3 smaller triangles
            drawTriangles(g, x1, y1, midX1, midY1, midX3, midY3, depth - 1);
            drawTriangles(g, midX1, midY1, x2, y2, midX2, midY2, depth - 1);
            drawTriangles(g, midX3, midY3, midX2, midY2, x3, y3, depth - 1);
            count *= 3;
        }
    }
}
