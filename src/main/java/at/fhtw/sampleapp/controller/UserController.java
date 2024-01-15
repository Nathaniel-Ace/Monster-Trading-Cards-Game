package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.UserService;

public class UserController implements RestController {

    private final UserService userService;

    public UserController() { this.userService = new UserService(); }

    @Override
    public Response handleRequest(Request request){
        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET and PUT requests
        if (request.getMethod() == Method.GET || request.getMethod() == Method.PUT) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        if (request.getMethod() == Method.GET && request.getPathParts().size() > 1 && request.getPathParts().get(0).equals("users")) {
            String usernameFromPath = request.getPathParts().get(1);
            if (!usernameFromPath.equals(usernameFromToken)) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "Access token does not match the username"
                );
            }
            return this.userService.getUserByUsername(usernameFromToken);
        } else if (request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            String userId = request.getPathParts().get(1);
            return this.userService.getUser(Integer.parseInt(userId));
        } else if (request.getMethod() == Method.POST) {
            return this.userService.addUser(request);
        } else if (request.getMethod() == Method.PUT && request.getPathParts().size() > 1 && request.getPathParts().get(0).equals("users")) {
            String usernameFromPath = request.getPathParts().get(1);
            if (!usernameFromPath.equals(usernameFromToken)) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "Access token does not match the username"
                );
            }
            return this.userService.editUser(usernameFromToken, request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
