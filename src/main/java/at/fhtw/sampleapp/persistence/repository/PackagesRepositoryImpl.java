package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PackagesRepositoryImpl implements PackagesRepository {

    private UnitOfWork unitOfWork;

    public PackagesRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public boolean cardExists(String cardId) {
        String selectSql = "SELECT COUNT(*) FROM cards WHERE id = ?";
        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(selectSql)) {
            preparedStatement.setString(1, cardId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error checking if card exists", e);
        }
    }

    public void addCards(List<Card> cards) {
        String insertCardsSql = "INSERT INTO cards (id, name, damage, element, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatementCards = this.unitOfWork.prepareStatement(insertCardsSql))
        {
            for (Card card : cards) {
                if (cardExists(card.getId())) {
                    throw new DataAccessException("Card already exists");
                } else {
                    preparedStatementCards.setString(1, card.getId());
                    preparedStatementCards.setString(2, card.getName());
                    preparedStatementCards.setInt(3, card.getDamage());
                    preparedStatementCards.setInt(4, card.getElement());
                    preparedStatementCards.setInt(5, card.getType());
                    preparedStatementCards.addBatch();
                }
            }
            preparedStatementCards.executeBatch();

            String insertPackagesSql = "INSERT INTO packages (card1_id, card2_id, card3_id, card4_id, card5_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementPackages = this.unitOfWork.prepareStatement(insertPackagesSql);
            for (int i = 0; i < cards.size(); i++) {
                preparedStatementPackages.setString(i + 1, cards.get(i).getId());
            }
            preparedStatementPackages.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Insert not successful", e);
        }
    }

}