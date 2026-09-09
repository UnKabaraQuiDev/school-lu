/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class MyList {
    
    private Node root = null;
    private Node last = null;
    private int size = 0;
    
    public void addOLD(int value) {    // Komplexitéit: O(n)
        Node n = new Node(value);
        
        if (root == null) {
            root = n;
        } else {
            // set tmp to point to the last existing node
            Node tmp = root;
            while (tmp.getNext() != null) {
                tmp = tmp.getNext();
            }
            tmp.setNext(n);
        }
        size++;
    }
    
    public void add(int value) {    // Komplexitéit: O(1)
        Node n = new Node(value);
        
        if (root == null) {
            root = n;
            last = n;
        } else {
            // set tmp to point to the last existing node
            /*
            Node tmp = root;
            while (tmp.getNext() != null) {
                tmp = tmp.getNext();
            }
            tmp.setNext(n);
            */
            last.setNext(n);
            last = n;
        }
        size++;
    }
    
    public int indexOfOLD(int value) {
        Node tmp = root;
        for (int i = 0; i < size - 1; i++) {
            if (tmp.getData() == value) return i;
            tmp = tmp.getNext();
        }
        return -1;
    }
    
    public int indexOf(int value) {    // Komplexitéit: O(n)
        Node tmp = root;
        int pos = 0;
        while (tmp != null) {
            if (tmp.getData() == value)
                return pos;
            tmp = tmp.getNext();
            pos++;
        }
        return -1;
    }
    
    public boolean contains(int value) {
        Node tmp = root;
        for (int i = 0; i < size; i++) {
            if (tmp.getData() == value) return true;
            tmp = tmp.getNext();
        }
        return false;
    }
    
    public void removeOLD(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        }
        
        if (index == 0) {
            root = root.getNext();
        } else {
            Node tmp = root;
            for (int i = 0; i < index - 1; i++) {
                tmp = tmp.getNext();
            }
            tmp.setNext(tmp.getNext().getNext());
        }
        
        size--;
    }
    
    public Node remove(int index) {
        if (index < 0 || index >= size()) {
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
            if (ret == last)
                last = tmp;
            tmp.setNext(tmp.getNext().getNext());
        }
        size--;
        return ret;
    }
    
    public void clear() {   // Komplexitéit: O(1)
        root = null;
        last = null;
        size = 0;
    }
    
    public int sizeOLD() {     // Komplexitéit: O(n) (n = Unzuel vun den Elementer an der Lëscht)
        int count = 0;
        Node tmp = root;
        while (tmp != null) {
            count++;
            tmp = tmp.getNext();
        }
        return count;
    }
    
    public int size() {     // Komplexitéit: O(1)
        return size;
    }
    
    public int getOLD(int index) {     // Komplexitéit: O(n)
        int count = 0;
        Node tmp = root;
        while (tmp != null && count <= index) {
            if (count == index) {
                return tmp.getData();
            }
            count++;
            tmp = tmp.getNext();
        }
        throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
    }
    
    public int get(int index) {     // Komplexitéit: O(n)
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        }
        Node tmp = root;
        for (int i = 0; i < index; i++) {
            tmp = tmp.getNext();
        }
        return tmp.getData();
    }
    
    public void set(int index, int value) {     // Komplexitéit: O(n)
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds");
        }
        Node tmp = root;
        for (int i = 0; i < index; i++) {
            tmp = tmp.getNext();
        }
        tmp.setData(value);
    }
    
    //selection sort
    public void sortOLD() {    // Komplexitéit: O(n^3)
        if (size > 0) {
            for (int i = 0; i < size - 1; i++) {
                int smallest = i;
                for (int j = i + 1; j < size; j++) {
                    if (get(j) < get(smallest)) {
                        smallest = j;
                    }
                }
                swap(smallest, i);
            }
        }
    }
    
    public void sort() {    // Komplexitéit: O(n^3)
        for (int i = 0; i < size - 1; i++) {
            int posmin = i;
            for (int j = i + 1; j < size; j++)      // --> O(n^2)
                if (get(j) < get(posmin))   // O(n) + O(n)
                    posmin = j;
            
            if (i != posmin) {
                int tmp = get(i);
                set(i, get(posmin));        // O(n)
                set(posmin, tmp);           // O(n)
            }
        }
    }
    
    private void swap(int index1, int index2) {     // Komplexitéit: O(n)
        int temp = get(index1);
        set(index1, get(index2));
        set(index2, temp);
        
//        Node tmp1 = root;
//        for (int i = 0; i <= index1; i++) {
//            tmp1 = tmp1.getNext();
//        }
//        tmp1.setData(get(index1));
//        
//        Node tmp2 = root;
//        for (int i = 0; i <= index2; i++) {
//            tmp2 = tmp2.getNext();
//        }
//        tmp2.setData(get(index2));
    }
    
    public int count(int object) {
        Node tmp = root;
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (tmp.getData() == object) count++;
            tmp = tmp.getNext();
        }
        return count;
    }
    
    public boolean verifySort() {
        for (int i = 0; i < size - 1; i++) {
            int posmin = i;
            for (int j = i + 1; j < size; j++)
                if (get(j) < get(posmin))
                    return false;
        }
        return true;
    }
    
    public void reverse() {
        for (int i = 0; i < size / 2; i++) {
            swap(i, size - 1 - i);
        }
    }
    
    public boolean isEmpty() {
        return root == null;
    }
    
    public void print() {
        Node tmp = root;
        while (tmp != null) {
            System.out.println(tmp.getData());
            tmp = tmp.getNext();
        }
    }
    
    // psvm + Ctrl + space fir follgend method
    public static void main(String[] args) {
        MyList ml = new MyList();
        System.out.println("is list emtpy? " + ml.isEmpty());
        ml.add(5);
        ml.add(2);
        ml.add(1);
        ml.add(4);
        ml.add(3);
        System.out.println("is list emtpy? " + ml.isEmpty());
        
        ml.print();
        System.out.println("size: " + ml.size());
        int getIndex = 2;
        System.out.println("get index " + getIndex + " : " + ml.get(getIndex));
        
        int indexToBeRemoved = 4;
        ml.remove(indexToBeRemoved);
        System.out.println("removed index " + indexToBeRemoved);
        ml.print();
        
        int indexOf = 2;
        System.out.println("get index of value: " + indexOf);
        System.out.println(ml.indexOf(indexOf));
        
        int containsValue = 5;
        System.out.println("contains value: " + containsValue + " ?");
        System.out.println(ml.contains(containsValue));
        
        ml.sort();
        System.out.println("sorted list:");
        ml.print();
        
        int toCount = 2;
        System.out.println("count of " + toCount + ": " + ml.count(toCount));
        
        System.out.println("sorted? "  + ml.verifySort());
        
        System.out.println("printed list:");
        ml.print();
        ml.reverse();
        System.out.println("reversed list:");
        ml.print();
    }
}
