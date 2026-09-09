
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Test {
    
    private String name;
    private String firstName;
    
    public Test(String name, String firstName) {
        this.name = name;
        this.firstName = firstName;
    }
    
    @Override
    public String toString() {
        return firstName + ">" + name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.firstName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Test other = (Test) obj;
        return Objects.equals(this.name, other.name);
    }
    
    public int equals2(Test other) {
        if (other == null) return 1;
        if (other.name.equals(name)) return 0;
        else return name.compareTo(other.name);
    }
    
    public static void main(String[] args) {
        HashSet<Test> hsTest = new HashSet<>();
        
        Test a = new Test("a", "b");
        Test b = new Test("b", "b");
        
        hsTest.add(a);
        hsTest.add(b);
        
        for (Iterator<Test> iterator = hsTest.iterator(); iterator.hasNext();) {
            Test next = iterator.next();
            System.out.println(next);
            System.out.println(next.hashCode());
        }
    }
    
}
