/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class MyList {
    
    private Node root;
    private Node last;
    private int size;
    
    public void add(int data) {
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
    
    public void add(int data, int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        } else if (index == size) {
            add(data);
        } else {
            Node tmp = root;
            for (int i = 0; i < index; i++) {
                tmp = tmp.getNext();
            }
            int tempData = tmp.getData();
            tmp.setData(data);
            for (int i = index + 1; i < size; i++) {
                tmp = tmp.getNext();
                int tempData2 = tmp.getData();
                tmp.setData(tempData);
                tempData = tempData2;
            }
            last = new Node(tempData);
            tmp.setNext(last);
            size++;
        }
    }
    
    public int indexOf(int data) {
        Node tmp = root;
        for (int i = 0; i < size; i++) {
            if (tmp.getData() == data) return i;
            tmp = tmp.getNext();
        }
        return -1;
    }
    
    public boolean contains(int data) {
        Node tmp = root;
        while (tmp != null) {
            if (tmp.getData() == data) return true;
            tmp = tmp.getNext();
        }
        return false;
    }
    
    public Node remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        }
        Node ret;
        if (index == 0) {
            ret = root;
            root = root.getNext();
        } else {
            Node tmp = root;
            for (int i = 0; i < index - 1; i++) {
                tmp = tmp.getNext();
            }
            ret = tmp.getNext();
            if (ret == last) {
                last = tmp;
            }
            tmp.setNext(tmp.getNext().getNext());
        }
        size--;
        return ret;
    }
    
    public void clear() {
        root = null;
        last = null;
        size = 0;
    }
    
    public int size() {
        return size;
    }
    
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        } else {
            Node tmp = root;
            for (int i = 0; i < index; i++) {
                tmp = tmp.getNext();
            }
            return tmp.getData();
        }
    }
    
    public void set(int data, int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        } else {
            Node tmp = root;
            for (int i = 0; i < index; i++) {
                tmp = tmp.getNext();
            }
            tmp.setData(data);
        }
    }
    
    public void sort() {
        for (int i = 0; i < size - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < size; j++) {
                if (get(j) < get(smallest)) smallest = j;
            }
            swap(smallest, i);
        }
    }
    
    public void swap(int index1, int index2) {
        int tmp = get(index1);
        set(get(index2), index1);
        set(tmp, index2);
    }
    
    public int count(int object) {
        int count = 0;
        Node tmp = root;
        while (tmp != null) {
            if (tmp.getData() == object) count++;
            tmp = tmp.getNext();
        }
        return count;
    }
    
    public boolean verifySort() {
        for (int i = 0; i < size - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < size; j++) {
                if (get(j) < get(smallest)) return false;
            }
        }
        return true;
    }
    
    public void reverse() {
        for (int i = 0; i < size / 2; i++) {
            swap(i, size - 1 - i);
        }
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public void print() {
        Node tmp = root;
        while (tmp != null) {
            System.out.println(tmp.getData());
            tmp = tmp.getNext();
        }
    }
    
//    public static void main(String[] args) {
//        MyList ml = new MyList();
//        ml.add(3);
//        ml.add(2);
//        ml.add(4);
//        ml.add(1);
//        ml.add(5);
//        ml.add(6);
//        
//        System.out.println("Printed List:");
//        ml.print();
//        
//        int test = 3;
//        System.out.println("count value " + test + " : " + ml.count(test));
//        
//        ml.sort();
//        
//        System.out.println("Printed List:");
//        ml.print();
//        
//        ml.reverse();
//        
//        System.out.println("Printed List:");
//        ml.print();
//    }
}
