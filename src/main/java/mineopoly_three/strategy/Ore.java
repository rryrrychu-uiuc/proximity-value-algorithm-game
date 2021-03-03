package mineopoly_three.strategy;

import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;

import java.awt.*;

public class Ore {

    private final double SELL_WEIGHT = 10;
    private Point location;
    private int turnsToMine;
    private int maxDist;
    private ItemType resourceType;
    private double referenceAngle;
    private double orePriority;

    public Ore(Point loc, int dist, TileType type) {
        location = loc;
        maxDist = dist/2;
        resourceType = getResourceType(type);
        turnsToMine = resourceType.getTurnsToMine();
    }

    public Point getLocation() {
        return location;
    }

    public double getReferenceAngle() {
        return referenceAngle;
    }

    public double getOrePriority() {
        return orePriority;
    }

    public ItemType getResourceType() {
        return resourceType;
    }

    public int getTurnsToMine() {
        return turnsToMine;
    }

    public void setReferenceAngle(double referenceAngle) {
        this.referenceAngle = referenceAngle;
    }

    public void setOrePriority(double sellValue, Point currentLocation) {

        double distanceFromOre = Distance.getDistanceBetweenPoints(currentLocation, location).getMagnitude();
        double weightedDistance = ((maxDist-distanceFromOre)/maxDist) * 100;
        double weightedPrice = sellValue * SELL_WEIGHT;

        orePriority = weightedPrice + weightedDistance;
    }

    /* given a resource tile, find the associated itemtype */
    private ItemType getResourceType(TileType tileType) {
        return switch (tileType) {
            case RESOURCE_DIAMOND -> ItemType.DIAMOND;
            case RESOURCE_RUBY -> ItemType.RUBY;
            case RESOURCE_EMERALD -> ItemType.EMERALD;
            default -> null;
        };
    }
}
