/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author killerbro
 */
public class Language {

    private static String[] knownCommands = {
        "def",
        "set",
        "dec",
        "inc",
        "jpd",
        "jiz",
        "jnz",
        "inp",
        "out",};

    public static boolean isValid(Command command) {
        int counter = 0;
        while (knownCommands.length > counter) {
            if (command.cmd.equals(knownCommands[counter])) {
                return true;
            }
            counter ++;
        }
        return false;
    }
}
