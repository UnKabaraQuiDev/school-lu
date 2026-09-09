
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Path implements Cloneable {
    private ArrayList<Cell> alCells = new ArrayList<>();
    
    public boolean add(Cell cell) {
        return alCells.add(cell);
    }
    
    public void removeLast() {
        if (!alCells.isEmpty()) {
            alCells.remove(alCells.size() - 1);
        }
    }
    
    @Override
    public Path clone() {
        Path newPath = new Path();
        for (Cell cell : alCells) {
            newPath.add(cell);
        }
        return newPath;
    }
    
    @Override
    public String toString() {
        if (alCells.isEmpty()) return "Empty path";
        StringBuilder sb = new StringBuilder();
        for (Cell cell : alCells) {
            sb.append(cell.toString());
        }
        return sb.toString();
    }
}