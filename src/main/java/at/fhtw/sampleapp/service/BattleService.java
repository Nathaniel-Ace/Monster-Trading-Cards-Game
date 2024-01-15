package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.BattleRepository;
import at.fhtw.sampleapp.persistence.repository.BattleRepositoryImpl;

import java.util.List;
import java.util.Random;

public class BattleService extends AbstractService {

    private BattleRepository battleRepository;

    public BattleService() {
        battleRepository = new BattleRepositoryImpl(new UnitOfWork());
    }

    public void startBattle(String username1, String username2) {
        try {
            List<?> deck1 = battleRepository.getDeck(username1);
            System.out.println(deck1);
            List<?> deck2 = battleRepository.getDeck(username2);
            System.out.println(deck2);

            // Continue with the battle logic

//            Random random = new Random();
//
//            int roundCount = 0;
//            while (!deck1.isEmpty() && !deck2.isEmpty() && roundCount < 100) {
//                int index1 = random.nextInt(deck1.size());
//                int index2 = random.nextInt(deck2.size());
//
//                Card card1 = (Card) deck1.get(index1);
//                Card card2 = (Card) deck2.get(index2);
//
//                if (card1.getDamage() > card2.getDamage()) {
//                    deck1.add(card2);
//                    deck2.remove(index2);
//                } else if (card1.getDamage() < card2.getDamage()) {
//                    deck2.add(card1);
//                    deck1.remove(index1);
//                }
//
//                roundCount++;
//            }
//
//            if (deck1.isEmpty()) {
//                System.out.println(username2 + " wins!");
//            } else if (deck2.isEmpty()) {
//                System.out.println(username1 + " wins!");
//            } else {
//                System.out.println("The game ended in a draw after 100 rounds.");
//            }
//
//            if (deck1.isEmpty()) {
//                System.out.println(username2 + " wins!");
//            } else {
//                System.out.println(username1 + " wins!");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
