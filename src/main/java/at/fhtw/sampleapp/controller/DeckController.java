package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.DeckService;

public class DeckController implements RestController {

    private final DeckService deckService;

    public DeckController() { this.deckService = new DeckService(); }

    @Override
    public Response handleRequest(Request request){
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET and PUT requests
        if (request.getMethod() == Method.GET || request.getMethod() == Method.PUT) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

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

            return this.deckService.getDeck(usernameFromToken, format);
        } else if (request.getMethod() == Method.PUT) {
            return this.deckService.updateDeck(usernameFromToken, request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
