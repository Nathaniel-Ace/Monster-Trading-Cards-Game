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

    public User findByUsername(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username = ?";

            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
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

                if (user == null) {
                    throw new DataAccessException("No user found");
                }

                return user;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
    }

    public void addUser(User user) {
        try {
            // Check if a user with the same username already exists
            User existingUser = null;
            try {
                existingUser = findByUsername(user.getUsername());
            } catch (DataAccessException e) {
                if (!e.getMessage().equals("No user found")) {
                    throw e;
                }
            }
            if (existingUser != null) {
                throw new DataAccessException("Username already taken");
            }

            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Insert nicht erfolgreich", e);
            }

            // Commit the transaction
            this.unitOfWork.commitTransaction();

        } catch (DataAccessException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw e;
        }
    }

    // This method inserts the username into the deck table
    public void insertUsernameDeck(String username) {
        try {
            String insertSql = "INSERT INTO deck (username) VALUES (?)";
            try (PreparedStatement insertStatement = this.unitOfWork.prepareStatement(insertSql)) {
                insertStatement.setString(1, username);
                insertStatement.executeUpdate();
            }
            System.out.println("Inserted username into deck table");

            // Commit the transaction
            this.unitOfWork.commitTransaction();

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to insert username into deck", e);
        }
    }

    // This method inserts the username into the stats table
    public void insertUsernameStats(String username) {
        try {
            String insertSql = "INSERT INTO stats (username) VALUES (?)";
            try (PreparedStatement insertStatement = this.unitOfWork.prepareStatement(insertSql)) {
                insertStatement.setString(1, username);
                insertStatement.executeUpdate();
            }
            System.out.println("Inserted username into stats table");

            // Commit the transaction
            this.unitOfWork.commitTransaction();

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to insert username into stats", e);
        }
    }

    public void editUser(String username, User user) {
        try {
            User existingUser = findByUsername(user.getUsername());
            if (existingUser == null) {
                throw new DataAccessException("No user found");
            }

            String sql = "UPDATE public.users SET name = ?, bio = ?, image = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getBio());
                preparedStatement.setString(3, user.getImage());
                preparedStatement.setString(4, username);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating user failed, no rows affected.");
                }
            }

            // Commit the transaction
            this.unitOfWork.commitTransaction();

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Update not successful", e);
        }
    }

}