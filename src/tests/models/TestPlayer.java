package models;

import battleship.entities.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing Player class
 */
public class TestPlayer {
    private Player player;
    private String defaultName;
    private String newName;

    @Before
    public void init() {
        player = new Player(null);
        defaultName = "Player";
        newName = "test";
    }

    @Test
    public void constructorTest() {
        Assert.assertTrue("Player default name is NOT \"" + defaultName + "\"", "Player".equals(player.getName()));
    }

    @Test
    public void setNameTest() {
        player.setName("");
        Assert.assertTrue("Player name after setting empty string is NOT default name (\"" + defaultName + "\")",
                "Player".equals(player.getName()));

        player.setName(newName);
        Assert.assertTrue("Player name after renaming is NOT a new name (\"" + newName + "\")",
                newName.equals(player.getName()));

        player.setName(null);
        Assert.assertTrue("Player name after setting null changed (but should not)",
                newName.equals(player.getName()));

        player.setName("");
        Assert.assertTrue("Player name after setting an empty string changed (but should not)",
                newName.equals(player.getName()));
    }

    @After
    public void destroy() {
        player = null;
        defaultName = null;
        newName = null;
    }
}
