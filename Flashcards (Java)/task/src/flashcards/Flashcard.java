package flashcards;

class Flashcard {
    private final String term;
    private final String definition;
    private int mistakes;

    public Flashcard(String term, String definition, int mistakes) {
        this.term = term;
        this.definition = definition;
        this.mistakes = mistakes;
    }

    // getters and setters
    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void incrementMistakes() {
        this.mistakes++;
    }

    public void resetMistakes() {
        this.mistakes = 0;
    }
}
