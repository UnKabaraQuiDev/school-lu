
import java.awt.Color;
import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author nicol
 */
public class MovingBall {

    private double x;
    private double y;
    private int radius = 5;
    private double xStep = 0.0;
    private double yStep = 0.0;
    private Color color = Color.BLUE;
    private int infactionTime;

    public MovingBall(double x, double y, double xStep, double yStep, Color color, int infectionTime) {
        this.x = x;
        this.y = y;
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

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int) x, (int) y, 2 * radius, 2 * radius);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isTouching(MovingBall movingBall) {
        // Compute next positions including their steps
        double nextX1 = this.x + getxStep();
        double nextY1 = this.y + getyStep();
        double nextX2 = movingBall.getX() + movingBall.getxStep();
        double nextY2 = movingBall.getY() + movingBall.getyStep();

        // Calculate distance between centers
        double dx = nextX1 - nextX2;
        double dy = nextY1 - nextY2;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Check if distance is less than sum of radii
        return distance < radius*2;
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

    public void setxStep(double xStep) {
        this.xStep = xStep;
    }

    public void setyStep(double yStep) {
        this.yStep = yStep;
    }

}
