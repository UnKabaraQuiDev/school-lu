
import java.awt.Graphics;
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
public class Storm {
    
    private ArrayList<Snowflake> alSnowflakes = new ArrayList<>();

    public boolean add(Snowflake e) {
        return alSnowflakes.add(e);
    }
    
    public Snowflake getLast() {
        return alSnowflakes.get(alSnowflakes.size() - 1);
    }
    
    public void draw(Graphics g) {
        for (int i = 0; i < alSnowflakes.size(); i++) {
            Snowflake get = alSnowflakes.get(i);
            get.draw(g);
        }
    }
    
    public void sortByDepth() {
        Collections.sort(alSnowflakes);
    }
}
