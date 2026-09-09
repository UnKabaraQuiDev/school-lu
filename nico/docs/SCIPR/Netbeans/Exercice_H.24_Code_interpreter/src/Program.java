
import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Program {
    
    private Instruction start;
    
    public void draw(Graphics g, int x, int y) {
        if (start != null) {
            start.draw(g, x, y);
        }
    }
    
    private Instruction parseLine(String line) {
        line = line.trim();
        if (!line.isEmpty()) return null;
        if (!line.contains(" ")) line += " 0";
        
        return null; // temp
    }
    
    public void fromCode(String code) {
        if (code.trim().isEmpty()) {
            start = null;
            return;
        }
        
        String[] lines = code.split("\n");
        
    }
    
    public Instruction getStart() {
        return start;
    }
    
    public void saveToFile(String filename) {
        
    }
    
    public String toCode() {
        return "";
    }
    
    public void loadFromFile(String filename) {
        
    }
}
