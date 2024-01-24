package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.StatsService;

import java.util.List;
import java.util.Map;

public class StatsController implements RestController {

    private StatsService statsService;

    public StatsController() { statsService = new StatsService(); }

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

        try {
            if (request.getMethod() == Method.GET) {
                if (request.getPathname().equals("/stats")) {
                    Map<String, Object> stats = statsService.getStats(usernameFromToken);
                    String json = this.getObjectMapper().writeValueAsString(stats);
                    return new Response(HttpStatus.OK, ContentType.JSON, "The stats could be retrieved successfully.\n" + json);
                } else if (request.getPathname().equals("/scoreboard")) {
                    List<Map<String, Object>> scoreboard = statsService.getScoreboard();
                    String json = this.getObjectMapper().writeValueAsString(scoreboard);
                    return new Response(HttpStatus.OK, ContentType.JSON, "The scoreboard could be retrieved successfully.\n" + json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
