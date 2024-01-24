package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.SessionRepository;
import at.fhtw.sampleapp.persistence.repository.SessionRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SessionService extends AbstractService{

    public boolean loginUser(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            SessionRepository sessionRepository = new SessionRepositoryImpl(unitOfWork);
            String body = request.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            String username = jsonNode.get("Username").asText();
            String password = jsonNode.get("Password").asText();
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            return sessionRepository.searchUsername(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
