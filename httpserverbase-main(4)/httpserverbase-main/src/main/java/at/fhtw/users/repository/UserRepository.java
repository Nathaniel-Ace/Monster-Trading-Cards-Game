package at.fhtw.users.repository;

import at.fhtw.database.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserRepository {
    public static void printUsers(Connection conn) {
        try {
            // Create a SQL statement
            Statement statement = conn.createStatement();

            // Execute a query to select all rows from the "public.users" table
            String query = "SELECT * FROM public.users";
            ResultSet resultSet = statement.executeQuery(query);

            // Iterate through the result set and print the data
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String userName = resultSet.getString("username");
                String password = resultSet.getString("password");
                int coins = resultSet.getInt("coins");
                // Add more columns as needed

                System.out.println("User ID: " + userId);
                System.out.println("Username: " + userName);
                System.out.println("Password: " + password);
                System.out.println("Coins: " + coins);
                // Print more columns as needed
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DBConnect db = new DBConnect();
        // Provide your established database connection here
        Connection conn = db.connect_to_db("swen1db", "postgres", "nathaniel17");

        if (conn != null) {


            // Call the printUsers method with the existing connection
            printUsers(conn);

            // You may close the database connection here if needed
        } else {
            System.out.println("Connection failed");
        }
    }
}
