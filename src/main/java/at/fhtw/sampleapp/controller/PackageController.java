package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.service.PackageService;

import java.util.List;

public class PackageController implements RestController {

    private final PackageService packageService;

    public PackageController() { this.packageService = new PackageService(); }

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
            if (request.getMethod() == Method.POST) {

                if (!"admin".equals(usernameFromToken)) {
                    return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "Provided user is not 'admin'");
                } else {
                    this.packageService.addCards(request);
                    return new Response(HttpStatus.CREATED, ContentType.JSON, "Package and cards successfully created");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Card already exists")) {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "At least one card in the packages already exists");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Card not added");
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
