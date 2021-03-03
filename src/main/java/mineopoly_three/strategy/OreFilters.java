package mineopoly_three.strategy;

import java.util.ArrayList;

public class OreFilters {

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
}
