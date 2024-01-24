package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.CardElement;
import at.fhtw.sampleapp.model.CardType;
import at.fhtw.sampleapp.persistence.UnitOfWork;
import at.fhtw.sampleapp.persistence.repository.BattleRepository;
import at.fhtw.sampleapp.persistence.repository.BattleRepositoryImpl;

import java.util.List;
import java.util.Random;

public class BattleService extends AbstractService {

    private StringBuilder battleLog;

    public BattleService() {
        battleLog = new StringBuilder();
    }

    private void calculateDamage(Card card1, Card card2) {
        if (card1.getType() == CardType.SPELL.ordinal()) {
            calculateSpellDamage(card1, card2);
        }
        if (card2.getType() == CardType.SPELL.ordinal()) {
            calculateSpellDamage(card2, card1);
        }
        if (card1.getType() == CardType.MONSTER.ordinal() && card2.getType() == CardType.MONSTER.ordinal()) {
            calculateMonsterDamage(card1, card2);
        }
    }

    private void calculateSpellDamage(Card spellCard, Card otherCard) {
        if (spellCard.getElement() == CardElement.WATER.ordinal() && otherCard.getName().equals("Knight")) {
            spellCard.setDamage(Integer.MAX_VALUE);
            battleLog.append(("Knight drowned\n"));
            return;
        }
        if (spellCard.getType() == CardType.SPELL.ordinal() && otherCard.getName().equals("Kraken")) {
            spellCard.setDamage(0);
            battleLog.append(("Kraken is immune to spells\n"));
            return;
        }
        if (spellCard.getElement() == CardElement.WATER.ordinal() && otherCard.getElement() == CardElement.FIRE.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() * 2);
        } else if (spellCard.getElement() == CardElement.FIRE.ordinal() && otherCard.getElement() == CardElement.WATER.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() / 2);
        } else if (spellCard.getElement() == CardElement.FIRE.ordinal() && otherCard.getElement() == CardElement.NORMAL.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() * 2);
        } else if (spellCard.getElement() == CardElement.NORMAL.ordinal() && otherCard.getElement() == CardElement.FIRE.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() / 2);
        } else if (spellCard.getElement() == CardElement.NORMAL.ordinal() && otherCard.getElement() == CardElement.WATER.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() * 2);
        } else if (spellCard.getElement() == CardElement.WATER.ordinal() && otherCard.getElement() == CardElement.NORMAL.ordinal()) {
            spellCard.setDamage(spellCard.getDamage() / 2);
        }
    }

    private void calculateMonsterDamage(Card monster1, Card monster2) {
        if ((monster1.getName().contains("Goblin") && monster2.getName().equals("Dragon")) ||
                (monster1.getName().equals("Dragon") && monster2.getName().contains("Goblin"))) {
            if (monster1.getName().contains("Goblin")) {
                monster1.setDamage(0);
            } else {
                monster2.setDamage(0);
            }
            battleLog.append("Goblins are too afraid of Dragons to attack.\n");
        }
        if ((monster1.getName().equals("FireElf") && monster2.getName().equals("Dragon")) ||
                (monster2.getName().equals("FireElf") && monster1.getName().equals("Dragon"))) {
            if (monster1.getName().equals("FireElf")) {
                monster2.setDamage(0);
            } else {
                monster1.setDamage(0);
            }
            battleLog.append("FireElves know Dragons since they were little and evade their attacks.\n");
        }
        if (monster1.getName().equals("Wizard") && monster2.getName().equals("Ork")) {
            monster2.setDamage(0);
        } else if (monster2.getName().equals("Wizard") && monster1.getName().equals("Ork")) {
            monster1.setDamage(0);
        }
    }

    public String startBattle(String username1, String username2) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            BattleRepository battleRepository = new BattleRepositoryImpl(unitOfWork);

            try {
                List<Card> deck1 = battleRepository.getDeckforBattle(username1);
                List<Card> deck2 = battleRepository.getDeckforBattle(username2);

                Random random = new Random();

                int roundCount = 0;
                while (!deck1.isEmpty() && !deck2.isEmpty() && roundCount < 100) {
                    int index1 = random.nextInt(deck1.size());
                    int index2 = random.nextInt(deck2.size());

                    Card card1 = deck1.get(index1);
                    Card card2 = deck2.get(index2);

                    System.out.println("Round " + (roundCount + 1) + ": " + card1.getName() + " vs " + card2.getName());
                    battleLog.append("Round ").append(roundCount + 1).append(": ").append(card1.getName()).append(" vs ").append(card2.getName()).append("\n");

                    // Store the original damage values
                    int originalDamage1 = card1.getDamage();
                    int originalDamage2 = card2.getDamage();

                    // Calculate the damage
                    calculateDamage(card1, card2);

                    System.out.println(card1.getName() + " (Damage: " + card1.getDamage() + ") vs " + card2.getName() + " (Damage: " + card2.getDamage() + ")");
                    battleLog.append(card1.getName()).append(" (Damage: ").append(card1.getDamage()).append(") vs ").append(card2.getName()).append(" (Damage: ").append(card2.getDamage()).append(")\n");

                    // General damage comparison
                    if (card1.getDamage() > card2.getDamage()) {
                        deck1.add(card2);
                        deck2.remove(index2);
                        System.out.println(card1.getName() + " wins");
                        battleLog.append(card1.getName()).append(" wins\n");
                    } else if (card1.getDamage() < card2.getDamage()) {
                        deck2.add(card1);
                        deck1.remove(index1);
                        System.out.println(card2.getName() + " wins");
                        battleLog.append(card2.getName()).append(" wins\n");
                    } else {
                        System.out.println("It's a draw");
                        battleLog.append("It's a draw\n");
                    }

                    // Reset the damage values at the end of the round
                    card1.setDamage(originalDamage1);
                    card2.setDamage(originalDamage2);

                    roundCount++;
                }

                if (deck1.isEmpty()) {
                    System.out.println(username2 + " wins the battle!");
                    battleRepository.increaseEloForUser(username2);
                    battleRepository.decreaseEloForUser(username1);
                    battleRepository.increaseWinsForUser(username2);
                    battleRepository.increaseLossesForUser(username1);
                    battleRepository.increaseCoinsforWinnner(username2);
                    battleLog.append(username2).append(" wins the battle!\n");
                } else if (deck2.isEmpty()) {
                    System.out.println(username1 + " wins the battle!");
                    battleRepository.increaseEloForUser(username1);
                    battleRepository.decreaseEloForUser(username2);
                    battleRepository.increaseWinsForUser(username1);
                    battleRepository.increaseLossesForUser(username2);
                    battleRepository.increaseCoinsforWinnner(username1);
                    battleLog.append(username1).append(" wins the battle!\n");
                } else {
                    System.out.println("The battle ended in a draw after 100 rounds.");
                    battleLog.append("The battle ended in a draw after 100 rounds.\n");
                }

                battleRepository.increaseTotalGamesForUser(username1);
                battleRepository.increaseTotalGamesForUser(username2);

                battleRepository.calculateWinRatioForUser(username1);
                battleRepository.calculateWinRatioForUser(username2);

                System.out.println("Final decks:");
                System.out.println(username1 + "'s deck: " + deck1);
                System.out.println(username2 + "'s deck: " + deck2);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Reset the battleLog
            String result = battleLog.toString();
            battleLog = new StringBuilder();
            return result;

        }

    }

}
