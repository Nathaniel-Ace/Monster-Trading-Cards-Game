package at.fhtw.sampleapp.persistence.repository;

import java.util.List;
import java.util.Map;

public interface CardRepository {

    public List<Map<String, Object>> getCards(String username);

}
