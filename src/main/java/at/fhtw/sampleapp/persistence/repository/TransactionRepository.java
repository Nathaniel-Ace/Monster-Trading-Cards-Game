package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.Card;


import java.util.List;
import java.util.Map;

public interface TransactionRepository {

    List<Map<String, Object>> selectCards();

    void acquirePackages(String username);


}
