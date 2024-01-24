package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.TransactionRepository;
import at.fhtw.sampleapp.persistence.repository.TransactionRepositoryImpl;

import java.util.List;
import java.util.Map;

public class TransactionService extends AbstractService{

    private TransactionRepository getTransactionRepository(UnitOfWork unitOfWork) {
        return new TransactionRepositoryImpl(unitOfWork);
    }

    public List<Map<String, Object>> selectCards() {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            TransactionRepository transactionRepository = getTransactionRepository(unitOfWork);
            // Call the getCards method from the CardRepository
            return transactionRepository.selectCards();
        }
    }

    public void acquirePackages(String username) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            TransactionRepository transactionRepository = getTransactionRepository(unitOfWork);
            // Call the getCards method from the CardRepository
            transactionRepository.acquirePackages(username);
        }
    }

}
