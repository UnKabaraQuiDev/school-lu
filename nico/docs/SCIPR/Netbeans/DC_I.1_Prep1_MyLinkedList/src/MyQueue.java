
import java.util.EmptyStackException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class MyQueue {
    
    private Node root;
    private Node last;
    private int size;
    
    public void push(int data) {
        Node n = new Node(data);
        if (size == 0) {
            root = n;
            last = n;
        } else {
            last.setNext(n);
            last = n;
        }
        size++;
    }
    
    public int pop() {
        if (size == 0)
            throw new EmptyStackException();
        Node ret = root;
        root = root.getNext();
        size--;
        return ret.getData();
    }
    
    public int size() {
        return size;
    }
    
    public static void main(String[] args) {
        MyQueue mq = new MyQueue();
        
        mq.push(1);
        mq.push(2);
        mq.push(3);
        
        System.out.println(mq.pop());
        System.out.println(mq.pop());
        System.out.println(mq.pop());
    }
}
