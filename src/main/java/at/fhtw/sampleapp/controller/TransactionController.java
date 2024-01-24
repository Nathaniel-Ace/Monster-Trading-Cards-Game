package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.TransactionService;

import java.util.List;
import java.util.Map;

public class TransactionController implements RestController {

    private final TransactionService transactionService;

    public TransactionController() { this.transactionService = new TransactionService(); }

    @Override
    public Response handleRequest(Request request){
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for POST requests
        if (request.getMethod() == Method.POST) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        try {
            if(request.getPathname().equals("/transactions/packages")) {
                if (request.getMethod() == Method.POST) {
                    this.transactionService.acquirePackages(usernameFromToken);
                    return new Response(HttpStatus.OK, ContentType.JSON, "A package has been successfully bought");
                } else if (request.getMethod() == Method.GET) {
                    List<Map<String, Object>> cards = this.transactionService.selectCards();
                    String json = getObjectMapper().writeValueAsString(cards);
                    return new Response(HttpStatus.OK, ContentType.JSON, json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("No packages found")) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "No card package available for buying");
            } else if (e.getMessage().equals("Not enough coins")) {
                return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "Not enough money for buying a card package");
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
