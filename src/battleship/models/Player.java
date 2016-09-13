package battleship.models;

/**
 * Player of the game entity
 */
public class Player {
    private String name;
    private Field field;

    private Player() {
        name = "Player";
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
        if (field == null) throw new RuntimeException("No field created yet for player" + name + "!");
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
