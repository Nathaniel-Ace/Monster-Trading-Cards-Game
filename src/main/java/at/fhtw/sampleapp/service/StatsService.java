package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.StatsRepository;
import at.fhtw.sampleapp.persistence.repository.StatsRepositoryImpl;

import java.util.List;
import java.util.Map;

public class StatsService extends AbstractService{

    private StatsRepository statsRepository;

    public StatsService() { statsRepository = new StatsRepositoryImpl(new UnitOfWork()); }

    public Response getStats(String username) {
        try {
            Map<String, Object> stats = statsRepository.getStats(username);
            String json = this.getObjectMapper().writeValueAsString(stats);
            // Call the getStats method from the StatsRepository
            return new Response(HttpStatus.OK, ContentType.JSON, "The stats could be retrieved successfully.\n" + json);
        } catch (Exception e) {
            e.printStackTrace();
            // If an error occurs, print the stack trace and return an error message
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
        }
    }

    public Response getScoreboard() {
        try {
            // Call the getScoreboard method from the StatsRepository
            List<Map<String, Object>> scoreboard = statsRepository.getScoreboard();
            String json = this.getObjectMapper().writeValueAsString(scoreboard);
            return new Response(HttpStatus.OK, ContentType.JSON, "The scoreboard could be retrieved successfully.\n" + json);
        } catch (Exception e) {
            e.printStackTrace();
            // If an error occurs, print the stack trace and return an error message
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
        }
    }

}
