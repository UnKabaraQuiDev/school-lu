/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class StatsEntry {
    
    private String word;
    private int count;

    public void setWord(String word) {
        this.word = word;
    }
    
    public String getWord() {
        return word;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
    
    @Override
    public String toString() {
        return word + " --> " + count;
    }
}
