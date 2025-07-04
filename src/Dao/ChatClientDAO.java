package Dao;

import Database.MySqlConnection;
import Model.MessageModel;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatClientDAO {
    private final MySqlConnection db = new MySqlConnection();

    public List<MessageModel> getAllUsers() {
        List<MessageModel> userList = new ArrayList<>();
        String sql = "SELECT first_name, last_name FROM users";
        Connection conn = db.openConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MessageModel user = new MessageModel(); // If you meant User, rename your model
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }

        return userList;
    }
    
    
    
    public String getEmail(String firstName, String lastName) {
        Connection conn = db.openConnection();
        try {
            String sql = "SELECT email FROM users WHERE first_name=? and last_name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("email"); // return the found OTP code
            } else {
                return null; // OTP not found
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        } finally {
            db.closeConnection(conn);
        }
    }
    
    
    public String getFirstnLastName(String email) {
        Connection conn = db.openConnection();
        try {
            String sql = "SELECT first_name, last_name FROM users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                return firstName + " " + lastName;// return the found OTP code
            } else {
                return null; // OTP not found
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        } finally {
            db.closeConnection(conn);
        }
    }
    
    public boolean updateUserImagePath(String email, String imagePath) {
        Connection conn = db.openConnection();
        try {
            String sql = "UPDATE users SET picture_path = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, imagePath);
            stmt.setString(2, email);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;  // true if update succeeded
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getImagePath(String email) {
        Connection conn = db.openConnection();
        try {
            String sql = "SELECT picture_path FROM users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String imagePath = rs.getString("picture_path");
                return imagePath;// return the imagePath
            } else {
                return null; // imagepath not found
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        } finally {
            db.closeConnection(conn);
        }
    }  
 
    public boolean saveMessage(MessageModel messageModel) {
        Connection conn = db.openConnection();
   

        try{
            String sql = "INSERT INTO messages (sender_email, receiver_email, content, timestamp) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, messageModel.getSender());
            ps.setString(2, messageModel.getReceiver());
            ps.setString(3, messageModel.getMessage());
            ps.setTimestamp(4, Timestamp.valueOf(messageModel.getTimeStamp()));
            
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public List<MessageModel> getChatHistory(String user1, String user2) {
        Connection conn = db.openConnection();
        List<MessageModel> history = new ArrayList<>();

        try {
            String sql = "SELECT * FROM messages WHERE "
                    + "((sender_email = ? AND receiver_email = ?) OR (sender_email = ? AND receiver_email = ?)) "
                    + "ORDER BY timestamp";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(new MessageModel(
                        rs.getString("sender_email"),
                        rs.getString("receiver_email"),
                        rs.getString("content")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}