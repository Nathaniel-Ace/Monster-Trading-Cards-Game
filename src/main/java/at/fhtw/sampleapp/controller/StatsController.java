package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.StatsService;

public class StatsController implements RestController {

    private final StatsService statsService;

    public StatsController() { this.statsService = new StatsService(); }

    @Override
    public Response handleRequest(Request request){
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET and PUT requests
        if (request.getMethod() == Method.GET) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        if (request.getMethod() == Method.GET) {
            if (request.getPathname().equals("/stats")) {
                return this.statsService.getStats(usernameFromToken);
            } else if (request.getPathname().equals("/scoreboard")) {
                return this.statsService.getScoreboard();
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
