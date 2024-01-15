package at.fhtw.sampleapp.persistence.repository;

import java.util.List;

public interface BattleRepository {

    List<?> getDeck(String username);

}
