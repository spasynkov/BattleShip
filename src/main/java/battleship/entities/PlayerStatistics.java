package battleship.entities;

/**
 * Player's statistics class
 */
public class PlayerStatistics {
    private String name;
    private int theNumberOfMoves;
    private int theLongestStreak;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTheNumberOfMoves() {
        return theNumberOfMoves;
    }

    public void setTheNumberOfMoves(int theNumberOfMoves) {
        this.theNumberOfMoves = theNumberOfMoves;
    }

    public int getTheLongestStreak() {
        return theLongestStreak;
    }

    public void setTheLongestStreak(int theLongestStreak) {
        this.theLongestStreak = theLongestStreak;
    }
}
