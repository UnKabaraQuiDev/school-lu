
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.naming.InvalidNameException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Program {
    
    private Command root;
    private Command last;
    
    HashMap<String, Integer> hmVariables = new HashMap<>();
    HashMap<String, Command> hmJumpPoints = new HashMap<>();
    
    public void add(Command command) {
        command.next = null;    // to avoid problems
        if (root == null) {
            root = command;
            last = command;
        } else {
            last.next = command;
            last = command;
        }
    }
    
    public void clear() {
        root = null;
        last = null;
        hmVariables.clear();
        hmJumpPoints.clear();
    }
    
    public void execute() throws InvalidNameException {
        hmVariables.clear();
        hmJumpPoints.clear();
        Command next = root;
        while (next != null) {
            next = next.execute(this);
        }
    }
    
    public String getText() {
        String result = "";
        Command next = root;
        while (next != null) {
            result += next.toString() + "\n";
//            System.out.println(next.toString());
            next = next.next;
        }
        return result;
    }
    
    public void loadFromText(String text) {
        clear();
        String newText = text.replace("\r", "");
        String[] parts = newText.split("\n");
        for (String part : parts) {
            add(new Command(part));
        }
    }
    
    public void saveToFile(String fileName) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            out.println(getText());
        }
    }
    
    public void loadFromFile(String fileName) throws IOException, FileNotFoundException {
        clear();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
//                loadFromText(line);
                add(new Command(line));
            }
        }
    }
}
