package flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Flashcard> flashcards = new LinkedHashMap<>();
    private static final List<String> log = new ArrayList<>();
    private static String importFile = null;
    private static String exportFile = null;

    public static void main(String[] args) {
        processArgs(args);

        if (importFile != null) {
            importFlashcards(importFile);
        }

        while (true) {
            String actionPrompt = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):";
            System.out.println(actionPrompt);
            log.add(actionPrompt);
            String action = scanner.nextLine();
            log.add(action);

            switch (action) {
                case "add" -> addFlashcard();
                case "remove" -> removeFlashcard();
                case "import" -> importFlashcards();
                case "export" -> {
                    String fileNamePrompt = "File name:";
                    System.out.println(fileNamePrompt);
                    log.add(fileNamePrompt);
                    String fileName = scanner.nextLine();
                    log.add(fileName);
                    exportFlashcards(fileName);
                }

                case "ask" -> askFlashcards();
                case "log" -> saveLog();
                case "hardest card" -> printHardestCard();
                case "reset stats" -> resetStats();
                case "exit" -> {
                    String exitMessage = "Bye bye!";
                    System.out.println(exitMessage);
                    log.add(exitMessage);

                    if (exportFile != null) {
                        exportFlashcards(exportFile);
                    }

                    return;
                }
            }

        }
    }

    private static void processArgs(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-import")) {
                importFile = args[i + 1];
            } else if (args[i].equals("-export")) {
                exportFile = args[i + 1];
            }
        }
    }

    private static void importFlashcards() {
        String fileNamePrompt = "File name:";
        System.out.println(fileNamePrompt);
        log.add(fileNamePrompt);
        String fileName = scanner.nextLine();
        log.add(fileName);

        importFlashcards(fileName);
    }

    private static void importFlashcards(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) {
            String notFoundMessage = "File not found.";
            System.out.println(notFoundMessage);
            log.add(notFoundMessage);
            return;
        }

        int count = 0;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    flashcards.put(parts[0], new Flashcard(parts[0], parts[1], Integer.parseInt(parts[2])));
                    count++;
                }
            }
        } catch (IOException e) {
            String notFoundMessage = "File not found.";
            System.out.println(notFoundMessage);
            log.add(notFoundMessage);
            return;
        }

        String loadedMessage = count + " cards have been loaded.";
        System.out.println(loadedMessage);
        log.add(loadedMessage);
    }

    private static void addFlashcard() {
        String cardPrompt = "The card:";
        System.out.println(cardPrompt);
        log.add(cardPrompt);
        String term = scanner.nextLine();
        log.add(term);
        if (flashcards.containsKey(term)) {
            String alreadyExistsMessage = "The card \"" + term + "\" already exists.";
            System.out.println(alreadyExistsMessage);
            log.add(alreadyExistsMessage);
            return;
        }

        String definitionPrompt = "The definition of the card:";
        System.out.println(definitionPrompt);
        log.add(definitionPrompt);
        String definition = scanner.nextLine();
        log.add(definition);
        if (flashcards.values().stream().anyMatch(c -> c.getDefinition().equals(definition))) {
            String definitionExistsMessage = "The definition \"" + definition + "\" already exists.";
            System.out.println(definitionExistsMessage);
            log.add(definitionExistsMessage);
            return;
        }

        flashcards.put(term, new Flashcard(term, definition, 0));
        String addedMessage = "The pair (\"" + term + "\":\"" + definition + "\") has been added.";
        System.out.println(addedMessage);
        log.add(addedMessage);
    }

    private static void removeFlashcard() {
        String whichCardPrompt = "Which card?";
        System.out.println(whichCardPrompt);
        log.add(whichCardPrompt);
        String term = scanner.nextLine();
        log.add(term);

        if (flashcards.remove(term) != null) {
            String removedMessage = "The card has been removed.";
            System.out.println(removedMessage);
            log.add(removedMessage);
        } else {
            String noCardMessage = "Can't remove \"" + term + "\": there is no such card.";
            System.out.println(noCardMessage);
            log.add(noCardMessage);
        }
    }

    private static void exportFlashcards(String fileName) {
        File file = new File(fileName);

        int count = 0;
        try (FileWriter writer = new FileWriter(file)) {
            for (Flashcard card : flashcards.values()) {
                writer.write(card.getTerm() + ":" + card.getDefinition() + ":" + card.getMistakes() + "\n");
                count++;
            }
        } catch (IOException e) {
            String errorWritingMessage = "Error writing to file.";
            System.out.println(errorWritingMessage);
            log.add(errorWritingMessage);
            return;
        }

        String savedMessage = count + " cards have been saved.";
        System.out.println(savedMessage);
        log.add(savedMessage);
    }


    private static void askFlashcards() {
        String askTimesPrompt = "How many times to ask?";
        System.out.println(askTimesPrompt);
        log.add(askTimesPrompt);
        int times = Integer.parseInt(scanner.nextLine());
        log.add(String.valueOf(times));

        List<Flashcard> cards = new ArrayList<>(flashcards.values());
        Random random = new Random();

        for (int i = 0; i < times; i++) {
            Flashcard card = cards.get(random.nextInt(cards.size()));
            String printDefinitionPrompt = "Print the definition of \"" + card.getTerm() + "\":";
            System.out.println(printDefinitionPrompt);
            log.add(printDefinitionPrompt);
            String userDefinition = scanner.nextLine();
            log.add(userDefinition);

            if (card.getDefinition().equals(userDefinition)) {
                String correctMessage = "Correct!";
                System.out.println(correctMessage);
                log.add(correctMessage);
            } else {
                card.incrementMistakes();
                Flashcard correctCard = flashcards.values().stream().filter(c -> c.getDefinition().equals(userDefinition)).findFirst().orElse(null);
                if (correctCard != null) {
                    correctCard.incrementMistakes();
                    String wrongMessage = "Wrong. The right answer is \"" + card.getDefinition() + "\", but your definition is correct for \"" + correctCard.getTerm() + "\".";
                    System.out.println(wrongMessage);
                    log.add(wrongMessage);
                } else {
                    String wrongMessage = "Wrong. The right answer is \"" + card.getDefinition() + "\".";
                    System.out.println(wrongMessage);
                    log.add(wrongMessage);
                }
            }
        }
    }

    private static void saveLog() {
        String fileNamePrompt = "File name:";
        System.out.println(fileNamePrompt);
        log.add(fileNamePrompt);
        String fileName = scanner.nextLine();
        log.add(fileName);
        String savedMessage = "The log has been saved.";
        log.add(savedMessage);
        // Add current date and time to log
        log.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));

        try (FileWriter writer = new FileWriter(fileName)) {
            for (String line : log) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            String errorWritingMessage = "Error writing to file.";
            System.out.println(errorWritingMessage);
            log.add(errorWritingMessage);
        }

        System.out.println(savedMessage);
    }

    private static void printHardestCard() {
        int maxMistakes = flashcards.values().stream().mapToInt(Flashcard::getMistakes).max().orElse(0);
        if (maxMistakes == 0) {
            String noMistakesMessage = "There are no cards with errors.";
            System.out.println(noMistakesMessage);
            log.add(noMistakesMessage);
        } else {
            List<String> hardestCards = new ArrayList<>();
            for (Flashcard card : flashcards.values()) {
                if (card.getMistakes() == maxMistakes) {
                    hardestCards.add(card.getTerm());
                }
            }

            if (hardestCards.size() == 1) {
                String hardestMessage = "The hardest card is \"" + hardestCards.get(0) + "\". You have " + maxMistakes + " errors answering it.";
                System.out.println(hardestMessage);
                log.add(hardestMessage);
            } else {
                String hardestMessage = "The hardest cards are \"" + String.join("\", \"", hardestCards) + "\". You have " + maxMistakes + " errors answering them.";
                System.out.println(hardestMessage);
                log.add(hardestMessage);
            }
        }
    }

    private static void resetStats() {
        for (Flashcard card : flashcards.values()) {
            card.resetMistakes();
        }

        String resetMessage = "Card statistics have been reset.";
        System.out.println(resetMessage);
        log.add(resetMessage);
    }
}

