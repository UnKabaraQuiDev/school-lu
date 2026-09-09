
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
public class Line {
    
    private int fx;
    private int fy;
    private int tx;
    private int ty;

    public Line(int fx, int fy, int tx, int ty) {
        this.fx = fx;
        this.fy = fy;
        this.tx = tx;
        this.ty = ty;
    }
    
    public void draw(Graphics g) {
        g.drawLine(fx, fy, tx, ty);
    }
}
