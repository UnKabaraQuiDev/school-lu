
import java.util.ArrayList;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author mattiwirtz
 */
public class Command {

    protected String cmd;
    protected ArrayList<String> params = new ArrayList<>();
    protected Command next = null;

    public Command(String line) {
        String[] temp = line.split(" ");
        this.cmd = temp[0];
        int counter = 1;
        while (counter < temp.length) {
            params.add(temp[counter]);
            counter++;
        }
    }

    public Command execute(Program program) throws InvalidNameException {
        if (cmd.startsWith("//")) {
        } 
        else if(cmd.isEmpty()){
            System.err.println("isEmpty");
        }
        
        else if (new Language().isValid(this) == false) {
            throw new InvalidNameException("Invalid Command: " + cmd);
        } 
        
        else if (cmd.equals("jpd")) {
            program.hmJumpPoints.put(params.get(0), this);
            System.err.println("jpd");
        } 
        
        else if (cmd.equals("def")) {
            program.hmVariables.put(params.get(0), 0);
            System.err.println("def");
        } 
        
        else if (cmd.equals("set")) {
            if (program.hmVariables.get(params.get(1)) != null) {
                if (program.hmVariables.replace(params.get(0), program.hmVariables.get(params.get(1))) != null) {
                } else {
                    throw new InvalidNameException("Undefined variable: " + params.get(0));
                }
            } else if (program.hmVariables.replace(params.get(0), Integer.valueOf(params.get(1))) != null) {
            } else {
                throw new InvalidNameException("Undefined variable: " + params.get(0));
            }
            System.err.println("set");
        } 
        
        else if (cmd.equals("inc")) {
            Integer temp = program.hmVariables.get(params.get(0));
            if (temp != null) {
                program.hmVariables.replace(params.get(0), temp + 1);
            } else {
                throw new InvalidNameException("Undefined variable: " + params.get(0));
            }
            System.err.println("inc");
        } 
        
        else if (cmd.equals("dec")) {
            Integer temp = program.hmVariables.get(params.get(0));
            if (temp != null) {
                program.hmVariables.replace(params.get(0), temp - 1);
            } else {
                throw new InvalidNameException("Undefined variable: " + params.get(0));
            }
            System.err.println("dec");
        } 
        
        else if (cmd.equals("jiz")) {
            if (program.hmVariables.get(params.get(0)) == 0) {
                return program.hmJumpPoints.get(params.get(1));
                
            }
            System.err.println("jiz");
        } 
        
        else if (cmd.equals("jnz")) {
            if (program.hmVariables.get(params.get(0)) != 0) {
                return program.hmJumpPoints.get(params.get(1));
                
            }
            System.err.println("jnz");
        } 
        
        else if (cmd.equals("out")) {
            System.err.println("out");
            String temp = "";
            for (int i = 0; i < params.size(); i++) {
                String get = params.get(i);
                Integer temp1 = program.hmVariables.get(get);
                if (temp1 != null) {
                    temp = temp + " " + String.valueOf(temp1);
                } else {
                    temp = temp + " " + get;
                }
            }
            System.out.println(temp);
        } 
        
        else if (cmd.equals("inp")) {
            if (program.hmVariables.get(params.get(0)) != null) {
                program.hmVariables.replace(params.get(0), Integer.valueOf(JOptionPane.showInputDialog("Enter a value")));
            }
            System.err.println("inp");
        }
        return next;
    }

    @Override
    public String toString() {
        String temp = cmd;
        for (int i = 0; i < params.size(); i++) {
            String get = params.get(i);
            temp = temp + " " + get;
        }
        return temp;
    }
}
