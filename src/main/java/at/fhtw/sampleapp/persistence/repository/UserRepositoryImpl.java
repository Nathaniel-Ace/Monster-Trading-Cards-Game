package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepositoryImpl implements UserRepository {

    private UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public User findById(int id) {
        try {
            Connection conn  = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);
            String sql = "SELECT * FROM public.users WHERE user_id = ?";

            PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql);
            preparedStatement.setInt(1, id);

//            Print the SQL query
//            System.out.println("Executing SQL query: " + preparedStatement.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            while(resultSet.next()) {
                user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3));
            }
            unitOfWork.commitTransaction();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }

    public User findByUsername(String username) {
        try {
            Connection conn  = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);
            String sql = "SELECT * FROM public.users WHERE username = ?";

            PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql);
            preparedStatement.setString(1, username);

            // Print the SQL query
            System.out.println("Executing SQL query: " + preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            while(resultSet.next()) {
                user = new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"));
                user.setCoins(resultSet.getInt("coins"));
                user.setName(resultSet.getString("name"));
                user.setBio(resultSet.getString("bio"));
                user.setImage(resultSet.getString("image"));
            }

            unitOfWork.commitTransaction();

//            if (user == null) {
//                throw new DataAccessException("No user found");
//            }

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }

//    @Override
//    public Collection<User> findAllUsers() {
//        Connection conn  = DatabaseManager.INSTANCE.getConnection();
//        unitOfWork.setConnection(conn);
//        try (PreparedStatement preparedStatement =
//                     this.unitOfWork.prepareStatement("SELECT * FROM public.users"))
//        {
//            ResultSet resultSet = preparedStatement.executeQuery();
//            Collection<User> userRows = new ArrayList<>();
//            while(resultSet.next())
//            {
//                User user = new User(
//                        resultSet.getInt(1),
//                        resultSet.getString(2),
//                        resultSet.getString(3));
//                userRows.add(user);
//            }
//            return userRows;
//        } catch (SQLException e) {
//            throw new DataAccessException("Select nicht erfolgreich", e);
//        }
//    }

    public void addUser(User user) {

        try {
            // Check if a user with the same username already exists
            User existingUser = findByUsername(user.getUsername());
            if (existingUser != null) {
                throw new DataAccessException("Username already taken");
            }

            Connection conn  = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

            PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Insert nicht erfolgreich", e);
        }
    }

    // This method inserts the username into the deck table
    public void insertUsernameDeck(String username) {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Prepare SQL INSERT statement
            String insertSql = "INSERT INTO deck (username) VALUES (?)";
            try (PreparedStatement insertStatement = this.unitOfWork.prepareStatement(insertSql)) {
                // Set username parameter in the prepared statement
                insertStatement.setString(1, username);
                // Execute the prepared statement
                insertStatement.executeUpdate();
            }
            System.out.println("Inserted username into deck table");
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to insert username into deck", e);
        }
    }

    // This method inserts the username into the stats table
    public void  insertUsernameStats(String username) {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Prepare SQL INSERT statement
            String insertSql = "INSERT INTO stats (username) VALUES (?)";
            try (PreparedStatement insertStatement = this.unitOfWork.prepareStatement(insertSql)) {
                // Set username parameter in the prepared statement
                insertStatement.setString(1, username);
                // Execute the prepared statement
                insertStatement.executeUpdate();
            }
            System.out.println("Inserted username into stats table");
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to insert username into stats", e);
        }
    }

    public void editUser(String username, User user) {
        try {
            Connection conn  = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Check if the user exists
            User existingUser = findByUsername(user.getUsername());
            if (existingUser == null) {
                throw new DataAccessException("No user found");
            }

            String sql = "UPDATE public.users SET name = ?, bio = ?, image = ? WHERE username = ?";

            PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getBio());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setString(4, username);

            // Print the SQL query
            System.out.println("Executing SQL query: " + preparedStatement);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Update not successful", e);
        }
    }

}