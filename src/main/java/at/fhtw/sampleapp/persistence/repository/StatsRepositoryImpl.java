package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.DataAccessException;
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
        Map<String, Object> stat = new LinkedHashMap<>();;

        String selectSql = "SELECT username, elo, wins, losses, total_games, win_ratio FROM stats WHERE username = ?";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                stat.put("Username", resultSet.getString("username"));
                stat.put("Elo", resultSet.getInt("elo"));
                stat.put("Wins", resultSet.getInt("wins"));
                stat.put("Losses", resultSet.getInt("losses"));
                stat.put("Total Games", resultSet.getInt("total_games"));
                stat.put("Win Ratio", resultSet.getDouble("win_ratio"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get stats", e);
        }

        return stat;
    }

    public List<Map<String, Object>> getScoreboard() {
        List<Map<String, Object>> stats = new ArrayList<>();

        String selectSql = "SELECT username, elo, wins, losses, total_games, win_ratio FROM stats ORDER BY elo DESC";
        try (PreparedStatement selectStatement = this.unitOfWork.prepareStatement(selectSql)) {
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> stat = new LinkedHashMap<>();
                stat.put("Username", resultSet.getString("username"));
                stat.put("Elo", resultSet.getInt("elo"));
                stat.put("Wins", resultSet.getInt("wins"));
                stat.put("Losses", resultSet.getInt("losses"));
                stat.put("Total Games", resultSet.getInt("total_games"));
                stat.put("Win Ratio", resultSet.getDouble("win_ratio"));
                stats.add(stat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get scoreboard", e);
        }

        return stats;
    }

}
