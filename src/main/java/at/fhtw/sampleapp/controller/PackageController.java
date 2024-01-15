package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.PackageService;

public class PackageController implements RestController {

    private final PackageService packageService;

    public PackageController() { this.packageService = new PackageService(); }

    @Override
    public Response handleRequest(Request request){

        if (request.getMethod() == Method.POST) {
            String token = request.getHeaderMap().getHeader("Authorization");

            if (token.contains("admin")) {
                return this.packageService.addCards(request);
            } else {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
