/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Person implements Comparable<Person> {
    
    private String name;
    private int age;
    private int height;

    public Person(String name, int age, int height) {
        this.name = name;
        this.age = age;
        this.height = height;
    }
    
    @Override
    public String toString() {
        return name + " > " + age + " : " + height;
    }

    @Override
    public int compareTo(Person that) {
        if (this.age == that.age) {
            return this.height - that.height;
        } else {
            return this.age - that.age;
        }
    }
}
