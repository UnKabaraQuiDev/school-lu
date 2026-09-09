
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Simulitis {
    
    private ArrayList<Person> alPersons = new ArrayList<>();
    
    private Graph graph = null;
    private int time = 0;
    private int sickTime = 100;

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
    
    public void add(int width, int height) {
        int radius = getRandomInteger(10, 30);
        int infectionTime = 0;
        double x = getRandomDouble(0.0, (double) (width - 2 * radius));
        double y = getRandomDouble(0.0, (double) (height - 2 * radius));

        double xStep = getRandomDouble(-3.0, 3.0);
        double yStep = getRandomDouble(-3.0, 3.0);

        Color color = Color.BLUE;
        if (getRandomInteger(0, 99) < 3) {
            color = Color.RED;
            infectionTime = time+1;
        }

        alPersons.add(new Person(x, y, 10, xStep, yStep, color, infectionTime));
    }

    public void add2(int width, int height, int amount) {
        for (int i = 0; i < amount; i++) {
            int radius = 10;

            double x = getRandomDouble(0.0, (double) (width - 2 * radius));
            double y = getRandomDouble(0.0, (double) (height - 2 * radius));
            
            int random = getRandomInteger(1, 5);
            
            double xStep = 0;
            double yStep = 0;
            
            if (random <= 2) {
                xStep = getRandomDouble(-3.0, 3.0);
                yStep = getRandomDouble(-3.0, 3.0);
            }
            
            Person person = new Person(x, y, radius, xStep, yStep, Color.GREEN, 0);
            
            alPersons.add(person);
            
//            for (int j = 0; j < alMovingBalls.size(); j++) {
//                MovingBall movingBall2 = alMovingBalls.get(j);
//                
//                if (movingBall.collidesWith(movingBall2)) {
//                    i--;
//                    alMovingBalls.remove(movingBall);
//                } else {
//                    
//                }
//            }
        }
    }
    
    private double getRandomDouble(double min, double max) {
        return Math.random() * (max - min + 1) + min;
    }
    
    private int getRandomInteger(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public void remove(int index) {
        if (!alPersons.isEmpty()) {
            alPersons.remove(index);
        }
    }

    public void remove(Person person) {
        alPersons.remove(person);
    }

    public void clear() {
        alPersons.clear();
    }
    
    public void doStep(int width, int height) {
        time++;
        for (int i = 0; i < alPersons.size(); i++) {
            Person person = alPersons.get(i);
            person.doStep(width, height);
        }
        spreadSimultitis();
        letPeopleDie();
        //dostats
        int cured = 0;
        int simulitised = 0;
        int dead = 0;
        int healthy = 0;
        
        for (int i = 0; i < alPersons.size(); i++) {
            Person get = alPersons.get(i);
            if(get.getColor() == Color.BLUE) healthy++;
            else if(get.getColor() == Color.RED) simulitised++;
            else if(get.getColor() == Color.GRAY) dead++;
            else cured ++;
        }
        
        graph.newCycle(healthy, cured, dead, simulitised);
    }
    
    public void doStep2(int width, int height) {
        for (int i = 0; i < alPersons.size(); i++) {
            Person person = alPersons.get(i);
            person.doStep(width, height);
            
            for (int j = 0; j < alPersons.size(); j++) {
                Person person2 = alPersons.get(j);
                
                if (person == person2) continue;
                
                if (person.collidesWith(person2)) {
                    person.setxStep(person.getxStep() * -1);
                    person.setyStep(person.getyStep() * -1);
                    person2.setxStep(person2.getxStep() * -1);
                    person2.setyStep(person2.getyStep() * -1);
                }
            }
        }
    }
    
    public void isInside(Point point) {
        for (int i = 0; i < alPersons.size(); i++) {
            Person person = alPersons.get(i);
            if (person.isInside(point)) {
                person.setState(1);
            }
        }
    }
    
    public void draw(Graphics g) {
        for (int i = 0; i < alPersons.size(); i++) {
            Person person = alPersons.get(i);
            person.draw(g);
            g.setColor(Color.RED);
        }
    }
    
    public void spreadSimultitis() {
        for (int i = 0; i < alPersons.size(); i++) {
            Person personA = alPersons.get(i);
            for (int j = i + 1; j < alPersons.size(); j++) {
                Person personB = alPersons.get(j);

                if (personA.isTouching(personB)) {
                    personA.setxStep(personA.getxStep()*-1);
                    personA.setyStep(personA.getyStep()*-1);
                    personB.setxStep(personB.getxStep()*-1);
                    personB.setyStep(personB.getyStep()*-1);
                    if (personA.getColor() == Color.RED && personB.getColor() == Color.BLUE) {
                        personB.setColor(Color.RED);
                        personB.setInfactionTime(time);
                    } else if (personB.getColor() == Color.RED && personA.getColor() == Color.BLUE) {
                        personA.setColor(Color.RED);
                        personA.setInfactionTime(time);
                    }
                }
            }
        }
    }

    private void letPeopleDie() {
        for (int i = 0; i < alPersons.size(); i++) {
            Person get = alPersons.get(i);
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
