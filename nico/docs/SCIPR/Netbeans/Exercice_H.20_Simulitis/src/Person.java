
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
public class Person {
    
    private double x = 0.0;
    private double y = 0.0;
    private int radius = 0;
    private double xStep = 0.0;
    private double yStep = 0.0;
    
    private Color color = Color.BLUE;
    private int infactionTime;
    
    private static final int HEALTHY = 0;
    private static final int INFECTED = 1;
    private static final int CURED = 2;
    private static final int DEAD = 3;
    
    private int state = 0;
    
    public Person(double x, double y, int radius, double xStep, double yStep, Color color, int infectionTime) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.xStep = xStep;
        this.yStep = yStep;
        this.color = color;
        this.infactionTime = infectionTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public double getxStep() {
        return xStep;
    }
    
    public double getyStep() {
        return yStep;
    }

    public int getState() {
        return state;
    }
    
    public void setxStep(double xStep) {
        this.xStep = xStep;
    }
    
    public void setyStep(double yStep) {
        this.yStep = yStep;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setInfactionTime(int infactionTime) {
        this.infactionTime = infactionTime;
    }

    public int getInfactionTime() {
        return infactionTime;
    }
    
    public void doStep(int width, int height) {
        if (x + 2 * radius >= width - 1) {
            xStep *= -1;
        } else if (x <= 0) {
            xStep = Math.abs(xStep);
        }
        x += xStep;

        if (y + 2 * radius >= height - 1) {
            yStep *= -1;
        } else if (y <= 0) {
            yStep = Math.abs(yStep);
        }
        y += yStep;
    }
    
//    public void doStep(int width, int height) {
//        if (x + 2 * radius >= width - 1) {
//            //xStep *= -1;
//            x = 0;
//        } else if(x <= 0) {
//            //xStep = Math.abs(xStep);
//            x = width - 2 * radius;
//        }
//        x += xStep;
//        
//        if (y + 2 * radius >= height - 1) {
//            //yStep *= -1;
//            y = 0;
//        } else if (y <= 0) {
//            //yStep = Math.abs(yStep);
//            y = height - 2 * radius;
//        }
//        y += yStep;
//    }
    
    public boolean collidesWith(Person other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        boolean result = distance < radius + other.getRadius();
        
        if (result) {
            if (this.getState() == HEALTHY && other.getState() == INFECTED) {
                this.setState(INFECTED);
            } else if (this.getState() == INFECTED && other.getState() == HEALTHY) {
                other.setState(INFECTED);
            }
        }
        
        return result;
    }
    
    public boolean isTouching(Person person) {
        // Compute next positions including their steps
        double nextX1 = this.x + getxStep();
        double nextY1 = this.y + getyStep();
        double nextX2 = person.getX() + person.getxStep();
        double nextY2 = person.getY() + person.getyStep();

        // Calculate distance between centers
        double dx = nextX1 - nextX2;
        double dy = nextY1 - nextY2;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Check if distance is less than sum of radii
        return distance < radius*2;
    }
    
    public boolean isInside(Point point) {
        double centerX = x + radius;
        double centerY = y + radius;
        double dx = point.x - centerX;
        double dy = point.y - centerY;
        return dx * dx + dy * dy <= radius * radius;
    }
    
    public void live() {
        
    }
    
    public void draw(Graphics g) {
        switch (state) {
            case HEALTHY -> g.setColor(Color.BLUE);
            case INFECTED -> g.setColor(Color.RED);
            case CURED -> g.setColor(Color.GREEN);
            case DEAD -> g.setColor(Color.GRAY);
            default -> {}
        }
        g.setColor(color);
        g.fillOval((int) x, (int) y, 2 * radius, 2 * radius);
    }
}
