package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.model.Card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.model.CardType;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.PackagesRepository;
import at.fhtw.sampleapp.persistence.repository.PackagesRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageService extends AbstractService {

    private PackagesRepository packagesRepository;

    public PackageService() { packagesRepository = new PackagesRepositoryImpl(new UnitOfWork()); }

    public Response addCards(Request request) {
        Card card = null;
        try {
            String body = request.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            List<Card> cards = new ArrayList<>();
            for (JsonNode cardNode : jsonNode) {
                String id = cardNode.get("Id").asText();
                String name = cardNode.get("Name").asText();
                Integer damage = cardNode.get("Damage").asInt();
                CardType type;
                if (name.contains("Fire")) {
                    type = CardType.FIRE;
                } else if (name.contains("Water")) {
                    type = CardType.WATER;
                } else if (name.contains("Regular")) {
                    type = CardType.REGULAR;
                } else {
                    type = CardType.NEUTRAL;
                }

                card = new Card(id, name, damage, type);
                if (packagesRepository.cardExists(card.getId())) {
                    return new Response(HttpStatus.CONFLICT, ContentType.JSON, "Card with id " + card.getId() + " already exists");
                }
                cards.add(card);
            }

            packagesRepository.addCards(cards);

            return new Response(HttpStatus.CREATED, ContentType.JSON, "Package and cards successfully created");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Card with id " + card.getId() + " already exists")) {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "Card with id " + card.getId() + " already exists");
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "Card not added");
            }
        }

    }

}
