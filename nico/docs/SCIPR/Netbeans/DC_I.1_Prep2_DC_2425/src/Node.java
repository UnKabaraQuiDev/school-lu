/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Node {
    
    private Node next;
    private Politician politician;
    
    public Node(Politician driver) {
        this.politician = driver;
    }
    
    public Node getNext() {
        return next;
    }
    
    public void setNext(Node next) {
        this.next = next;
    }
    
    public Politician getPolitician() {
        return politician;
    }
    
    public void setPolitician(Politician politician) {
        this.politician = politician;
    }
}
