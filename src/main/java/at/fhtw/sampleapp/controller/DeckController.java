package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.DeckService;

import java.util.List;

public class DeckController implements RestController {

    private final DeckService deckService;

    public DeckController() { deckService = new DeckService(); }

    @Override
    public Response handleRequest(Request request){

        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET requests
        if (request.getMethod() == Method.GET || request.getMethod() == Method.PUT) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        try {
            if (request.getMethod() == Method.GET) {

                // Get the format parameter from the query parameters
                String format = null;
                String params = request.getParams();
                if (params != null) {
                    String[] queryParams = params.split("&");
                    for (String param : queryParams) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2 && "format".equals(keyValue[0])) {
                            format = keyValue[1];
                            break;
                        }
                    }
                }
                List<?> deck = deckService.getDeck(usernameFromToken, format);
                if ("plain".equals(format)) {
                    // return the deck in plain format
                    return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "The deck has cards, the response contains these: \n" + String.join("\n", (List<String>) deck));
                } else {
                    // return the deck in the original format
                    String json = this.getObjectMapper().writeValueAsString(deck);
                    return new Response(HttpStatus.OK, ContentType.JSON, "The deck has cards, the response contains these: \n" + json);
                }
            } else if (request.getMethod() == Method.PUT) {
                deckService.updateDeck(usernameFromToken, request);
                return new Response(HttpStatus.OK, ContentType.JSON, "The deck has been successfully configured");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("No cards found")) {
                return new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the deck doesn't have any cards");
            } else if (e.getMessage().equals("not enough cards provided")) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "The provided deck did not include the required amount of cards");
            }else if (e.getMessage().equals("user does not own all cards")) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "At least one of the provided cards does not belong to the user or is not available.");
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
