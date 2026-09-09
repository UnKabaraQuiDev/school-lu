
import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public abstract class Figure {
    
    protected int x;
    protected int y;
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public abstract void draw(Graphics g);
}
