
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
public class Maze {
    
    private int cols;
    private int rows;
    
    private Cell[][] cells;
    
    private ArrayList<Path> alPaths = new ArrayList<>();
    
    public Maze(int cols, int rows, int width, int height) {
        this.cols = cols;
        this.rows = rows;
        cells = new Cell[cols][rows];
        int cellWidth = (width - 1) / cols;
        int cellHeight = (height - 1) / rows;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                cells[i][j] = new Cell(i, j, cellWidth, cellHeight);
            }
        }
    }
    
    public void draw(Graphics g, int width, int height) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                cells[i][j].draw(g);
            }
        }
    }
    
    public void toggleCellState(Point mouse, int width, int height) {
        int colIndex = -1;
        int rowIndex = -1;
        for (int i = 0; i < cols; i++) {
            if (mouse.x > width / cols * i && mouse.x < width / cols * (i+1)) {
                colIndex = i;
                break;
            }
        }
        for (int i = 0; i < rows; i++) {
            if (mouse.y > height / rows * i && mouse.y < height / rows * (i+1)) {
                rowIndex = i;
                break;
            }
        }
        if (colIndex == -1 || rowIndex == -1) return;
        if (cells[colIndex][rowIndex].getState() == 0) cells[colIndex][rowIndex].setState(1);
        else if (cells[colIndex][rowIndex].getState() == 1) cells[colIndex][rowIndex].setState(0);
    }
    
    public void fill(int percentage) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                cells[i][j].setState(0);
            }
        }
        int total = cols * rows;
        int amount = (int) (total * percentage / 100.0);
        int counter = 0;
        while (counter < amount) {
            int i = (int) (Math.random() * cols);
            int j = (int) (Math.random() * rows);
            if (i == 0 && j == 0 || i == cols - 1 && j == rows - 1) {
                continue;
            } else if (cells[i][j].getState() == 0) {
                cells[i][j].setState(1);
                counter++;
            }
        }
    }
    
    public Cell getCell(int x, int y) {
        if (x >= 0 && x < cols && y >= 0 && y < rows) {
            return cells[x][y];
        }
        return null;
    }
    
    private int getRandomInteger(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
    
    public void solve() {
        alPaths.clear();

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (cells[i][j].getState() != 1) {
                    cells[i][j].setState(0);
                }
            }
        }

        Path path = new Path();
        solveRecursive(0, 0, path);
    }
    
    private void solveRecursive(int x, int y, Path path) {
    if (x < 0 || x >= cols || y < 0 || y >= rows) {
        return;
    }
    Cell currentCell = cells[x][y];
    if (currentCell.getState() == 1) {
        return;
    }
    if (currentCell.getState() == 2) {
        return;
    }
    
    currentCell.setState(2);
    path.add(currentCell);
    
    if (x == cols - 1 && y == rows - 1) {
        alPaths.add(path.clone());
        return;
    }
    
    solveRecursive(x + 1, y, path);
    solveRecursive(x, y + 1, path);
    solveRecursive(x - 1, y, path);
    solveRecursive(x, y - 1, path);
    
    currentCell.setState(0);
    path.removeLast();
}

    public Object[] toArray() {
        return alPaths.toArray();
    }

    public boolean add(Path e) {
        return alPaths.add(e);
    }
}
