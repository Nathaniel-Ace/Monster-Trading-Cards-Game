package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.BattleService;

import java.util.*;

public class BattleController implements RestController {

    private final BattleService battleService;
    private List<Request> list;
    private Map<Request, String> playerUsernames;

    public BattleController() {
        this.battleService = new BattleService();
        this.list = Collections.synchronizedList(new ArrayList<>());
        this.playerUsernames = new HashMap<>();
    }

    public void postRequest(Request request, String usernameFromToken) {
        synchronized (list) {
            list.add(request);
            playerUsernames.put(request, usernameFromToken);

            if (list.size() == 2) {
                Request player1 = list.get(0);
                Request player2 = list.get(1);
                String username1 = playerUsernames.get(player1);
                System.out.println(username1);
                String username2 = playerUsernames.get(player2);
                System.out.println(username2);

                if (username1 != null && username2 != null) {
                    list.remove(0);
                    list.remove(0);
                    playerUsernames.remove(player1);
                    playerUsernames.remove(player2);
                    battleService.startBattle(username1, username2);
                }
            }
        }
    }

    public Response handleRequest(Request request){

        String token = request.getHeaderMap().getHeader("Authorization");
        Object result = null;

        // Check token for GET and PUT requests
        if (request.getMethod() == Method.POST) {
            result = checkToken(token);
            if (result instanceof Response) {
                return (Response) result;
            }
        }

        String usernameFromToken = result != null ? (String) result : null;

        if (request.getMethod() == Method.POST) {

            postRequest(request, usernameFromToken);

            if (list.isEmpty()) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "2 players ready for battle"
                );
            } else {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "Player added, waiting for another player"
                );
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]");
    }

}