package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardRepositoryImpl implements CardRepository{

    private UnitOfWork unitOfWork;

    public CardRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

//    public List<String> selectFourCards(String username) {
//        List<String> cards = new ArrayList<>();
//        try {
//            Connection conn = DatabaseManager.INSTANCE.getConnection();
//            conn.setAutoCommit(false);
//            unitOfWork.setConnection(conn);
//
//            // Prepare SQL SELECT statement
//            String selectSql = "SELECT card_id FROM bought_cards WHERE username = ? LIMIT 4";
//            try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
//                // Set username parameter in the prepared statement
//                selectStatement.setString(1, username);
//                // Execute the prepared statement
//                ResultSet resultSet = selectStatement.executeQuery();
//                while (resultSet.next()) {
//                    cards.add(resultSet.getString("card_id"));
//                }
//            }
//
//            unitOfWork.commitTransaction();
//            if (cards.isEmpty()) {
//                throw new DataAccessException("No cards found");
//            }
//            System.out.println("Selected cards for username " + username + ": " + cards);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            unitOfWork.rollbackTransaction();
//            throw new DataAccessException("Failed to select cards for username", e);
//        }
//        return cards;
//    }
//
//    public void updateCardsIntoDeck(String username) {
//        try {
//            Connection conn = DatabaseManager.INSTANCE.getConnection();
//            conn.setAutoCommit(false);
//            unitOfWork.setConnection(conn);
//
//            // Get the list of card IDs
//            List<String> cards = selectFourCards(username);
//
//            // Prepare SQL UPDATE statement
//            String updateSql = "UPDATE deck SET card1_id = ?, card2_id = ?, card3_id = ?, card4_id = ? WHERE username = ?";
//            try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
//                for (int i = 0; i < cards.size(); i++) {
//                    updateStatement.setString(i + 1, cards.get(i));
//                }
//                // Set username parameter in the prepared statement
//                updateStatement.setString(5, username);
//                // Execute the prepared statement
//                updateStatement.executeUpdate();
//            }
//
//            unitOfWork.commitTransaction();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            unitOfWork.rollbackTransaction();
//            throw new DataAccessException("Failed to update cards into deck for username", e);
//        }
//    }

    public List<Map<String, Object>> getCards(String username) {
        List<Map<String, Object>> cards = new ArrayList<>();
        try {
            Connection conn  = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);
            String sql = "SELECT card_id FROM bought_cards WHERE username = ?";

            PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String cardId = resultSet.getString("card_id");

                String cardSql = "SELECT id, name, damage FROM cards WHERE id = ?";
                PreparedStatement cardStatement = this.unitOfWork.prepareStatement(cardSql);
                cardStatement.setString(1, cardId);
                ResultSet cardResultSet = cardStatement.executeQuery();

                while (cardResultSet.next()) {
                    Map<String, Object> card = new HashMap<>();
                    card.put("ID", cardResultSet.getString("id"));
                    card.put("Name", cardResultSet.getString("name"));
                    card.put("Damage", cardResultSet.getDouble("damage"));
                    cards.add(card);
                }
            }

            // Check if cards is still empty after processing the ResultSet
            if (cards.isEmpty()) {
                //System.out.println("No cards found");
                throw new DataAccessException("No cards found");
            }

            // Put the first 4 cards into the deck table
            //updateCardsIntoDeck(username);

            unitOfWork.commitTransaction();
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to get cards", e);
        }
    }

//    public static void main(String[] args) {
//        CardRepositoryImpl cardRepository = new CardRepositoryImpl(new UnitOfWork());
//        String testUsername = "altenhof"; // replace with a valid username
//        List<Map<String, Object>> cards = cardRepository.getCards(testUsername);
//        for (Map<String, Object> card : cards) {
//            System.out.println("ID: " + card.get("ID"));
//            System.out.println("Name: " + card.get("Name"));
//            System.out.println("Damage: " + card.get("Damage"));
//            System.out.println("------------------------");
//        }
//    }
}
