package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionRepositoryImpl implements TransactionRepository{

    private UnitOfWork unitOfWork;

    public TransactionRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public List<Map<String, Object>> selectCards() {
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            Connection conn  = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);
            String sql = "SELECT * FROM packages LIMIT 1";

            PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                rows.add(row);
                System.out.println(row);
            }
            unitOfWork.commitTransaction();

            // Check if rows is empty
            if (rows.isEmpty()) {
                throw new DataAccessException("No packages found");
            }



        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Select nicht erfolgreich", e);
        }
        return rows;
    }

    public boolean enoughCoins(String username) {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);
            String sql = "SELECT coins FROM users WHERE username = ?";

            PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int coins = rs.getInt("coins");
                return coins < 5;
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to retrieve coins", e);
        }
        return true;
    }

//    public static void main(String[] args) {
//        TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl(new UnitOfWork());
//        String testUsername = "admin"; // replace with a valid username
//        boolean result = transactionRepository.enoughCoins(testUsername);
//        System.out.println("Does user " + testUsername + " have more than 5 coins? " + result);
//    }

    public void deductCoins(String username) {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Step 1: Check if user has enough coins
            if (enoughCoins(username)) {
                throw new DataAccessException("Not enough coins");
            }

            // Step 2: Deduct 5 coins from the user's total coins
            String updateSql = "UPDATE users SET coins = coins - 5 WHERE username = ?";
            try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
                updateStatement.setString(1, username);
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DataAccessException("Failed to deduct coins");
                }
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to deduct coins", e);
        }
    }

//    public static void main(String[] args) {
//        TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl(new UnitOfWork());
//        String testUsername = "admin"; // replace with a valid username
//        transactionRepository.deductCoins(testUsername);
//        System.out.println("5 coins deducted from user " + testUsername);
//    }

    public void insertCards(String username) {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Retrieve the first row from the previously selected packages
            List<Map<String, Object>> selectedCards = selectCards();

            // Insert cards into bought_cards table
            String insertSql = "INSERT INTO bought_cards (username, card_id) VALUES (?, ?)";
            for (Map<String, Object> card : selectedCards) {
                try (PreparedStatement insertStatement = this.unitOfWork.prepareStatement(insertSql)) {
                    for (int i = 1; i <= 5; i++) {
                        String columnName = "card" + i + "_id";
                        System.out.println("Inserting card with ID " + card.get(columnName));
                        Object cardIdObj = card.get(columnName);
                        if (cardIdObj == null) {
                            throw new DataAccessException("Card ID is null");
                        }
                        insertStatement.setString(1, username);
                        insertStatement.setString(2, (String) cardIdObj);
                        insertStatement.executeUpdate();
                        unitOfWork.commitTransaction();

                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to insert cards", e);
        }
    }

//    public static void main(String[] args) {
//        TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl(new UnitOfWork());
//        String testUsername = "admin"; // replace with a valid username
//        transactionRepository.insertCards(testUsername);
//        System.out.println("Cards inserted for user " + testUsername);
//    }

    public void deletePackage() {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Retrieve the first row from the previously selected packages
            List<Map<String, Object>> selectedCards = selectCards();

            // Get the ID of the first row
            int packageId = (int) selectedCards.get(0).get("package_id");

            // Delete the first row from the packages table
            String deleteSql = "DELETE FROM packages WHERE package_id = ?";
            //PreparedStatement is used within a try-with-resources statement,
            // which automatically closes the resources when the block is exited
            try (PreparedStatement deleteStatement = this.unitOfWork.prepareStatement(deleteSql)) {
                deleteStatement.setInt(1, packageId);
                int rowsAffected = deleteStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DataAccessException("Failed to delete the package");
                }
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to delete package", e);
        }
    }

//    public static void main(String[] args) {
//        TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl(new UnitOfWork());
//        transactionRepository.deletePackage();
//        System.out.println("Package deleted successfully");
//    }

    public List<String> selectFourCards(String username) {
        List<String> cards = new ArrayList<>();
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Prepare SQL SELECT statement
            String selectSql = "SELECT card_id FROM bought_cards WHERE username = ? LIMIT 4";
            try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
                // Set username parameter in the prepared statement
                selectStatement.setString(1, username);
                // Execute the prepared statement
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    cards.add(resultSet.getString("card_id"));
                }
            }

            unitOfWork.commitTransaction();
            if (cards.isEmpty()) {
                throw new DataAccessException("No cards found");
            }
            System.out.println("Selected cards for username " + username + ": " + cards);
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to select cards for username", e);
        }
        return cards;
    }

    public void updateCardsIntoDeck(String username) {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Get the list of card IDs
            List<String> cards = selectFourCards(username);

            // Prepare SQL UPDATE statement
            String updateSql = "UPDATE deck SET card1_id = ?, card2_id = ?, card3_id = ?, card4_id = ? WHERE username = ?";
            try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
                for (int i = 0; i < cards.size(); i++) {
                    updateStatement.setString(i + 1, cards.get(i));
                }
                // Set username parameter in the prepared statement
                updateStatement.setString(5, username);
                // Execute the prepared statement
                updateStatement.executeUpdate();
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to update cards into deck for username", e);
        }
    }

    public void acquirePackages(String username) {
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            // Step 1: Select cards
            System.out.println("Selecting cards...");
            List<Map<String, Object>> selectedCards = selectCards();

            // Step 2: Check if user has enough coins
            System.out.println("Checking if user has enough coins...");
            if (enoughCoins(username)) {
                throw new DataAccessException("Not enough coins");
            }

            // Step 3: Insert cards into bought_cards table
            System.out.println("Inserting cards into bought_cards table...");
            insertCards(username);

            // Step 4: Delete package
            System.out.println("Deleting package...");
            deletePackage();

            // Step 5: Deduct 5 coins from the user's total coins
            System.out.println("Deducting coins from user's total coins...");
            deductCoins(username);

            // Step 6: Update cards into deck
            System.out.println("Updating cards into deck...");
            updateCardsIntoDeck(username);

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to acquire packages", e);
        }
    }

//    public static void main(String[] args) {
//        TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl(new UnitOfWork());
//        String testUsername = "admin"; // replace with a valid username
//        transactionRepository.acquirePackages(testUsername);
//        System.out.println("Packages acquired and coins deducted for user " + testUsername);
//    }

}
