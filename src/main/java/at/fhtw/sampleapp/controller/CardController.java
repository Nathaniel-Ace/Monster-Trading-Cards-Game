package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.CardService;

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

        if (request.getMethod() == Method.GET) {
            return this.cardService.getCards(usernameFromToken);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
