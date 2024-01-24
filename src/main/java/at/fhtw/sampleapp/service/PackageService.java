package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.model.Card;

import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.model.CardElement;
import at.fhtw.sampleapp.model.CardType;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.PackagesRepository;
import at.fhtw.sampleapp.persistence.repository.PackagesRepositoryImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class PackageService extends AbstractService {

    public void addCards(Request request) throws Exception {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            PackagesRepository packagesRepository = new PackagesRepositoryImpl(unitOfWork);
            Card card;
            String body = request.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            List<Card> cards = new ArrayList<>();
            for (JsonNode cardNode : jsonNode) {
                String id = cardNode.get("Id").asText();
                String name = cardNode.get("Name").asText();
                Integer damage = cardNode.get("Damage").asInt();
                // Assign the correct element and type to the card
                CardElement element;
                if (name.contains("Fire")) {
                    element = CardElement.FIRE;
                } else if (name.contains("Water")) {
                    element = CardElement.WATER;
                } else {
                    element = CardElement.NORMAL;
                }
                CardType type;
                if (name.contains("Spell")) {
                    type = CardType.SPELL;
                } else {
                    type = CardType.MONSTER;
                }

                card = new Card(id, name, damage, element, type);
                cards.add(card);
            }

            packagesRepository.addCards(cards);
        }

    }

}
