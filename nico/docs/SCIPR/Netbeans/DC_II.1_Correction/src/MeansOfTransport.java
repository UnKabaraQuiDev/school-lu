
import java.sql.SQLException;
import java.util.ArrayList;
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
            int pk_mot = entry.getKey();
            mot = new MeanOfTransport(entry.getValue());
            for (Map.Entry<String, String> entry2 : database.loadMotsAttributes(pk_mot).entrySet()) {
                mot.addAttribute(entry2.getKey(), entry2.getValue());
            }
            alMeanOfTransport.add(mot);
        }
        
//        clear();
//        HashMap<Integer, String> dmots = database.loadMots();
//        for (Map.Entry<Integer, String> entry : dmots.entrySet()) {
//            Integer pk_mot = entry.getKey();
//            MeanOfTransport mot = new MeanOfTransport();
//            HashMap<String, String> atts = database.loadMotsAttributes(pk_mot);
//            for (Map.Entry<String, String> entry1 : atts.entrySet()) {
//                String name = entry1.getKey();
//                String value = entry1.getValue();
//                mot.addAttribute(name, value);
//            }
//            alMeanOfTransport.add(mot);
//        }
    }
}
