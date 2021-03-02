package mineopoly_three.strategy;

import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;

import java.awt.*;

public class Ore {

    private Point location;
    private int turnsToMine;
    private ItemType resourceType;

    public Ore(Point loc, TileType type) {
        location = loc;
        resourceType = getResourceType(type);
        turnsToMine = resourceType.getTurnsToMine();

    }

    private ItemType getResourceType(TileType tileType) {
        return switch (tileType) {
            case RESOURCE_DIAMOND -> ItemType.DIAMOND;
            case RESOURCE_RUBY -> ItemType.RUBY;
            case RESOURCE_EMERALD -> ItemType.EMERALD;
            default -> null;
        };
    }
}
