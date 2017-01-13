package battleship.service;

import battleship.entities.Field;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for testing FieldService methods
 */
public class FieldServiceTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private Random random = new Random();
    private int maxX = 10;
    private int maxY = 10;
    private Field field = new Field(maxX, maxY, 10);
    private FieldService fieldService = new FieldService(field);

    @Before
    public void initializeFieldValues() {
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                field.getFieldValues()[x][y] = random.nextBoolean();
            }
        }

        field.getFieldValues()[0][0] = false;
        field.getFieldValues()[1][1] = true;
    }

    @Test
    public void getCellTest() throws Exception {
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                // by Coordinates object (Pair<Integer, Integer>)
                assertTrue(field.getFieldValues()[y][x] == fieldService.getCell(new Pair<>(x, y)));
                // by pure coordinates values
                assertTrue(field.getFieldValues()[y][x] == fieldService.getCell(x, y));
            }
        }

        assertFalse(fieldService.getCell(0, 0));
        assertFalse(fieldService.getCell(new Pair<>(0, 0)));

        assertTrue(fieldService.getCell(1, 1));
        assertTrue(fieldService.getCell(new Pair<>(1, 1)));
    }
}
