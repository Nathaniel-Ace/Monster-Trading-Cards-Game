package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DatabaseManager;
import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StatsRepositoryImpl implements StatsRepository{

    private UnitOfWork unitOfWork;

    public StatsRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public Map<String, Object> getStats(String username) {
        Map<String, Object> stat = new LinkedHashMap<>();
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            String selectSql = "SELECT username, elo, wins, losses FROM stats WHERE username = ?";
            try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
                selectStatement.setString(1, username);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    stat.put("Username", resultSet.getString("username"));
                    stat.put("Elo", resultSet.getInt("elo"));
                    stat.put("Wins", resultSet.getInt("wins"));
                    stat.put("Losses", resultSet.getInt("losses"));
                }

                unitOfWork.commitTransaction();
            } catch (SQLException e) {
                unitOfWork.rollbackTransaction();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stat;
    }

    public List<Map<String, Object>> getScoreboard() {
        List<Map<String, Object>> stats = new ArrayList<>();
        try {
            Connection conn = DatabaseManager.INSTANCE.getConnection();
            conn.setAutoCommit(false);
            unitOfWork.setConnection(conn);

            String selectSql = "SELECT username, elo, wins, losses FROM stats ORDER BY elo DESC";
            try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
                ResultSet resultSet = selectStatement.executeQuery();

                while (resultSet.next()) {
                    Map<String, Object> stat = new LinkedHashMap<>();
                    stat.put("Username", resultSet.getString("username"));
                    stat.put("Elo", resultSet.getInt("elo"));
                    stat.put("Wins", resultSet.getInt("wins"));
                    stat.put("Losses", resultSet.getInt("losses"));
                    stats.add(stat);
                }

                unitOfWork.commitTransaction();
            } catch (SQLException e) {
                unitOfWork.rollbackTransaction();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stats;
    }

}
