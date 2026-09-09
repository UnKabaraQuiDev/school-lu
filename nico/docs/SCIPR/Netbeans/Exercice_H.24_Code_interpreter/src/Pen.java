
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
public class Pen {
    
    private Lines lines;    // list of lines
    private Color color = Color.YELLOW;
    private double x;       // current position
    private double y;
    private int angle;      // in degrees
    private boolean down;   // pencil placed on canvas or not
    
    public void draw(Graphics g) {
        g.setColor(color);
        lines.draw(g);
    }
    
    private void execute(Instruction inst) {
        if (inst == null) return;
        
        if (null != inst.getCmd().toLowerCase()) switch (inst.getCmd().toLowerCase()) {
            case "down" -> down = true;
            case "up" -> down = false;
            case "move" -> {
                double newX = x + inst.getParam() * Math.cos(angle);
                double newY = y + inst.getParam() * Math.sin(angle);
                if (down) {
                    lines.add(new Line((int) x, (int) y, (int) newX, (int) newY));
                }
                x = newX;
                y = newY;
            }
            case "rotate" -> angle += inst.getParam();
            case "loop" -> {
                for (int i = 0; i < inst.getParam(); i++) {
                    if (inst.getSub() != null) {
                        execute(inst.getSub());
                    }
                }
            }
            default -> {
            }
        }
        
        if (inst.getNext() != null) {
            execute(inst.getNext());
        }
    }
    
    public void execute(Program program, int x, int y, int angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        if (program.getStart() != null) {
            execute(program.getStart());
        }
    }
}
