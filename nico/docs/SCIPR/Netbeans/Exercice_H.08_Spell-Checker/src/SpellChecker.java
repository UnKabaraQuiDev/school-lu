
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TreeSet;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class SpellChecker {
    
    // fisch: 3 classen fir all type vun datestruktur + abstact class
    // class error mat tostring
    
    private String mode;  // al / hs / ts --> kënnt vum mainframe
    
    private ArrayList<String> alWordList = new ArrayList<>();
    private HashSet<String> hsWordList = new HashSet<>();
    private TreeSet<String> tsWordList = new TreeSet<>();
    
    private ArrayList<String> alText = new ArrayList<>();
    
    private ArrayList<String> alErrors = new ArrayList<>();
    
    private void loadWordsFromFile(String fileName) throws IOException {
        alWordList.clear();
        hsWordList.clear();
        tsWordList.clear();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    alWordList.add(word);
                    hsWordList.add(word);
                    tsWordList.add(word);
                }
            }
        }
//        System.out.println(alWordList.size());
//        System.out.println(hsWordList.size());
//        System.out.println(tsWordList.size());
    }
    
    private void loadText(String text) {
        alText.clear();
        String[] words = text.split(" ");
        for (String word : words) {
            alText.add(word);
        }
    }
    
    public long checkText(String mode, String fileName, String text) throws IOException {
        long start = Calendar.getInstance().getTimeInMillis();
        alErrors.clear();
        this.mode = mode;
        loadWordsFromFile(fileName);
        loadText(text.replaceAll("\\n", " ").replaceAll(".,?!", " "));
        switch (mode) {
            case "al":
                for (int i = 0; i < alText.size(); i++) {
                    String word = alText.get(i);
                    if (!alWordList.contains(word)) {
                        alErrors.add(word + " @ " + i);
                    }
                }
                break;
            case "hs":
                for (int i = 0; i < alText.size(); i++) {
                    String word = alText.get(i);
                    if (!hsWordList.contains(word)) {
                        alErrors.add(word + " @ " + i);
                    }
                }
                break;
            case "ts":
                for (int i = 0; i < alText.size(); i++) {
                    String word = alText.get(i);
                    if (!tsWordList.contains(word)) {
                        alErrors.add(word + " @ " + i);
                    }
                }
                break;
            default:
                break;
        }
        long end = Calendar.getInstance().getTimeInMillis();
        return end - start;
    }

    public Object[] errorsToArray() {
        return alErrors.toArray();
    }
}
