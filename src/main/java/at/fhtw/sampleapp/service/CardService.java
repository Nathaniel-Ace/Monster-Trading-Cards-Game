package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.CardRepository;
import at.fhtw.sampleapp.persistence.repository.CardRepositoryImpl;

import java.util.List;
import java.util.Map;

public class CardService extends AbstractService{

    private CardRepository cardRepository;

    public CardService() { cardRepository = new CardRepositoryImpl(new UnitOfWork()); }

    public Response getCards(String username) {
        try {
            // Call the getCards method from the CardRepository
            List<Map<String, Object>> cards = cardRepository.getCards(username);
            // Convert the cards to JSON
            String json = this.getObjectMapper().writeValueAsString(cards);
            return new Response(HttpStatus.OK, ContentType.JSON, "The user has cards, the response contains these: \n" + json);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("No cards found")) {
                //System.out.println("The request was fine, but the user doesn't have any cards");
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the user doesn't have any cards");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }

        }
    }
}
