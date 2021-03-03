package mineopoly_three.strategy;

import java.util.Comparator;

public class OrePriorityComparator implements Comparator<Ore> {

    @Override
    public int compare(Ore targetOre, Ore otherOre) {

        double targetValue = targetOre.getOrePriority();
        double otherValue = otherOre.getOrePriority();

        return Double.compare(otherValue, targetValue);
    }
}
