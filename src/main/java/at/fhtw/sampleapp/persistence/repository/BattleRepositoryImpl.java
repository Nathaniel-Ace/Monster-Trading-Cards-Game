package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.CardElement;
import at.fhtw.sampleapp.model.CardType;
import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BattleRepositoryImpl implements BattleRepository {

    private UnitOfWork unitOfWork;

    public BattleRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public List<Card> getDeckforBattle(String username) {
        List<Card> cards = new ArrayList<>();

        String selectSql = "SELECT card1_id, card2_id, card3_id, card4_id FROM deck WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                for (int i = 1; i <= 4; i++) {
                    String cardId = resultSet.getString("card" + i + "_id");

                    String cardSql = "SELECT id, name, damage, element, type FROM cards WHERE id = ?";
                    PreparedStatement cardStatement = this.unitOfWork.prepareStatement(cardSql);
                    cardStatement.setString(1, cardId);
                    ResultSet cardResultSet = cardStatement.executeQuery();

                    while (cardResultSet.next()) {
                        String id = cardResultSet.getString("id");
                        String name = cardResultSet.getString("name");
                        int damage = cardResultSet.getInt("damage");
                        int elementOrdinal = cardResultSet.getInt("element");
                        CardElement element = CardElement.values()[elementOrdinal];
                        int typeOrdinal = cardResultSet.getInt("type");
                        CardType type = CardType.values()[typeOrdinal];

                        Card card = new Card(id, name, damage, element, type);
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

        return cards;

    }

    public void increaseEloForUser(String username) {

        String updateSql = "UPDATE stats SET elo = elo + 3 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to increase elo for username", e);
        }

    }

    public void decreaseEloForUser(String username) {

        String updateSql = "UPDATE stats SET elo = elo - 5 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to decrease elo for username", e);
        }

    }

    public void increaseWinsForUser(String username) {

        String updateSql = "UPDATE stats SET wins = wins + 1 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to increase wins for username", e);
        }

    }

    public void increaseLossesForUser(String username) {

        String updateSql = "UPDATE stats SET losses = losses + 1 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to increase losses for username", e);
        }

    }

    public void increaseCoinsforWinnner(String username) {

        String updateSql = "UPDATE users SET coins = coins + 2 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to increase coins for username", e);
        }

    }

    public void increaseTotalGamesForUser(String username) {
        String updateSql = "UPDATE stats SET total_games = total_games + 1 WHERE username = ?";
        try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
            updateStatement.setString(1, username);
            updateStatement.executeUpdate();

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to increase total games for username", e);
        }
    }

    public void calculateWinRatioForUser(String username) {
        String selectSql = "SELECT wins, total_games FROM stats WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int wins = resultSet.getInt("wins");
                int totalGames = resultSet.getInt("total_games");

                double winRatio = (double) wins / totalGames;

                String updateSql = "UPDATE stats SET win_ratio = ? WHERE username = ?";
                try (PreparedStatement updateStatement = this.unitOfWork.prepareStatement(updateSql)) {
                    updateStatement.setDouble(1, winRatio);
                    updateStatement.setString(2, username);
                    updateStatement.executeUpdate();

                    unitOfWork.commitTransaction();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to calculate win ratio for username", e);
        }
    }

}
