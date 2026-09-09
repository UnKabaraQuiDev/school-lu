
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
public class TelephoneBook {
    
    private ArrayList<Person> alPerons = new ArrayList<>();

    public int size() {
        return alPerons.size();
    }

    public boolean add(Person e) {
        return alPerons.add(e);
    }

    public boolean remove(Object o) {
        return alPerons.remove(o);
    }

    public Person remove(int index) {
        return alPerons.remove(index);
    }

    public void clear() {
        alPerons.clear();
    }

    public void sort() {
        Collections.sort(alPerons);
    }

    public Person get(int index) {
        return alPerons.get(index);
    }

    public boolean isEmpty() {
        return alPerons.isEmpty();
    }
}
