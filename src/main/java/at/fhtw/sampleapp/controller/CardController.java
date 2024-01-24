package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.CardService;

import java.util.List;
import java.util.Map;

public class CardController implements RestController {

    private final CardService cardService;

    public CardController() { this.cardService = new CardService(); }

    @Override
    public Response handleRequest(Request request){

        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET requests
        if (request.getMethod() == Method.GET) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        try {
            if (request.getMethod() == Method.GET) {
                List<Map<String, Object>> cards = cardService.getCards(usernameFromToken);
                String json = this.getObjectMapper().writeValueAsString(cards);
                return new Response(HttpStatus.OK, ContentType.JSON, "The user has cards, the response contains these: \n" + json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("No cards found")) {
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the user doesn't have any cards");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
