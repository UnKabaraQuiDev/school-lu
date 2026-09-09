
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
public class Cell {
    
    private static final int STATE_EMPTY = 0;
    private static final int STATE_SOLID = 1;
    private static final int STATE_VISITED = 2;
    
    private int state = STATE_EMPTY;
    
    private int gridX;
    private int gridY;
    private int x;
    private int y;
    private int width;
    private int height;

    public Cell(int gridX, int gridY, int width, int height) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.x = gridX * width;
        this.y = gridY * height;
        this.width = width;
        this.height = height;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
    
    public void draw(Graphics g) {
        switch (state) {
            case STATE_EMPTY -> {
                g.setColor(Color.WHITE);
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
            }
            case STATE_SOLID -> {
                g.setColor(Color.BLACK);
                g.fillRect(x, y, width, height);
            }
            case STATE_VISITED -> {
                g.setColor(Color.BLACK);
                g.fillRect(x, y, width, height);
            }
            default -> {
            }
        }
    }

    @Override
    public String toString() {
        return "> " + x + ":" + y + " ";
    }
}
