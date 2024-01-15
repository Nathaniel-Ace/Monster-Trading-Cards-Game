package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.DeckRepository;
import at.fhtw.sampleapp.persistence.repository.DeckRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeckService extends AbstractService {

    private DeckRepository deckRepository;

    public DeckService() { deckRepository = new DeckRepositoryImpl(new UnitOfWork()); }

    public Response getDeck(String username, String format) {
        try {
            // Call the getDeck method from the DeckRepository
            List<?> deck = deckRepository.getDeck(username, format);
            String json = this.getObjectMapper().writeValueAsString(deck);
            return new Response(HttpStatus.OK, ContentType.JSON, "The deck has cards, the response contains these: \n" + json);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("No cards found")) {
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the deck doesn't have any cards");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }
    }

    public Response updateDeck(String username, Request request) {
        try {
            String body = request.getBody();
            System.out.println("body: " + body);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            System.out.println("username: " + username);
            List<String> cardIds = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode cardIdNode : jsonNode) {
                    cardIds.add(cardIdNode.asText());
                }
            }
            System.out.println("cardIds: " + cardIds);

             //Check if the deck contains exactly 4 cards
             if (cardIds.size() != 4) {
                 return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "The provided deck did not include the required amount of cards");
             }

            // Check if the user owns the provided cards
             boolean userOwnsCards = deckRepository.checkUserOwnsCards(username, cardIds);
             if (!userOwnsCards) {
                 return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "At least one of the provided cards does not belong to the user or is not available.");
             }

            // Perform the update and capture the response
            deckRepository.updateDeck(username, cardIds);

            // Return the response
            return new Response(HttpStatus.OK, ContentType.JSON, "The deck has been successfully configured");

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Failed to update deck");
        }
    }


}
