package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;

public class TradingController implements RestController {

    public Response handleRequest(Request request) {
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET and PUT requests
        if (request.getMethod() == Method.GET || request.getMethod() == Method.POST || request.getMethod() == Method.DELETE) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        if (request.getMethod() == Method.GET) {
            return new Response(HttpStatus.OK, ContentType.JSON, "Not implemented yet");
        } else if (request.getMethod() == Method.POST) {
            return new Response(HttpStatus.OK, ContentType.JSON, "Not implemented yet");
        } else if (request.getMethod() == Method.DELETE) {
            return new Response(HttpStatus.OK, ContentType.JSON, "Not implemented yet");
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
