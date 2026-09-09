
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.*;
import java.util.HashMap;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Database {
    
    private Connection connection;
    
    public Database() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:mots.db");
    }
    
    public HashMap<Integer, String> loadMots() throws ClassNotFoundException, SQLException {
        
        HashMap<Integer, String> hmResult = new HashMap<>();
        
        String server = "localhost";
        int port = 3306;
        String username = "root";
        String password = "root";
        String database = "meansoftransport";
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
        
        try (Connection connect = DriverManager.getConnection(url)) {
            
            try (Statement statement = connect.createStatement()) {
                
                ResultSet resultSet = statement.executeQuery("SELECT * FROM mot;");
                
                while (resultSet.next()) {
                    int key = resultSet.getInt("pk_mot");
                    String brand = resultSet.getString("brand");
                    String name = resultSet.getString("name");
                    String value = brand + " " + name;
                    hmResult.put(key, value);
                }
            }
        }
        return hmResult;
    }
    
    public HashMap<String, String> loadMotsAttributes(int pk_mot) throws ClassNotFoundException, SQLException {
        
        HashMap<String, String> hmResult = new HashMap<>();
        
        String server = "localhost";
        int port = 3306;
        String username = "root";
        String password = "root";
        String database = "meansoftransport";
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
        
        try (Connection connect = DriverManager.getConnection(url)) {
            
            try (Statement statement = connect.createStatement()) {
                
                ResultSet resultSet = statement.executeQuery("SELECT * FROM attribute WHERE fk_mot = " + pk_mot + ";");
                
                while (resultSet.next()) {
                    String key = resultSet.getString("name");
                    String value = resultSet.getString("value");
                    hmResult.put(key, value);
                }
            }
        }
        return hmResult;
    }
}
