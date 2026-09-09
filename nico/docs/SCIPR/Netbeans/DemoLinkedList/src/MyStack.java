
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
    
    private Node root = null;
    private int size = 0;
    
    public void push(int data) {    // Komplexitéit: O(1)
        Node n = new Node(data);
        n.setNext(root);
        root = n;
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
//        MyStack ms = new MyStack();
//        ms.push(1);
//        ms.push(2);
//        ms.push(3);
//        System.out.println(ms.pop());
//        System.out.println(ms.pop());
//    }
}
