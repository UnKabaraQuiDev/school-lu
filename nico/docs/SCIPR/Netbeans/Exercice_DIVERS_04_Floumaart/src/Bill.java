
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nicolas
 */
public class Bill {
    
    private ArrayList<Item> alItems = new ArrayList<>();
    
    public void add(Item item, int quantity) {
        for (int i = 0; i < quantity; i++) {
            alItems.add(item);
        }
    }
    
    public void remove(int index) {
        alItems.remove(index);
    }
    
    public void printBill(String paymentMethod) {
        try {
            //String server = "localhost";
            String server = "172.17.250.213";
            int port = 3306;
            String username = "root";
            //String password = "root";
            String password = "STRONG_PASSWORD";
            //String database = "floumaart";
            String database = "fleemarket";
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
            
            try (Connection connect = DriverManager.getConnection(url)) {
                int pk = 0;
                
                String statement = "INSERT INTO bill (fk_cashier, paymentMethod, timeStamp) VALUES (?, ?, ?)";
                                        
                try (PreparedStatement preparedStatement = connect.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setInt(1, 1);
                    preparedStatement.setString(2, paymentMethod);

                    //String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp());
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String timestamp = now.format(formatter);

                    preparedStatement.setString(3, timestamp);
                    
                    System.out.println(preparedStatement);

                    preparedStatement.executeUpdate();
                    var rs = preparedStatement.getGeneratedKeys();
                    rs.first();
                    pk = rs.getInt(1);
                    rs.close();
                    
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }
                
                String statement2 = "INSERT INTO item (price, timestamp, fk_vendor, fk_bill) VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement preparedStatement = connect.prepareStatement(statement2)) {
                    for (int i = 0; i < alItems.size(); i++) {
                        Item item = alItems.get(i);
                        preparedStatement.setInt(1, (int) item.getPrice());
                        
                        //String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp());
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String timestamp = now.format(formatter);
                        
                        preparedStatement.setString(2, timestamp);
                        
                        //preparedStatement.setString(3, item.getVendorCode());
                        preparedStatement.setString(3, item.getVendorCode());
                        preparedStatement.setInt(4, pk);
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

    public Object[] toArray() {
        return alItems.toArray();
    }
    
    public double getTotal() {
        double total = 0;
        for (int i = 0; i < alItems.size(); i++) {
            Item item = alItems.get(i);
            total += item.getPrice();
        }
        return total;
    }
}
