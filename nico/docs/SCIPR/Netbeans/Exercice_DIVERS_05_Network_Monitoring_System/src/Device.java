
import java.awt.Color;
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
public class Device {
    
    private String name;
    private String ip;
    
    private static final int UNKNOWN = 0;
    private static final int ONLINE = 1;
    private static final int OFFLINE = 2;
    
    private int state;
    
    private boolean selected = false;
    
    private ArrayList<Device> alLinkedDevices = new ArrayList<>();
    
    private Point position;
    private int size;

    public Device(String name, String ip, Point position, int size) {
        this.name = name;
        this.ip = ip;
        
        this.state = UNKNOWN;
        
        this.position = position;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ArrayList<Device> getAlLinkedDevices() {
        return alLinkedDevices;
    }

    public void setAlLinkedDevices(ArrayList<Device> alLinkedDevices) {
        this.alLinkedDevices = alLinkedDevices;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int size() {
        return alLinkedDevices.size();
    }

    public boolean isEmpty() {
        return alLinkedDevices.isEmpty();
    }

    public Device get(int index) {
        return alLinkedDevices.get(index);
    }

    public boolean add(Device e) {
        return alLinkedDevices.add(e);
    }

    public Device remove(int index) {
        return alLinkedDevices.remove(index);
    }

    public boolean remove(Object o) {
        return alLinkedDevices.remove(o);
    }

    public void clear() {
        alLinkedDevices.clear();
    }

    public boolean contains(Object o) {
        return alLinkedDevices.contains(o);
    }

    @Override
    public String toString() {
        if (ip == null) return name;
        else return name + " (" + ip + ")";
    }
    
    public void draw(Graphics g) {
        switch (state) {
            case UNKNOWN -> g.setColor(Color.ORANGE);
            case ONLINE -> g.setColor(Color.GREEN);
            case OFFLINE -> g.setColor(Color.RED);
            default -> {
            }
        }
        g.fillRect(position.x, position.y, size, size);
        if (selected) {
            g.setColor(Color.BLUE);
            g.drawRect(position.x, position.y, size, size);
        }
        g.setColor(Color.BLACK);
        g.drawString(toString(), position.x, position.y + size / 2);
    }
    
    public void drawLinks(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < alLinkedDevices.size(); i++) {
            Device device = alLinkedDevices.get(i);
            g.drawLine(position.x + size / 2, position.y + size / 2, device.getPosition().x + size / 2, device.getPosition().y + size / 2);
        }
    }
    
    public boolean isInside(Point point) {
        return point.x >= position.x && point.x <= position.x + size && point.y >= position.y && point.y <= position.y + size;
    }
    
    public void toggleState() {
        if (state == UNKNOWN) {
            state = ONLINE;
        } else if (state == ONLINE) {
            state = OFFLINE;
        } else if (state == OFFLINE) {
            state = ONLINE;
        }
    }
}
    