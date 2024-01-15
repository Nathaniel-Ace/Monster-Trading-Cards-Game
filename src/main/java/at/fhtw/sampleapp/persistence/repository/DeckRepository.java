package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;

import java.util.List;
import java.util.Map;

public interface DeckRepository {

    //public List<Map<String, Object>> getDeck(String username);
    List<?> getDeck(String username, String format);

    public boolean checkUserOwnsCards(String username, List<String> cardIds);

    public void updateDeck(String username, List<String> cardIds);

}
