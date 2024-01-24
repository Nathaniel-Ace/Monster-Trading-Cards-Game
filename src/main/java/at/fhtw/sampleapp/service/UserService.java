package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.UserRepository;
import at.fhtw.sampleapp.persistence.repository.UserRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserService extends AbstractService{

    private UserRepository getUserRepository(UnitOfWork unitOfWork) {
        return new UserRepositoryImpl(unitOfWork);
    }

    public User getUserByUsername(String username) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = getUserRepository(unitOfWork);
            return userRepository.findByUsername(username);
        }
    }

    public void addUser(Request request) throws JsonProcessingException {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = getUserRepository(unitOfWork);
            String body = request.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            String username = jsonNode.get("Username").asText();
            String password = jsonNode.get("Password").asText();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            userRepository.addUser(user);
            userRepository.insertUsernameDeck(username);
            userRepository.insertUsernameStats(username);
        }
    }

    public void editUser(String username, Request request) throws JsonProcessingException {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            UserRepository userRepository = getUserRepository(unitOfWork);
            String body = request.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            String name = jsonNode.get("Name").asText();
            String bio = jsonNode.get("Bio").asText();
            String image = jsonNode.get("Image").asText();

            User user = new User();
            user.setUsername(username);
            user.setName(name);
            user.setBio(bio);
            user.setImage(image);

            userRepository.editUser(username, user);
        }
    }
}