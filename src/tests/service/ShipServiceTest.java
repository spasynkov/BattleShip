package service;

import battleship.ShipPlacementException;
import battleship.entities.Coordinates;
import battleship.entities.Ship;
import battleship.service.ShipService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

/**
 * Test class for testing ShipService methods
 */
public class ShipServiceTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private Ship ship;
    private int startX = 0;
    private int startY = 0;
    private int numberOfDecks = 4;
    private int endX = startX;
    private int endY = startY + numberOfDecks - 1;
    private Random random = new Random(System.currentTimeMillis());

    @Test
    public void createShipWithOneDeckTest() throws ShipPlacementException {
        ship = ShipService.createShip(startX, startY);
        assertNotNull(ship);
        assertEquals(new Ship(startX, startY), ship);

        assertNotEquals(
                new Ship(
                        startX + random.nextInt(10) + 1,    // no need of random value 0 here
                        startY + random.nextInt(10) + 1),   // and here too. we are ok with [1..11]
                ship);

    }

    @Test
    public void createShipWithOneDeckFailWithNegativeXTest() throws ShipPlacementException {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Coordinates for ship and decks number should be a positive value."));
        ship = ShipService.createShip(-1, 0);
    }

    @Test
    public void createShipWithOneDeckFailWithNegativeYTest() throws ShipPlacementException {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Coordinates for ship and decks number should be a positive value."));
        ship = ShipService.createShip(0, -1);
    }

    @Test
    public void createHorizontalShipWithManyDecsTest() throws Exception {
        // horizontal ship should be like (startX; startY) - (startX + numberOfDecks - 1; startY)
        ship = ShipService.createShip(startX, startY, numberOfDecks, startX + numberOfDecks - 1, startY);

        boolean isHorizontal = true;

        assertNotNull(ship);
        assertEquals(new Ship(startX, startY, numberOfDecks, isHorizontal), ship);

        assertNotEquals(new Ship(startX + 1, startY, numberOfDecks, isHorizontal), ship);
        assertNotEquals(new Ship(startX, startY + 1, numberOfDecks, isHorizontal), ship);
        assertNotEquals(new Ship(startX, startY, numberOfDecks + 1, isHorizontal), ship);
        assertNotEquals(new Ship(startX, startY, numberOfDecks, !isHorizontal), ship);
    }

    @Test
    public void createVerticalShipWithManyDecsTest() throws Exception {
        // vertical ship should be like (startX; startY) - (startX; startY + numberOfDecks - 1)
        ship = ShipService.createShip(startX, startY, numberOfDecks, startX, startY + numberOfDecks - 1);

        boolean isHorizontal = false;

        assertNotNull(ship);
        assertEquals(new Ship(startX, startY, numberOfDecks, isHorizontal), ship);

        assertNotEquals(new Ship(startX + 1, startY, numberOfDecks, isHorizontal), ship);
        assertNotEquals(new Ship(startX, startY + 1, numberOfDecks, isHorizontal), ship);
        assertNotEquals(new Ship(startX, startY, numberOfDecks + 1, isHorizontal), ship);
        assertNotEquals(new Ship(startX, startY, numberOfDecks, !isHorizontal), ship);
    }

    @Test
    public void createShipWithManyDecsFailWithNegativeStartXTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Coordinates for ship and decks number should be a positive value."));
        ship = ShipService.createShip(-1, startY, numberOfDecks, endX, endY);
    }

    @Test
    public void createShipWithManyDecsFailWithNegativeStartYTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Coordinates for ship and decks number should be a positive value."));
        ship = ShipService.createShip(startX, -1, numberOfDecks, endX, endY);
    }

    @Test
    public void createShipWithManyDecsFailWithNegativeNumberOfDecksTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Coordinates for ship and decks number should be a positive value."));
        ship = ShipService.createShip(startX, startY, -1, endX, endY);
    }

    @Test
    public void createShipWithManyDecsFailWithNegativeEndXTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Coordinates for ship and decks number should be a positive value."));
        ship = ShipService.createShip(startX, startY, numberOfDecks, -1, endY);
    }

    @Test
    public void createShipWithManyDecsFailWithNegativeEndYTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Coordinates for ship and decks number should be a positive value."));
        ship = ShipService.createShip(startX, startY, numberOfDecks, endX, -1);
    }

    @Test
    public void createShipWithManyDecsFailForDiagonalShipTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Ships could be placed only at horizontal or vertical lines."));
        ship = ShipService.createShip(
                startX,
                startY,
                numberOfDecks,
                startX + numberOfDecks - 1,
                startY + numberOfDecks - 1);
    }

    @Test
    public void createShipWithManyDecsFailForInclinedByXShipTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Ships could be placed only at horizontal or vertical lines."));
        // horizontal ship should be like (startX; startY) - (startX + numberOfDecks - 1; startY)
        ship = ShipService.createShip(startX, startY, numberOfDecks, startX + numberOfDecks - 1, startY + 1);
    }

    @Test
    public void createShipWithManyDecsFailForInclinedByYShipTest() throws Exception {
        exception.expect(ShipPlacementException.class);
        exception.expectMessage(containsString(
                "Ships could be placed only at horizontal or vertical lines."));
        // vertical ship should be like (startX; startY) - (startX; startY + numberOfDecks - 1)
        ship = ShipService.createShip(startX, startY, numberOfDecks, startX + 1, startY + numberOfDecks - 1);
    }

    @Test
    public void hitShipWithOneDeckTest() throws Exception {
        ship = new Ship(startX, startY);

        assertTrue(ship.getSafeDecks() > 0);

        ShipService.hit(ship);

        assertTrue(ship.getSafeDecks() == 0);

        exception.expect(RuntimeException.class);
        exception.expectMessage(containsString("No more alive decks left in this ship"));
        ShipService.hit(ship);
    }

    @Test
    public void hitShipWithManyDecksTest() throws Exception {
        ship = new Ship(startX, startY, numberOfDecks, true);   // lets create some horizontal ship

        int safeDecks = ship.getSafeDecks();
        assertTrue(safeDecks > 0);

        for (int i = 0; i < numberOfDecks; i++) {
            ShipService.hit(ship);
            assertTrue(ship.getSafeDecks() < safeDecks);
            safeDecks = ship.getSafeDecks();
        }
        assertTrue(ship.getSafeDecks() == 0);

        exception.expect(RuntimeException.class);
        exception.expectMessage(containsString("No more alive decks left in this ship"));
        ShipService.hit(ship);
    }

    @Test
    public void isShipAliveTest() throws Exception {
        // for ship with one deck
        ship = new Ship(startX, startY);
        assertTrue(ShipService.isShipAlive(ship));
        ShipService.hit(ship);
        assertFalse(ShipService.isShipAlive(ship));

        // for ship with many decks
        ship = new Ship(startX, startY, numberOfDecks, true);   // lets create some horizontal ship
        assertTrue(ShipService.isShipAlive(ship));

        for (int i = 0; i < numberOfDecks - 1; i++) {
            ShipService.hit(ship);
            assertTrue(ShipService.isShipAlive(ship));
        }
        ShipService.hit(ship);      // doing last hit
        assertFalse(ShipService.isShipAlive(ship));
    }

    @Test
    public void getCoordinatesOfTheShipWithOneDeckTest() throws Exception {
        ship = new Ship(startX, startY);
        Set<Coordinates> actualListOfCoordinates = ShipService.getCoordinatesOfTheShip(ship);
        assertNotNull(actualListOfCoordinates);
        assertFalse(actualListOfCoordinates.contains(null));

        Coordinates expectedCoordinates = new Coordinates(startX, startY);
        assertTrue(actualListOfCoordinates.contains(expectedCoordinates));

        assertTrue(actualListOfCoordinates.size() == 1);

        // check if it contains some other values
        Set<Coordinates> clone = new HashSet<>(actualListOfCoordinates);
        Set<Coordinates> expectedListOfCoordinates = new HashSet<>();
        expectedListOfCoordinates.add(expectedCoordinates);
        assertFalse(actualListOfCoordinates.retainAll(expectedListOfCoordinates));
        assertEquals(clone, actualListOfCoordinates);
    }

    @Test
    public void getCoordinatesOfTheHorizontalShipTest() throws Exception {
        ship = new Ship(startX, startY, numberOfDecks, true);   // lets create some horizontal ship
        Set<Coordinates> actualListOfCoordinates = ShipService.getCoordinatesOfTheShip(ship);
        assertNotNull(actualListOfCoordinates);
        assertFalse(actualListOfCoordinates.contains(null));

        // creating list of coordinates
        Set<Coordinates> expectedListOfCoordinates = new HashSet<>();
        for (int i = 0; i < numberOfDecks; i++) {
            expectedListOfCoordinates.add(new Coordinates(startX + i, startY));
        }

        assertTrue(actualListOfCoordinates.containsAll(expectedListOfCoordinates));

        assertTrue(actualListOfCoordinates.size() == numberOfDecks);

        // check if it contains some other values
        Set<Coordinates> clone = new HashSet<>(actualListOfCoordinates);
        assertFalse(actualListOfCoordinates.retainAll(expectedListOfCoordinates));
        assertEquals(clone, actualListOfCoordinates);
    }

    @Test
    public void getCoordinatesOfTheVerticalShipTest() throws Exception {
        ship = new Ship(startX, startY, numberOfDecks, false);  // lets create some vertical ship
        Set<Coordinates> actualListOfCoordinates = ShipService.getCoordinatesOfTheShip(ship);
        assertNotNull(actualListOfCoordinates);
        assertFalse(actualListOfCoordinates.contains(null));

        // creating list of coordinates
        Set<Coordinates> expectedListOfCoordinates = new HashSet<>();
        for (int i = 0; i < numberOfDecks; i++) {
            expectedListOfCoordinates.add(new Coordinates(startX, startY + i));
        }

        assertTrue(actualListOfCoordinates.containsAll(expectedListOfCoordinates));

        assertTrue(actualListOfCoordinates.size() == numberOfDecks);

        // check if it contains some other values
        Set<Coordinates> clone = new HashSet<>(actualListOfCoordinates);
        assertFalse(actualListOfCoordinates.retainAll(expectedListOfCoordinates));
        assertEquals(clone, actualListOfCoordinates);
    }
}
