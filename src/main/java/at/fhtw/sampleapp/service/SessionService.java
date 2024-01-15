package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.SessionRepository;
import at.fhtw.sampleapp.persistence.repository.SessionRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionService extends AbstractService{

    private SessionRepository sessionRepository;

    public SessionService() { sessionRepository = new SessionRepositoryImpl(new UnitOfWork()); }

    public Response loginUser(Request request) {
        try {
            String body = request.getBody();


            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);


            String username = jsonNode.get("Username").asText();
            String password = jsonNode.get("Password").asText();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            boolean login = sessionRepository.SearchUsername(user);
            if(!login)
            {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Invalid username/password provided");
            } else {
                return new Response(HttpStatus.OK, ContentType.JSON, "User login successful");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");

        }
    }
}
