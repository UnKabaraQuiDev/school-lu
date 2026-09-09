
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class LineComparableDEMO implements Comparable<LineComparableDEMO> {
    
    private int fx;
    private int fy;
    private int tx;
    private int ty;

    public LineComparableDEMO(int fx, int fy, int tx, int ty) {
        this.fx = fx;
        this.fy = fy;
        this.tx = tx;
        this.ty = ty;
    }
    
    public void draw(Graphics g) {
        g.drawLine(fx, fy, tx, ty);
    }
    
    public double getLength() {
        return Math.sqrt(Math.pow(tx - fx, 2) + Math.pow(ty - fy, 2));
    }
    
    @Override
    public int compareTo(LineComparableDEMO other) {
        return Double.compare(getLength(), other.getLength());
    }
    
    public static void main(String[] args) {
        ArrayList<LineComparableDEMO> lines = new ArrayList<>();
        Collections.sort(lines);
    }
}
