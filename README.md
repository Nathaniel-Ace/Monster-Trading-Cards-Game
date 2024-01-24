# Monster Trading Cards Game (MTCG)

Design:

- OOP-Regeln werden befolgt.
- Server und Anwendung sind getrennt.
- Wiederverwendbare Klassen für den Einsatz in verschiedenen Methoden.
- Der Server verändert HTTP-Anfragen und sendet sie an die Anwendung, wo die weitere Logik basiert.
- Unter der Bedingung, dass die Anfrage korrekt ist:
  - Der entsprechende Controller übernimmt die Anfrage und entscheidet, welcher Teil seines Dienstes aufgerufen wird.
  - Der Service führt alle notwendigen "Logik"-Schritte innerhalb der Anwendung durch und sendet Anfragen an die Datenbank oder erhält Antworten.
  - Die empfangene Antwort oder der entsprechende Antwortcode wird an den Controller zurückgesendet.
  - Der Controller ruft den Response Creator auf, der eine gültige HTTP-Antwort erstellt.
  - Die Antwort wird an den Server zurückgesendet.
  - Der Benutzer erhält seine Antwort.

Unit-Tests Design:

Die für die Unit-Tests gewählte Entwurfsweise kombiniert das Arrange-Act-Assert (AAA)-Muster mit der Verwendung von Mock-Objekten.

Arrange-Act-Assert (AAA)-Muster: Dieses Muster ist eine gängige Methode zum Schreiben von Unit-Tests für eine zu prüfende Methode. Die Struktur dieser Tests ist in drei Abschnitte unterteilt, die in der Reihenfolge der Ausführung angeordnet sind:

- Arrange: Alle notwendigen Vorbedingungen und Eingaben werden definiert.

- Act: Auf das zu prüfende Objekt wird eine Aktion ausgeführt.

- Assert: Die erwarteten Ergebnisse werden überprüft.

Mock-Objekte: Mock-Objekte sind simulierte Objekte, die das Verhalten realer Objekte auf kontrollierte Weise imitieren. In diesem Test werden die BattleService- und Request-Objekte mithilfe von Mockito gemockt. Dies ermöglicht die Kontrolle ihres Verhaltens während des Tests, wie beispielsweise das Festlegen der Rückgabewerte ihrer Methoden.

Lessons learned:

- Zeitmanagement ist entscheidend:
Effizientes Zeitmanagement ist ein grundlegendes Element für den erfolgreichen Projektverlauf. Die Priorisierung von Aufgaben, die Festlegung realistischer Fristen und die angemessene Zeitallokation tragen zu einer verbesserten Produktivität und erfolgreichen Projektergebnissen bei.

- Verbesserte Integration von Git Versionskontrolle:
Eine nahtlosere Integration von Git Versionskontrolle kann die Zusammenarbeit bei der Entwicklung erheblich verbessern. Das Verständnis von Branching-Strategien, die Nutzung von Feature-Branches und regelmäßige Commits tragen zur verbesserten Code-Zusammenarbeit, Versionsverfolgung und allgemeinen Projekstabilität bei.

Unique Features:

- Wenn ein Benutzer gewinnt, erhält er/sie 2 Coins, die man wieder für Kauf von Karten benutzen kann.
- In Stats und Scoreboard werden die gesamten Spiele und die Siegesquote (win ratio) dargestellt.
- Wenn ein Benutzer einen POST-Request für einen Battle sendet und mehr als eine Minute wartet, wird der Request automatisch abgebrochen. Leider hab ich es nicht geschafft einen neuen Response zu senden, der zeigt, dass kein Gegner gefunden wurde.