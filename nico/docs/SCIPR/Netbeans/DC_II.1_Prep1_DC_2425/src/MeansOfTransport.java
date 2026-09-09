
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class MeansOfTransport {
    
    private ArrayList<MeanOfTransport> alMeanOfTransport = new ArrayList<>();

    public int size() {
        return alMeanOfTransport.size();
    }

    public boolean isEmpty() {
        return alMeanOfTransport.isEmpty();
    }

    public Object[] toArray() {
        return alMeanOfTransport.toArray();
    }

    public MeanOfTransport get(int index) {
        return alMeanOfTransport.get(index);
    }

    public boolean add(MeanOfTransport e) {
        return alMeanOfTransport.add(e);
    }

    public void clear() {
        alMeanOfTransport.clear();
    }
    
    public void loadFromDatabase(Database database) throws ClassNotFoundException, SQLException {
        clear();
        MeanOfTransport mot = null;
        for (Map.Entry<Integer, String> entry : database.loadMots().entrySet()) {
            int key = entry.getKey();
            mot = new MeanOfTransport(entry.getValue());
            for (Map.Entry<String, String> entry2 : database.loadMotsAttributes(key).entrySet()) {
                String key2 = entry2.getKey();
                String value2 = entry2.getValue();
                mot.addAttribute(key2, value2);
            }
            alMeanOfTransport.add(mot);
        }
    }
}
