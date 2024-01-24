package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;

import java.util.List;

public interface BattleRepository {

    List<Card> getDeckforBattle(String username);

    void increaseEloForUser(String username);

    void decreaseEloForUser(String username);

    void increaseWinsForUser(String username);

    void increaseLossesForUser(String username);

    void increaseCoinsforWinnner(String username);

    void increaseTotalGamesForUser(String username);

    void calculateWinRatioForUser(String username);

}
