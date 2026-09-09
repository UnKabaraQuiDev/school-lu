package model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Elements {
    
    private ArrayList<Element> alElements = new ArrayList<>();
    
    private Element selectedElement = null;
    
    public int size() {
        return alElements.size();
    }

    public void add(Element element) {
        alElements.add(element);
    }

    public void removeSelected() {
        if (selectedElement != null) {
            alElements.remove(selectedElement);
            selectedElement = null;
        }
    }

    public void clear() {
        alElements.clear();
    }
    
    public boolean isEmpty() {
        return alElements.isEmpty();
    }
    
    public Element getSelectedElement() {
        return selectedElement;
    }
    
    public void selectElementAt(Point p) {
        // Deselect all
        for (Element element : alElements) element.setSelected(false);
        selectedElement = null;

        // Check from top (reverse order) so front items are selected first
        for (int i = alElements.size() - 1; i >= 0; i--) {
            Element element = alElements.get(i);
            if (element.contains(p)) {
                element.setSelected(true);
                selectedElement = element;
                break;
            }
        }
    }
    
    public void bringToFront(Element e) {
        if (alElements.contains(e)) {
            alElements.remove(e);
            alElements.add(e);
        }
    }

    public void sendToBack(Element e) {
        if (alElements.contains(e)) {
            alElements.remove(e);
            alElements.add(0, e);
        }
    }

    public void bringForward(Element e) {
        int index = alElements.indexOf(e);
        if (index >= 0 && index < alElements.size() - 1) {
            alElements.remove(e);
            alElements.add(index + 1, e);
        }
    }

    public void sendBackward(Element e) {
        int index = alElements.indexOf(e);
        if (index > 0) {
            alElements.remove(e);
            alElements.add(index - 1, e);
        }
    }
    
    public void draw(Graphics g) {
        for(int i = 0; i < alElements.size(); i++) {
            Element element = alElements.get(i);
            element.draw(g);
        }
    }
    
    public void saveToJSON(String fileName) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.print(gson.toJson(alElements));
            //gson.toJson(alElements, out);
        }
    }
    
    public void loadFromJSON(String fileName) throws IOException, FileNotFoundException {
        Gson gson = new Gson();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            alElements.clear();
            selectedElement = null;
            
            Element[] loaded = gson.fromJson(in, Element[].class);

            if (loaded != null) {
                for (Element e : loaded) {
                    if (e != null) {
                        Point start = e.getStart();
                        Point end = e.getEnd();
                        Color outline = new Color(e.getOutlineRGB());
                        Color fill = new Color(e.getFillRGB());

                        Element newElement = null;
                        switch (e.getType()) {
                            case "Line":
                                newElement = new Line(start, end, outline, fill);
                                break;
                            case "Rectangle":
                                newElement = new Rectangle(start, end, outline, fill);
                                break;
                            case "Ellipse":
                                newElement = new Ellipse(start, end, outline, fill);
                                break;
                            default:
                                continue;
                        }
                        alElements.add(newElement);
                    }
                }
            }
        }
    }
}
