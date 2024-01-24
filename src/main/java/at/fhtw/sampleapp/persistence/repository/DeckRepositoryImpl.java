package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class DeckRepositoryImpl implements DeckRepository {

    private UnitOfWork unitOfWork;

    public DeckRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public List<?> selectDeck(String username, String format) {
        List<Map<String, Object>> cards = new ArrayList<>();
        // Prepare SQL SELECT statement
        String selectSql = "SELECT card1_id, card2_id, card3_id, card4_id FROM deck WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            // Set username parameter in the prepared statement
            selectStatement.setString(1, username);
            // Execute the prepared statement
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                for (int i = 1; i <= 4; i++) {
                    String cardId = resultSet.getString("card" + i + "_id");

                    String cardSql = "SELECT id, name, damage, element FROM cards WHERE id = ?";
                    PreparedStatement cardStatement = this.unitOfWork.prepareStatement(cardSql);
                    cardStatement.setString(1, cardId);
                    ResultSet cardResultSet = cardStatement.executeQuery();

                    while (cardResultSet.next()) {
                        Map<String, Object> card = new HashMap<>();
                        card.put("ID", cardResultSet.getString("id"));
                        card.put("Name", cardResultSet.getString("name"));
                        card.put("Damage", cardResultSet.getInt("damage"));
                        //card.put("Element", cardResultSet.getInt("element"));
                        cards.add(card);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to select cards from deck for username", e);
        }

        if (cards.isEmpty()) {
            throw new DataAccessException("No cards found");
        }

        if ("plain".equals(format)) {
            // return the deck in plain format
            return cards.stream().map(card -> "Name: " + card.get("Name").toString() +
                    ", Damage: " + card.get("Damage").toString()).
                    collect(Collectors.toList());
        } else {
            // return the deck in the original format
            return cards;
        }
    }

    public List<?> getDeck(String username, String format) {
        List<?> deck;
        try {
            // get cards from deck table
            deck = selectDeck(username, format);

        } catch (DataAccessException e) {
            e.printStackTrace();
            throw e;  // Rethrow the original DataAccessException
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get deck", e);
        }
        return deck;
    }

    public boolean checkUserOwnsCards(String username, List<String> cardIds) {
        // Generate placeholders for IN clause
        String placeholders = String.join(",", Collections.nCopies(cardIds.size(), "?"));

        // Prepare SQL SELECT statement
        String selectSql = "SELECT card_id FROM bought_cards WHERE username = ? AND card_id IN (" + placeholders + ")";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            // Set username parameter in the prepared statement
            selectStatement.setString(1, username);
            // Set card ID parameters in the prepared statement
            for (int i = 0; i < cardIds.size(); i++) {
                selectStatement.setString(i + 2, cardIds.get(i));
            }
            // Execute the prepared statement
            ResultSet resultSet = selectStatement.executeQuery();

            // Count the number of rows in the resultSet
            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            // If the number of rows is equal to the size of cardIds, then the user owns all the cards
            return rowCount == cardIds.size();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to check if user owns cards", e);
        }

    }

    public void updateDeck(String username, List<String> cardIds) {

        System.out.println("updateDeck: " + cardIds);

        // Prepare SQL UPDATE statement
        String updateSql = "UPDATE deck SET card1_id = ?, card2_id = ?, card3_id = ?, card4_id = ? WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            // Set card ID parameters in the prepared statement
            for (int i = 0; i < cardIds.size(); i++) {
                updateStatement.setString(i + 1, cardIds.get(i));
            }
            // Set username parameter in the prepared statement
            updateStatement.setString(5, username);
            // Execute the prepared statement
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction();
            System.out.println("updateDeck done");
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to update deck for username", e);
        }
    }

}
