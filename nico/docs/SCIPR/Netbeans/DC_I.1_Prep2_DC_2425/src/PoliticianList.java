
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class PoliticianList {
    
    private Node root;
    private int size;
    
    public void add(Politician d) {
        Node n = new Node(d);
        if (size == 0) {
            root = n;
        } else {
            Node tmp = root;
            while (tmp.getNext() != null) {
                tmp = tmp.getNext();
            }
            tmp.setNext(n);
        }
        size++;
    }
    
    public void print() {
        Node tmp = root;
        while (tmp != null) {
            System.out.println(tmp.toString());
            tmp = tmp.getNext();
        } 
    }
    
    public void addInOrder(Politician d) {
        Node n = new Node(d);
        if (size == 0) {
            root = n;
        } else if (root.getPolitician().getName().compareTo(d.getName()) < 0) {
            n.setNext(root);
            root = n;
        } else {
            Node tmp = root;
            while (tmp.getNext() != null && tmp.getNext().getPolitician().getName().compareTo(d.getName()) < 0) {
                tmp = tmp.getNext();
            }
            n.setNext(tmp.getNext());
            tmp.setNext(n);
        }
        size++;
    }
    
    public int size() {
        return size;
    }
    
    private Node getNode(int index) {
        if (index < 0 && index >= size) {
            throw new ArrayIndexOutOfBoundsException("Index " + index + " out of bounds");
        } else {
            Node tmp = root;
            for (int i = 0; i < index; i++) {
                tmp = tmp.getNext();
            }
            return tmp;
        }
    }
    
    public Politician get(int index) {
        return getNode(index).getPolitician();
    }
    
    public void swap(int a, int b) {
        Node nodeA = getNode(a);
        Node nodeB = getNode(b);
        Politician temp = nodeA.getPolitician();
        nodeA.setPolitician(nodeB.getPolitician());
        nodeB.setPolitician(temp);
    }
    
    public void sortByName() {
        for (int i = 0; i < size - 1; i++) {
            Node first = getNode(i);
            int firstIndex = i;
            for (int j = i + 1; j < size; j++) {
                Node tmp = getNode(j);
                if (tmp.getPolitician().getName().compareTo(first.getPolitician().getName()) < 0) {
                    first = tmp;
                    firstIndex = j;
                }
            }
            if (firstIndex != i) swap(i, firstIndex);
        }
    }
    
    public void sortByVotes() {
        for (int i = 0; i < size - 1; i++) {
            Node minNode = getNode(i);
            int minIndex = i;
            for (int j = i + 1; j < size; j++) {
                Node tmp = getNode(j);
                if (minNode.getPolitician().getVotes() < tmp.getPolitician().getVotes()) {
                    minNode = tmp;
                    minIndex = j;
                }
            }
            if (minIndex != i) swap(minIndex, i);
        }
    }
    
    public void rank() {
        sortByVotes();
        for (int i = 0; i < size; i++) {
            getNode(i).getPolitician().setRank(i + 1);
        }
    }
    
    public Object[] toArray() {
        Politician[] politicians = new Politician[size];
        Node tmp = root;
        for (int i = 0; i < size; i++) {
            politicians[i] = tmp.getPolitician();
            tmp = tmp.getNext();
        }
        return politicians;
    }
    
    public void saveToFile(String fileName) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            for (int i = 0; i < size; i++) {
                out.println(getNode(i).getPolitician().toCsv());
            }
        }
    }
    
    public void loadFromFile(String fileName) throws IOException, FileNotFoundException {
        try (BufferedReader in = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            root = null;
            size = 0;
            while ((line = in.readLine()) != null) {
                add(new Politician(line));
            }
        }
    }
}
