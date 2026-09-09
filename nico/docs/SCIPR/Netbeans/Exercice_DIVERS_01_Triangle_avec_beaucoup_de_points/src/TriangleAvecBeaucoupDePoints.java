
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class TriangleAvecBeaucoupDePoints {
    
    private ArrayList<Point> alPoints = new ArrayList<>();
    
    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        
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
        int[] xPoints = { x1, x2, x3 };
        int[] yPoints = { y1, y2, y3 };
        Polygon triangle = new Polygon(xPoints, yPoints, 3);
        g.drawPolygon(triangle);
        alPoints.clear();
        Point randomPoint = new Point(0, 0);
        do {
            randomPoint.x = (int) (Math.random() * width);
            randomPoint.y = (int) (Math.random() * height);
        } while (triangle.contains(randomPoint));
        alPoints.add(randomPoint);
        for (int i = 0; i < 100000; i++) {
            Point randomEdge = switch ((int) (Math.random() * 3) + 1) {
                case 1 -> new Point(x1, y1);
                case 2 -> new Point(x2, y2);
                case 3 -> new Point(x3, y3);
                default -> null;
            };
            double dx = randomEdge.x - randomPoint.x;
            double dy = randomEdge.y - randomPoint.y;
            Point newPoint = new Point((int) (randomPoint.x + dx / 2), (int) (randomPoint.y + dy / 2));
            alPoints.add(newPoint);
            randomPoint = newPoint;
        }
        g.setColor(Color.BLUE);
        for (int i = 0; i < alPoints.size(); i++) {
            Point point = alPoints.get(i);
            g.fillOval(point.x - 1, point.y - 1, 2, 2);
        }
    }
}
