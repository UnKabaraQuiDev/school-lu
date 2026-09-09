/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Language {
    
    private String[] knownCommands = {"def", "set", "dec", "inc", "jpd", "jiz", "jnz", "inp", "out"};
    
    public boolean isValid(Command command) {
        for (String knownCommand : knownCommands) {
            if (knownCommand.equals(command.cmd)) return true;
        }
        return false;
    }
}
