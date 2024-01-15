package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.persistence.DataAccessException;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.UserRepository;
import at.fhtw.sampleapp.persistence.repository.UserRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserService extends AbstractService{

    private UserRepository userRepository;

    public UserService() { userRepository = new UserRepositoryImpl(new UnitOfWork()); }
    public Response getUser(int id)
    {
        System.out.println("get user for id: " + id);
        User user = userRepository.findById(id);
        String json = null;
        try {
            json = this.getObjectMapper().writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Response(HttpStatus.OK, ContentType.JSON, json);
    }

    public Response getUserByUsername(String username)
    {
        System.out.println("get user for username: " + username);
        try {
            User user = userRepository.findByUsername(username);
            String json = this.getObjectMapper().writeValueAsString(user);
            return new Response(HttpStatus.OK, ContentType.JSON, json);
        } catch (DataAccessException e) {
            if (e.getMessage().equals("No user found")) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "User not found");
            } else {
                throw e;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Response addUser(Request request) {
        try {
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
            return new Response(HttpStatus.CREATED, ContentType.JSON, "User successfully created");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Username already taken")) {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "User with same username already registered");
            } else{
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }
    }

    public Response editUser(String username, Request request) {
        try {
            String body = request.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            //String username = jsonNode.get("username").asText();
            String name = jsonNode.get("Name").asText();
            String bio = jsonNode.get("Bio").asText();
            String image = jsonNode.get("Image").asText();

            User user = new User();
            user.setUsername(username);
            user.setName(name);
            user.setBio(bio);
            user.setImage(image);

            userRepository.editUser(username, user);

            return new Response(HttpStatus.OK, ContentType.JSON, "User successfully updated");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("No user found")) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "User not found");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Internal Server Error");
            }
        }
    }
}
