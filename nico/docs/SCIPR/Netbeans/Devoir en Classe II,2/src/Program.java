
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author mattiwirtz
 */
class Program {

    private Command root;
    private Command last;

    HashMap<String, Integer> hmVariables = new HashMap<>();
    HashMap<String, Command> hmJumpPoints = new HashMap<>();

    public void add(Command command) {
        last.next = command;
        last = command;
    }

    public void clear() {
        last = null;
        root = null;
    }

    public void execute() {
        try {
            System.out.println("Program.execute()");
            System.err.println("Root: " + root.toString());
            Command next = root;
            while (next.next != null) {
                next = next.execute(this);
                System.err.println("Executed one");
            } next.execute(this);
        } catch (InvalidNameException ex) {
            System.err.println(ex.getMessage());
        }

    }

    public String getText() {
        String text = "";
        Command next = root;
        while (next.next != null) {
            text = text + next.toString() + "\n";
            next = next.next;
        }
        text = text + next.toString() + "\n";
        return text;
    }

    public void loadFromText(String text) {
        String[] temp = text.split("\n");
        
        int counter = 0;
        
        while (counter < temp.length) {
            if (counter == 0 && root == null){
                root = new Command(temp[counter]);
                last = root;
            } else {
                last.next = new Command(temp[counter]);
                last = last.next;
            }
            counter ++;
        }
    }

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println(getText());
        }
    }

    public void loadFromFile(String filename) throws FileNotFoundException, IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = in.readLine()) != null) {
                loadFromText(line);
            }
        }
    }
}
