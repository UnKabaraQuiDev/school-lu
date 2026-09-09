/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Person implements Comparable<Person> {
    
    private String surname;
    private String firstname;
    private String title;

    public Person(String surname, String firstname, String title) {
        this.surname = surname;
        this.firstname = firstname;
        this.title = title;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String toString() {
        return surname + " " + firstname + " - " + title;
    }

    @Override
    public int compareTo(Person other) {
        if (this.surname.equals(other.surname)) {
            return this.surname.compareTo(other.surname);
        } else {
            return this.firstname.compareTo(other.firstname);
        }
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) return true;
        if (anObject == null || getClass() != anObject.getClass()) return false;
        Person person = (Person) anObject;
        return surname.equals(person.getSurname())
                && firstname.equals(person.getFirstname())
                && title.equals(person.getTitle());
    }
}
