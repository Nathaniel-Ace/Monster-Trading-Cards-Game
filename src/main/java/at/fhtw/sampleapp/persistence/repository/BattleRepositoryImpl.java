package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.persistence.UnitOfWork;

import java.util.List;

public class BattleRepositoryImpl implements BattleRepository {

    private DeckRepository deckRepository;

    public BattleRepositoryImpl(UnitOfWork unitOfWork) {
        this.deckRepository = new DeckRepositoryImpl(unitOfWork);
    }

    public List<?> getDeck(String username) {
        return deckRepository.getDeck(username, "standard");
    }
}
