package mineopoly_three;

import mineopoly_three.strategy.Ore;
import mineopoly_three.strategy.OrePriorityFilters;
import mineopoly_three.tiles.TileType;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class OrePriorityFilterTest {

    private OrePriorityFilters priorityFilter;

    @Before
    public void setUp() {
        priorityFilter = new OrePriorityFilters();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFilterArray() {
        OrePriorityFilters.filterByReferenceAngle(0, 0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyFilterArray() {

        ArrayList<Ore> targetOres = new ArrayList<Ore>();
        OrePriorityFilters.filterByReferenceAngle(0, 0, targetOres);
    }

    @Test
    public void testValidReferenceFilter() {

        Point location = new Point(0,0);
        int boardSize = 5;
        Ore inBounds = new Ore(location, boardSize, TileType.RESOURCE_DIAMOND);
        inBounds.setReferenceAngle(12.0);
        Ore outOfBounds = new Ore(location, boardSize, TileType.RESOURCE_DIAMOND);
        outOfBounds.setReferenceAngle(20);
        ArrayList<Ore> targetOres = new ArrayList<Ore>();

        targetOres.add(inBounds);
        targetOres.add(outOfBounds);

        Ore expectedValue = inBounds;
        Ore actualValue = OrePriorityFilters.filterByReferenceAngle(13.0, 5, targetOres).get(0);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void validOreValueSort() {
        Point oreLocation = new Point(0,0);
        Point currentLocation = new Point(5,0);
        int boardSize = 5;
        Ore firstOre = new Ore(oreLocation, boardSize, TileType.RESOURCE_DIAMOND);
        Ore secondOre = new Ore(oreLocation, boardSize, TileType.RESOURCE_DIAMOND);
        Ore thirdOre = new Ore(oreLocation, boardSize, TileType.RESOURCE_DIAMOND);
        firstOre.setOrePriority(10, currentLocation);
        secondOre.setOrePriority(100, currentLocation);
        thirdOre.setOrePriority(2, currentLocation);

        ArrayList<Ore> targetOres = new ArrayList<Ore>();

        targetOres.add(firstOre);
        targetOres.add(secondOre);
        targetOres.add(thirdOre);
        targetOres.sort(priorityFilter);

        Object[] expectedValue = {thirdOre, firstOre, secondOre};
        Object[] actualValue = targetOres.toArray();

        assertArrayEquals(expectedValue, actualValue);
    }
}
