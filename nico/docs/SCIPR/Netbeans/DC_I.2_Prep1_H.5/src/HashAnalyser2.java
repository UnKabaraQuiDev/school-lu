
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
public class HashAnalyser2 {
    
    private HashMap<String, Integer> hmWords = new HashMap<>();
    
    public StatsEntry[] analyse(String text) {
        hmWords.clear();
        String[] words = text.split(" ");
        for (String word : words) {
            int count = 1;
            if (hmWords.containsKey(word)) {
                count = hmWords.get(word) + 1;
            }
            hmWords.put(word, count);
        }
        return toArray();
    }
    
    private StatsEntry[] toArray() {
        StatsEntry[] result = new StatsEntry[hmWords.size()];
        int index = 0;
        for (Map.Entry<String, Integer> entry : hmWords.entrySet()) {
            StatsEntry statsEntry = new StatsEntry(entry.getKey(), entry.getValue());
            result[index++] = statsEntry;
        }
        Arrays.sort(result, (a, b) -> a.getWord().compareTo(b.getWord()));
        return result;
    }
}
