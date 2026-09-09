
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.management.AttributeNotFoundException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class MeanOfTransport {
    
    private HashMap<String, String> hmAttributes = new HashMap<>();

    public MeanOfTransport() {}
    
    public MeanOfTransport(String name) {
        addAttribute("name", name);
    }
    
    public void addAttribute(String name, String value) {
        hmAttributes.put(name, value);
    }
    
    public String getAttribute(String name) {
        return hmAttributes.get(name);
    }
    
    @Override
    public String toString() {
        return hmAttributes.get("name");
    }
    
    public ArrayList<String> getAttributeNames() {
        ArrayList<String> alResult = new ArrayList<>();
        for (Map.Entry<String, String> entry : hmAttributes.entrySet()) {
            alResult.add(entry.getKey());
        }
        if (!alResult.isEmpty())
            return alResult;
        else
            return null;
    }
    
    public MeanOfTransport loadFromFile(String fileName) throws IOException {
        MeanOfTransport mot = new MeanOfTransport();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("=");
                mot.addAttribute(parts[0], parts[1]);
            }
            
        }
        return mot;
    }
    
    public void saveToFile(String fileName) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            for (Map.Entry<String, String> entry : hmAttributes.entrySet()) {
                out.println(entry.getKey() + "=" + entry.getValue());
            }
        }
    }
    
    public int compareTo(String attribute, MeanOfTransport that) throws AttributeNotFoundException {
        if (this.getAttribute(attribute) == null || that.getAttribute(attribute) == null) {
            throw new AttributeNotFoundException();
        } else {
            return that.getAttribute(attribute).compareTo(this.getAttribute(attribute));
        }
        
//        if (!this.hmAttributes.containsKey(attribute)) {
//            throw new AttributeNotFoundException("Can't find attribute " + attribute + " in THAT");
//        }
//        if (!that.hmAttributes.containsKey(attribute)) {
//            throw new AttributeNotFoundException("Can't find attribute " + attribute + " in THAT");
//        }
//        return this.getAttribute(attribute).compareTo(that.getAttribute(attribute));
    }
}
