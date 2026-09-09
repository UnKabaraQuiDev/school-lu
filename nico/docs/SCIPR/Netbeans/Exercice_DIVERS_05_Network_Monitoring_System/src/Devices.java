
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Devices {
    
    private ArrayList<Device> alDevices = new ArrayList<>();

    public int size() {
        return alDevices.size();
    }

    public boolean isEmpty() {
        return alDevices.isEmpty();
    }

    public Device get(int index) {
        return alDevices.get(index);
    }

    public boolean add(Device e) {
        return alDevices.add(e);
    }

    public Device remove(int index) {
        return alDevices.remove(index);
    }

    public boolean remove(Object o) {
        return alDevices.remove(o);
    }

    public void clear() {
        alDevices.clear();
    }

    public Object[] toArray() {
        return alDevices.toArray();
    }
    
    public void draw(Graphics g) {
        for (int i = 0; i < alDevices.size(); i++) {
            Device device = alDevices.get(i);
            device.drawLinks(g);
        }
        for (int i = 0; i < alDevices.size(); i++) {
            Device device = alDevices.get(i);
            device.draw(g);
        }
    }
    
    public Device getDeviceAtPosition(Point point) {
        for (int i = 0; i < alDevices.size(); i++) {
            Device device = alDevices.get(i);
            if (device.isInside(point)) return device;
        }
        return null;
    }
    
    public void saveToJSON(String fileName) {
        
    }
    
    public void loadFromJSON(String fileName) {
        
    }
}
