package mineopoly_three;

import mineopoly_three.competition.Distance;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class DistanceTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNullPoint() {
        Distance.getDistanceBetweenPoints(null, null);
    }

    @Test
    public void testValidDistanceCalculation() {

        Point reference = new Point(0,0);
        Point target = new Point (5, 0);

        int expectedValue = 5;
        int actualValue = Distance.getDistanceBetweenPoints(reference, target).getMagnitude();

        assertEquals(expectedValue, actualValue);
    }
}
