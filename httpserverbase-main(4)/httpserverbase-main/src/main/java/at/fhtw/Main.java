package at.fhtw;


import at.fhtw.database.DBConnect;
import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.sampleapp.controller.EchoController;
import at.fhtw.sampleapp.controller.WeatherController;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        /*
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        DBConnect db = new DBConnect();
        db.connect_to_db("swen1db", "postgres", "nathaniel17");
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/weather", new WeatherController());
        router.addService("/echo", new EchoController());

        return router;
    }
}
