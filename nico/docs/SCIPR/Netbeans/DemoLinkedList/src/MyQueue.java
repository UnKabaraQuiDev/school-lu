
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
    
    private Node root = null;
    private Node last = null;
    private int size = 0;
    
    public void push(int data) {    // Komplexitéit: O(1)
        Node n = new Node(data);
        if (root == null) {
            root = n;
            last = n;
        } else {
            last.setNext(n);
            last = n;
        }
        size++;
    }
    
    public int pop() {     // Komplexitéit: O(1)
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
    
//    public static void main(String[] args) {
//        MyQueue mq = new MyQueue();
//        mq.push(1);
//        mq.push(2);
//        mq.push(3);
//        System.out.println(mq.pop());
//        System.out.println(mq.pop());
//        System.out.println(mq.pop());
//        System.out.println(mq.pop());
//    }
}
