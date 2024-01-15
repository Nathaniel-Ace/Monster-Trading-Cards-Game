package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.TransactionRepository;
import at.fhtw.sampleapp.persistence.repository.TransactionRepositoryImpl;

import java.util.List;
import java.util.Map;

public class TransactionService extends AbstractService{

    private TransactionRepository transactionRepository;

    public TransactionService() { transactionRepository = new TransactionRepositoryImpl(new UnitOfWork()); }

    public List<Map<String, Object>> selectCards() {
        return transactionRepository.selectCards();
    }

    public void acquirePackages(String username) {
        transactionRepository.acquirePackages(username);
    }

}
