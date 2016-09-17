package service;

import battleship.ShipPlacementException;
import battleship.entities.Ship;
import battleship.service.ShipService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for testing ShipService methods
 */
public class ShipServiceTest {
    private Ship ship;

    @Test
    public void createShipTest() {
        int startX = 0;
        int startY = 0;
        try {
            ship = ShipService.createShip(startX, startY);
            Assert.assertEquals(new Ship(startX, startY), ship);

            Assert.assertNotEquals(new Ship(startX + 2, startY + 5), ship);
        } catch (ShipPlacementException e) {
            e.printStackTrace();
        }
    }
}
