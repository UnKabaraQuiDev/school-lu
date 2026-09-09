
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class HashAnalyser {
    
    private HashMap<String, Integer> hmWords = new HashMap<>();
    
    public String[] analyse(String text) {
        hmWords.clear();
        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            int count = 1;
            if (hmWords.containsKey(word)) {
                count = hmWords.get(word) + 1;
            }
            hmWords.put(word, count);
        }
        return toArray();
    }
    
    private String[] toArray() {
        String[] result = new String[hmWords.size()];
        int index = 0;
        for (Map.Entry<String, Integer> entry : hmWords.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            result[index] = key + " --> " + value;
            index++;
        }
        Arrays.sort(result); // O(n*log(n))     // n = Unzuel Symbols
        return result;
    }
}
