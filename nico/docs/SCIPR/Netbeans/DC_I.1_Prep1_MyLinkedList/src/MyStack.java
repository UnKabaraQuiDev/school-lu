
import java.util.EmptyStackException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class MyStack {
    
    private Node top;
    private int size;
    
    public void push(int data) {
        Node n = new Node(data);
        n.setNext(top);
        top = n;
        size++;
    }
    
    public int pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Node ret = top;
        top = top.getNext();
        size--;
        return ret.getData();
    }
    
    public int size() {
        return size;
    }
    
//    public static void main(String[] args) {
//        MyStack ms = new MyStack();
//        
//        ms.push(1);
//        ms.push(2);
//        ms.push(3);
//        
//        System.out.println(ms.pop());
//        System.out.println(ms.pop());
//        System.out.println(ms.pop());
//    }
}
