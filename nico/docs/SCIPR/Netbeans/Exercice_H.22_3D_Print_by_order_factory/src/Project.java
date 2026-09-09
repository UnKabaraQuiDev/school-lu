
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.EmptyStackException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Project {
    
    private Node root = null;
    private Node last = null;
    private int size = 0;
    
    private ArrayList<Printer> alPrinters = new ArrayList<>();
    
    public void pushPrintObject(PrintObject printObject) {
        Node n = new Node(printObject);
        if (size == 0) {
            root = n;
            last = n;
        } else {
            last.setNext(n);
            last = n;
        }
        size++;
    }
    
    public PrintObject popPrintObject() {
        if (size == 0) {
            throw new EmptyStackException();
        } else {
            Node ret = root;
            root = root.getNext();
            size--;
            return ret.getPrintObject();
        }
    }
    
    public Object[] toArray() {
        PrintObject[] printObjects = new PrintObject[size];
        Node tmp = root;
        for (int i = 0; i < size; i++) {
            printObjects[i] = tmp.getPrintObject();
            tmp = tmp.getNext();
        }
        return printObjects;
    }
    
    public void reduceTime(double time) {
        for (int i = 0; i < alPrinters.size(); i++) {
            Printer printer = alPrinters.get(i);
            if (printer.getPrintObject() != null) {
                printer.getPrintObject().setTime(printer.getPrintObject().getTime() - time);
                if (printer.getPrintObject().getTime() <= 0) {
                    printer.setPrintObject(null);
                    printer.setPrinting(false);
                }
            }
        }
    }
    
    public void startNewPrint() {
        if (size > 0) {
            for (int i = 0; i < alPrinters.size(); i++) {
                Printer printer = alPrinters.get(i);
                if (printer.getPrintObject() == null) {
                    printer.setPrintObject(popPrintObject());
                    printer.setPrinting(true);
                    break;
                }
            }
        }
    }
    
    public void addPrinter(Printer printer) {
        alPrinters.add(printer);
    }
    
    public void removePrinter(int index) {
        alPrinters.remove(index);
    }
    
    public int size() {
        return alPrinters.size();
    }
    
    public void rotatePrinters() {
        for (int i = 0; i < alPrinters.size(); i++) {
            Printer printer = alPrinters.get(i);
            if (printer.isPrinting()) printer.rotateCross();
        }
    }
    
    public void draw(Graphics g, int width, int height) {
        int rows;
        int cols;
        int remaining;
        if (width > height) {
            rows = (int) Math.sqrt(alPrinters.size());
            cols = alPrinters.size() / rows;
            remaining = alPrinters.size() % rows;
        } else {
            cols = (int) Math.sqrt(alPrinters.size());
            rows = alPrinters.size() / cols;
            remaining = alPrinters.size() % cols;
        }
        int printerWidth = width * 2/3 / cols;
        int printerHeight = height * 2/3 / (rows + (remaining == 0 ? 0 : 1));
        int horizontalSpace = width * 1/3 / (cols + 1);
        int verticalSpace = height * 1/3 / (rows + 1 + (remaining == 0 ? 0 : 1));
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * (printerWidth + horizontalSpace) + horizontalSpace;
                int y = i * (printerHeight + verticalSpace) + verticalSpace;
                Point topLeft = new Point(x, y);
                alPrinters.get(index++).draw(g, topLeft, printerWidth, printerHeight);
            }
        }
        for (int i = 0; i < remaining; i++) {
            int x = i * (printerWidth + horizontalSpace) + horizontalSpace;
            int y = rows * (printerHeight + verticalSpace) + verticalSpace;
            Point topLeft = new Point(x, y);
            alPrinters.get(i).draw(g, topLeft, printerWidth, printerHeight);
        }
    }
}
