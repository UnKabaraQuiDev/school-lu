/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Node {
    
    private PrintObject printObject;
    private Node next;

    public Node(PrintObject printObject) {
        this.printObject = printObject;
    }
    
    public void setPrintObject(PrintObject printObject) {
        this.printObject = printObject;
    }

    public PrintObject getPrintObject() {
        return printObject;
    }
    
    public void setNext(Node next) {
        this.next = next;
    }

    public Node getNext() {
        return next;
    }
}
