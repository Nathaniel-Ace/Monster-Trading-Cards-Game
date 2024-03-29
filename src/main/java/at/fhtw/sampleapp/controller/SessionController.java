package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.SessionService;

public class SessionController implements RestController {
    private final SessionService sessionService;

    public SessionController() { this.sessionService = new SessionService(); }

    @Override
    public Response handleRequest(Request request) {
        boolean loginSuccessful = this.sessionService.loginUser(request);
        if (!loginSuccessful) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Invalid username/ password provided");
        } else {
            return new Response(HttpStatus.OK, ContentType.JSON, "User login successful");
        }
    }

}
