/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Language {
    
    private static final String[] knownCommands = {"def", "set", "dec", "inc", "jpd", "jiz", "jnz", "inp", "out"};
    
    public static boolean isValid(Command command) {
        for (int i = 0; i < knownCommands.length; i++) {
            if (knownCommands[i].equals(command.cmd)) return true;
        }
        return false;
    }
}
