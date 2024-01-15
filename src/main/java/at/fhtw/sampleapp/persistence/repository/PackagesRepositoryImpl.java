package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.Connection;
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
        String selectSql = "SELECT COUNT(*) FROM \"cards\" WHERE id = ?";
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
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);

                if (cardExists(card.getId())) {
                    throw new DataAccessException("Card with id " + card.getId() + " already exists");
                }

                // INSERT-Statement für "cards"-Tabelle
                String insertCardsSql = "INSERT INTO \"cards\" (id, name, damage, type) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatementCards = this.unitOfWork.prepareStatement(insertCardsSql)) {
                    preparedStatementCards.setString(1, card.getId());
                    preparedStatementCards.setString(2, card.getName());
                    preparedStatementCards.setInt(3, card.getDamage());
                    preparedStatementCards.setInt(4, card.getType());
                    preparedStatementCards.executeUpdate();
                }
            }
            // INSERT-Statement für "packages"-Tabelle
            String insertPackagesSql = "INSERT INTO \"packages\" (card1_id, card2_id, card3_id, card4_id, card5_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatementPackages = this.unitOfWork.prepareStatement(insertPackagesSql)) {
                for (int i = 0; i < cards.size(); i++) {
                    preparedStatementPackages.setString(i + 1, cards.get(i).getId());
                }
                preparedStatementPackages.executeUpdate();
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Insert nicht erfolgreich", e);
        }
    }

}
