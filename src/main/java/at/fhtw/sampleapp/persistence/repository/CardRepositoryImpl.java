package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;

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

    public List<Map<String, Object>> getCards(String username) {
        List<Map<String, Object>> cards = new ArrayList<>();
        String sql = "SELECT card_id FROM bought_cards WHERE username = ?";
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql))
        {
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
                    card.put("Damage", cardResultSet.getInt("damage"));
                    cards.add(card);
                }
            }
            // Check if cards is still empty after processing the ResultSet
            if (cards.isEmpty()) {
                //System.out.println("No cards found");
                throw new DataAccessException("No cards found");
            }
            return cards;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get cards", e);
        }
    }

}
