
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class TelephoneBook {
    
    private ArrayList<Person> alPersons = new ArrayList<>();

    public int size() {
        return alPersons.size();
    }

    public boolean add(Person e) {
        return alPersons.add(e);
    }

    public boolean remove(Object o) {
        return alPersons.remove(o);
    }

    public Person remove(int index) {
        return alPersons.remove(index);
    }

    public void clear() {
        alPersons.clear();
    }

    public void sort() {
        Collections.sort(alPersons);
    }

    public Person get(int index) {
        return alPersons.get(index);
    }

    public boolean isEmpty() {
        return alPersons.isEmpty();
    }
    
    public void loadFromDB() {
        try {
            String server = "localhost";
            int port = 3306;
            String username = "root";
            String password = "root";
            String database = "telephonebook";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
            
            String url2 = "jdbc:mysql://" + server + ":" + port + "/" + database;
            
            //try (Connection connect = DriverManager.getConnection(url)) {
                
            try (Connection connect = DriverManager.getConnection(url2, username, password)) {
                
                try (Statement statement = connect.createStatement()) {
                    
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM person");
                    
                    while (resultSet.next()) {
                        String fullName = resultSet.getString("fullName");
                        String telNr = resultSet.getString("telNr");
                        String email = resultSet.getString("email");
                        Person person = new Person(fullName, telNr, email);
                        alPersons.add(person);
                        System.out.println(fullName);
                    }
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(DatabaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveToDB() {
        try {
            String server = "localhost";
            int port = 3306;
            String username = "root";
            String password = "root";
            String database = "telephonebook";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
            
            String url2 = "jdbc:mysql://" + server + ":" + port + "/" + database;
            
            //try (Connection connect = DriverManager.getConnection(url)) {
                
            try (Connection connect = DriverManager.getConnection(url2, username, password)) {
                
                try (Statement statement = connect.createStatement()) {
                    statement.executeUpdate("DELETE FROM person");
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }
                String statement = "INSERT INTO person (fullName, telNr, email) VALUES (?, ?, ?)";
                
                try (PreparedStatement preparedStatement = connect.prepareStatement(statement)) {
                    for (int i = 0; i < alPersons.size(); i++) {
                        Person person = alPersons.get(i);
                        preparedStatement.setString(1, person.getFullName());
                        preparedStatement.setString(3, person.getTelNr());
                        preparedStatement.setString(2, person.getEmail());
                        preparedStatement.executeUpdate();
                    }
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(DatabaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
