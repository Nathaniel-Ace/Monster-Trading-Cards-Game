package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.DeckRepository;
import at.fhtw.sampleapp.persistence.repository.DeckRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class DeckService extends AbstractService {

    private DeckRepository getDeckRepository(UnitOfWork unitOfWork) {
        return new DeckRepositoryImpl(unitOfWork);
    }

    public List<?> getDeck(String username, String format) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            DeckRepository deckRepository = getDeckRepository(unitOfWork);
            // Call the getDeck method from the DeckRepository
            return deckRepository.getDeck(username, format);
        }

    }

    public void updateDeck(String username, Request request) throws Exception {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            DeckRepository deckRepository = getDeckRepository(unitOfWork);
            String body = request.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            List<String> cardIds = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode cardIdNode : jsonNode) {
                    cardIds.add(cardIdNode.asText());
                }
            }

            //Check if the deck contains exactly 4 cards
            if (cardIds.size() != 4) {
                throw new IllegalArgumentException("not enough cards provided");
            }

            // Check if the user owns the provided cards
            boolean userOwnsCards = deckRepository.checkUserOwnsCards(username, cardIds);
            if (!userOwnsCards) {
                throw new IllegalArgumentException("user does not own all cards");
            }

            // Perform the update
            deckRepository.updateDeck(username, cardIds);
        }

    }
}
