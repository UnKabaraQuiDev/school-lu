
import java.util.ArrayList;
import java.util.Collections;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Node implements Comparable<Node> {
    
    private Person person;
    private ArrayList<Node> subordinates = new ArrayList<>();

    public Node(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ArrayList<Node> getSubordinates() {
        return subordinates;
    }
    
    public void addSubordinate(Node subordinate) {
        subordinates.add(subordinate);
        Collections.sort(subordinates, (a, b) -> a.getPerson().compareTo(b.getPerson()));
    }
    
    @Override
    public int compareTo(Node other) {
        if (this.getPerson().getSurname().equals(other.getPerson().getSurname())) {
            return this.getPerson().getSurname().compareTo(other.getPerson().getSurname());
        } else {
            return this.getPerson().getFirstname().compareTo(other.getPerson().getFirstname());
        }
    }
}
