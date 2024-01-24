package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.StatsRepository;
import at.fhtw.sampleapp.persistence.repository.StatsRepositoryImpl;

import java.util.List;
import java.util.Map;

public class StatsService extends AbstractService{

    private StatsRepository getStatsRepository(UnitOfWork unitOfWork) {
        return new StatsRepositoryImpl(unitOfWork);
    }

    public Map<String, Object> getStats(String username) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            StatsRepository statsRepository = getStatsRepository(unitOfWork);
            // Call the getStats method from the StatsRepository
            return statsRepository.getStats(username);
        }
    }

    public List<Map<String, Object>> getScoreboard() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            StatsRepository statsRepository = getStatsRepository(unitOfWork);
            // Call the getScoreboard method from the StatsRepository
            return statsRepository.getScoreboard();
        }
    }
}
