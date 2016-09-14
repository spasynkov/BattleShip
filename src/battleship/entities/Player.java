package battleship.entities;

import java.util.HashSet;
import java.util.Set;

/**
 * Player of the game entity
 */
public class Player {
    private String name;

    /**
     * The field to store player's ships
     */
    private Field field;

    /**
     * The lists of coordinates player shoots at
     */
    private Set<Coordinates> coordinatesList;

    private Player() {
        name = "Player";
        coordinatesList = new HashSet<>();
    }

    public Player(String name) {
        this();
        setName(name);
    }

    public Player(String name, Field field) {
        this(name);
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public Field getField() {
        if (field == null) throw new RuntimeException("The field is not created yet for player" + name + "!");
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Set<Coordinates> getCoordinatesList() {
        return coordinatesList;
    }

    public void addCoordinates(Coordinates coordinates) {
        if (coordinates != null) {
            coordinatesList.add(coordinates);
        }
    }
}
