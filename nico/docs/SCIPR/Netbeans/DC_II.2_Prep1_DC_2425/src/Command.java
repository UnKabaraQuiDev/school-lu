
import java.util.ArrayList;
import javax.naming.InvalidNameException;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Command {
    
    protected String cmd;
    protected ArrayList<String> params = new ArrayList<>();
    protected Command next;

    public Command(String line) {
        String[] parts = line.split(" ");
        this.cmd = parts[0];
        for (int i = 1; i < parts.length; i++) {
            params.add(parts[i]);
        }
    }
    
    public Command execute(Program program) throws InvalidNameException {
        if (cmd.startsWith("//") || cmd.isEmpty()) {
            return next;
        } else if (!Language.isValid(this)) {
            throw new InvalidNameException("Invalid command: " + cmd);
        } else if (cmd.equals("jpd")) {
            String param1 = params.get(0);
            program.hmJumpPoints.put(param1, this);
        } else if (cmd.equals("def")) {
            String param1 = params.get(0);
            program.hmVariables.put(param1, 0);
        } else if (cmd.equals("set")) {
            String param1 = params.get(0);
            String param2 = params.get(1);
            if (!program.hmVariables.containsKey(param1)) {
                throw new InvalidNameException("Undefined variable: " + param1);
            }
            if (program.hmVariables.containsKey(param2)) {
                program.hmVariables.replace(param1, program.hmVariables.get(param2));
            } else {
                try {
                    program.hmVariables.replace(param1, Integer.valueOf(param2));
                } catch (NumberFormatException ex) {
                    throw new InvalidNameException("Undefined variable: " + param2);
                }
            }
        } else if (cmd.equals("inc")) {
            String param1 = params.get(0);
            if (!program.hmVariables.containsKey(param1)) {
                throw new InvalidNameException("Undefined variable: " + param1);
            }
            program.hmVariables.replace(param1, program.hmVariables.get(param1) + 1);
        } else if (cmd.equals("dec")) {
            String param1 = params.get(0);
            if (!program.hmVariables.containsKey(param1)) {
                throw new InvalidNameException("Undefined variable: " + param1);
            }
            program.hmVariables.replace(param1, program.hmVariables.get(param1) - 1);
        } else if (cmd.equals("jiz")) {
            String param1 = params.get(0);
            String param2 = params.get(1);
            if (!program.hmVariables.containsKey(param1)) {
                throw new InvalidNameException("Undefined variable: " + param1);
            }
            if (!program.hmJumpPoints.containsKey(param2)) {
                throw new InvalidNameException("Undefined jump point: " + param2);
            }
            if (program.hmVariables.get(param1) == 0) {
                return program.hmJumpPoints.get(param2);
            }
        } else if (cmd.equals("jnz")) {
            String param1 = params.get(0);
            String param2 = params.get(1);
            if (!program.hmVariables.containsKey(param1)) {
                throw new InvalidNameException("Undefined variable: " + param1);
            }
            if (!program.hmJumpPoints.containsKey(param2)) {
                throw new InvalidNameException("Undefined jump point: " + param2);
            }
            if (program.hmVariables.get(param1) != 0) {
                return program.hmJumpPoints.get(param2);
            }
        } else if (cmd.equals("out")) {
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                if (program.hmVariables.containsKey(param)) {
                    System.out.println(program.hmVariables.get(param));
                } else {
                    System.out.println(param);
                }
            }
            System.out.println("\n");
        } else if (cmd.equals("inp")) {
            String param1 = params.get(0);
            if (!program.hmVariables.containsKey(param1)) {
                throw new InvalidNameException("Undefined variable: " + param1);
            }
            String value = JOptionPane.showInputDialog("Enter an Integer for: " + param1);
            program.hmVariables.put(param1, Integer.valueOf(value));
        }
        return next;
    }
    
    @Override
    public String toString() {
        String result = cmd;
        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            result += " " + param;
        }
        return result;
    }
}
