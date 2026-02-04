package shopbancancau.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import shopbancancau.model.User;

public class UserDAO {
    
    
    private boolean columnExists(String tableName, String columnName) {
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, columnName);
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
              
                try {
                    user.setFullname(rs.getString("fullname"));
                    user.setPhone(rs.getString("phone"));
                } catch (Exception e) {
                    
                }
                return user;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public void createUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createUser(String username, String password, String role, String fullname, String phone) {
        String sql = "INSERT INTO users(username, password, role, fullname, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, fullname);
            ps.setString(5, phone);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                
                try {
                    user.setFullname(rs.getString("fullname"));
                    user.setPhone(rs.getString("phone"));
                } catch (Exception e) {
                    
                }
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
               
                try {
                    user.setFullname(rs.getString("fullname"));
                    user.setPhone(rs.getString("phone"));
                } catch (Exception e) {
                   
                }
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }

    public void updateUser(int userId, String password, String fullname, String phone, String role) {
        boolean hasFullname = columnExists("users", "fullname");
        boolean hasPhone = columnExists("users", "phone");
        
        String sql;
        PreparedStatement ps;
        
        try (Connection conn = DBConnection.getConnection()) {
            if (hasFullname && hasPhone) {
                
                sql = "UPDATE users SET password = ?, fullname = ?, phone = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, password);
                ps.setString(2, fullname);
                ps.setString(3, phone);
                ps.setString(4, role);
                ps.setInt(5, userId);
            } else if (hasFullname) {
                
                sql = "UPDATE users SET password = ?, fullname = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, password);
                ps.setString(2, fullname);
                ps.setString(3, role);
                ps.setInt(4, userId);
            } else if (hasPhone) {
                
                sql = "UPDATE users SET password = ?, phone = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, password);
                ps.setString(2, phone);
                ps.setString(3, role);
                ps.setInt(4, userId);
            } else {
                
                sql = "UPDATE users SET password = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, password);
                ps.setString(2, role);
                ps.setInt(3, userId);
            }
            
            ps.executeUpdate();
            ps.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật người dùng: " + e.getMessage(), e);
        }
    }

    public void updateUser(int userId, String username, String password, String fullname, String phone, String role) {
        boolean hasFullname = columnExists("users", "fullname");
        boolean hasPhone = columnExists("users", "phone");
        
        String sql;
        PreparedStatement ps;
        
        try (Connection conn = DBConnection.getConnection()) {
            if (hasFullname && hasPhone) {
                
                sql = "UPDATE users SET username = ?, password = ?, fullname = ?, phone = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, fullname);
                ps.setString(4, phone);
                ps.setString(5, role);
                ps.setInt(6, userId);
            } else if (hasFullname) {
               
                sql = "UPDATE users SET username = ?, password = ?, fullname = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, fullname);
                ps.setString(4, role);
                ps.setInt(5, userId);
            } else if (hasPhone) {
               
                sql = "UPDATE users SET username = ?, password = ?, phone = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, phone);
                ps.setString(4, role);
                ps.setInt(5, userId);
            } else {
               
                sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                ps.setInt(4, userId);
            }
            
            ps.executeUpdate();
            ps.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật người dùng: " + e.getMessage(), e);
        }
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void insertUser(String username, String role) {
       
        String defaultPassword = "123456";
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, defaultPassword);
            ps.setString(3, role);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRole(int userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);
            ps.setInt(2, userId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(int userId, String username, String role) {
        String sql = "UPDATE users SET username = ?, role = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, role);
            ps.setInt(3, userId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean usernameExistsExcept(String username, int exceptUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, exceptUserId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
