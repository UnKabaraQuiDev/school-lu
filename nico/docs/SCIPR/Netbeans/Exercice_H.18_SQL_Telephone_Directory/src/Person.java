/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Person implements Comparable<Person> {
    
    private int pk_person;
    private String fullName;
    private String telNr;
    private String email;

    public Person(String fullName, String telNbr, String email) {
        this.fullName = fullName;
        this.telNr = telNbr;
        this.email = email;
    }
    
    @Override
    public int compareTo(Person other) {
        return this.fullName.compareTo(other.fullName);
    }

    public String getFullName() {
        return fullName;
    }

    public String getTelNr() {
        return telNr;
    }

    public String getEmail() {
        return email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setTelNr(String telNr) {
        this.telNr = telNr;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
