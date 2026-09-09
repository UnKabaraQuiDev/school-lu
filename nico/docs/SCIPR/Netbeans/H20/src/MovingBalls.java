
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author nicol
 */
public class MovingBalls {

    int time = 0;
    int sickTime = 100;
    public Stats stats = null;

    public void setStats(Stats stats) {
        this.stats = stats;
    }
    

    private ArrayList<MovingBall> alMovingBalls = new ArrayList<>();

    public void add(int width, int height) {
        int radius = getRandomInteger(10, 30);
        int infectionTime = 0;
        double x = getRandomDouble(0.0, (double) (width - 2 * radius));
        double y = getRandomDouble(0.0, (double) (height - 2 * radius));

        double xStep = getRandomDouble(-5.0, 5.0);
        double yStep = getRandomDouble(-5.0, 5.0);

        Color color = Color.BLUE;
        if (getRandomInteger(0, 99) < 3) {
            color = Color.RED;
            infectionTime = time+1;
        }

        alMovingBalls.add(new MovingBall(x, y, xStep, yStep, color, infectionTime));
    }

    private double getRandomDouble(double min, double max) {
        return Math.random() * (max - min + 1) + min;
    }

    private int getRandomInteger(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public void remove(int index) {
        if (!alMovingBalls.isEmpty()) {
            alMovingBalls.remove(index);
        }
    }

    public void clear() {
        alMovingBalls.clear();
    }

    public void doStep(int width, int height) {
        time++;
        for (int i = 0; i < alMovingBalls.size(); i++) {
            MovingBall movingBall = alMovingBalls.get(i);
            movingBall.doStep(width, height);
        }
        spreadSimultitis();
        letPeopleDie();
        //dostats
        int cured = 0;
        int simulitised = 0;
        int dead = 0;
        int healthy = 0;
        
        for (int i = 0; i < alMovingBalls.size(); i++) {
            MovingBall get = alMovingBalls.get(i);
            if(get.getColor() == Color.BLUE) healthy++;
            else if(get.getColor() == Color.RED) simulitised++;
            else if(get.getColor() == Color.GRAY) dead++;
            else cured ++;
        }
        
        stats.newCycle(healthy, cured, dead, simulitised);
    }

    public void spreadSimultitis() {
        for (int i = 0; i < alMovingBalls.size(); i++) {
            MovingBall ballA = alMovingBalls.get(i);
            for (int j = i + 1; j < alMovingBalls.size(); j++) {
                MovingBall ballB = alMovingBalls.get(j);

                if (ballA.isTouching(ballB)) {
                    ballA.setxStep(ballA.getxStep()*-1);
                    ballA.setyStep(ballA.getyStep()*-1);
                    ballB.setxStep(ballB.getxStep()*-1);
                    ballB.setyStep(ballB.getyStep()*-1);
                    if (ballA.getColor() == Color.RED && ballB.getColor() == Color.BLUE) {
                        ballB.setColor(Color.RED);
                        ballB.setInfactionTime(time);
                    } else if (ballB.getColor() == Color.RED && ballA.getColor() == Color.BLUE) {
                        ballA.setColor(Color.RED);
                        ballA.setInfactionTime(time);
                    }
                }
            }
        }
    }

    public void draw(Graphics g) {
        for (int i = 0; i < alMovingBalls.size(); i++) {
            MovingBall movingBall = alMovingBalls.get(i);
            movingBall.draw(g);
            g.setColor(Color.RED);
            int x1 = 0;
            int y1 = 0;
            int x2 = 0;
            int y2 = 0;
            int radius1 = 0;
            int radius2 = 0;
//            if(i == 0) {
//                x1 = (int) movingBall.getX();
//                y1 = (int) movingBall.getY();
//                x2 = (int) alMovingBalls.get((alMovingBalls.size()) - 1).getX();
//                y2 = (int) alMovingBalls.get((alMovingBalls.size()) - 1).getY();
//                radius1 = movingBall.getRadius();
//                radius2 = alMovingBalls.get((alMovingBalls.size()) - 1).getRadius();

        
    

    ////                g.drawLine(x1 + radius1, y1 + radius1, x2 + radius2, y2 + radius2);
//            } else {
//                x1 = (int) movingBall.getX();
//                y1 = (int) movingBall.getY();
//                x2 = (int) alMovingBalls.get(i - 1).getX();
//                y2 = (int) alMovingBalls.get(i - 1).getY();
//                radius1 = movingBall.getRadius();
//                radius2 = alMovingBalls.get(i - 1).getRadius();
////                g.drawLine(x1 + radius1, y1 + radius1, x2 + radius2, y2 + radius2);
//            }
        }
    }

    private void letPeopleDie() {
        for (int i = 0; i < alMovingBalls.size(); i++) {
            MovingBall get = alMovingBalls.get(i);
            if (get.getInfactionTime() > 0 && time - get.getInfactionTime() > sickTime) {
                if (getRandomInteger(0, 99) < 2) {
                    get.setColor(Color.GRAY);
                    get.setInfactionTime(0);
                    get.setxStep(0);
                    get.setyStep(0);
                } else {
                    get.setColor(Color.PINK);
                    get.setInfactionTime(0);
                }
            }
        }
    }
}
