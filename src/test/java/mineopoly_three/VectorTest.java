package mineopoly_three;

import mineopoly_three.competition.Vector;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class VectorTest {

    @Before
    public void setUp() {
        // This is run before every test

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPoint() {
        Vector.getAngleBetweenVectors(null, null);
    }

    @Test
    public void testValidCalculation() {

        Point reference = new Point(0,0);
        Point target = new Point (-1, 0);

        Double expectedValue = 90.0;
        Double actualValue = Vector.getAngleFromReference(reference, target);

        assertEquals(expectedValue, actualValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullVectorAngle() {
        Vector.getAngleBetweenVectors(null, null);
    }

    @Test
    public void testValidVectorAngle() {

        Vector firstVector = new Vector(1, 1);
        Vector secondVector = new Vector(-1, 1);

        Double expectedValue = 90.0;
        Double actualValue = Vector.getAngleBetweenVectors(firstVector, secondVector);

        assertEquals(expectedValue, actualValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullVectorDotProduct() {

        Vector.getDotProduct(null, null);
    }

    @Test
    public void testValidDotProduct() {
        Vector firstVector = new Vector(0, 1);
        Vector secondVector = new Vector(1, 0);

        Double expectedValue = 0.0;
        Double actualValue = Vector.getDotProduct(firstVector, secondVector);

        assertEquals(expectedValue, actualValue);
    }
}
