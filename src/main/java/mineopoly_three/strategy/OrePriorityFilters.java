package mineopoly_three.strategy;

import java.util.ArrayList;
import java.util.Comparator;

public class OrePriorityFilters implements Comparator<Ore> {

    /**
     * Finds all of the ores in the given arraylist that is  within the bounds of the offset
     * @param referenceAngle the angle to begin looking for ores
     * @param offset the maximum angle away from the reference an ore can be
     * @param toFilter the array list to get the ores from
     * @return an arraylist of ores that are at most offset distance away from the reference
     */
    public static ArrayList<Ore> filterByReferenceAngle(double referenceAngle, double offset, ArrayList<Ore> toFilter) {

        ArrayList<Ore> oresToReturn = new ArrayList<>();

        double lowerBound = referenceAngle - offset;
        double upperBound = referenceAngle + offset;

        for(Ore targetOre: toFilter) {

            double targetAngle = targetOre.getReferenceAngle();
            if(isInBounds(targetAngle, lowerBound, upperBound)) {
                oresToReturn.add(targetOre);
            }
        }

        return oresToReturn;
    }

    private static boolean isInBounds(double target, double lower, double upper) {

        if(lower < 0) {
            return target > 360 - lower && target < upper;
        } else if(upper > 360) {
            return target > lower && target < upper - 360;
        } else {
            return target > lower && target < upper;
        }

    }

    @Override
    public int compare(Ore targetOre, Ore otherOre) {

        double targetValue = targetOre.getOrePriority();
        double otherValue = otherOre.getOrePriority();

        return Double.compare(targetValue, otherValue);
    }
}
