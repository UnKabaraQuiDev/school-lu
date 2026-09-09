
import java.awt.Graphics;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Lines {
    
    private ArrayList<Line> alLines = new ArrayList<>();

    public boolean add(Line line) {
        return alLines.add(line);
    }

    public void clear() {
        alLines.clear();
    }
    
    public void draw(Graphics g) {
        for (int i = 0; i < alLines.size(); i++) {
            Line line = alLines.get(i);
            line.draw(g);
        }
    }
}
