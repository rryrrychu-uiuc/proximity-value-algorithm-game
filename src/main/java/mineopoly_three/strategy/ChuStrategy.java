package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;

public class ChuStrategy implements MinePlayerStrategy {

    private static final double OFFSET_PROXIMITY = 10.0;

    private int boardSize;
    private int maxInventorySize;
    private int maxCharge;
    private int winningScore;
    private boolean isRedPlayer;
    private Point referencePoint;

    private Map<ItemType, Integer> currentPrices;
    private ArrayList<Point> rechargeStationLocations;
    private ArrayList<Point> shopLocations;
    private ArrayList<Ore> allOres;
    private ArrayList<Ore> oresToMine;
    private ArrayList<TurnAction> actionsToTake;

    private OrePriorityComparator comparePriority;

    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore, PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {

        this.boardSize = boardSize;
        this.maxInventorySize = maxInventorySize;
        this.maxCharge = maxCharge;
        this.winningScore = winningScore;
        this.isRedPlayer = isRedPlayer;
        referencePoint = new Point(boardSize/2, boardSize/2);
        comparePriority = new OrePriorityComparator();

        currentPrices = new HashMap<>();
        actionsToTake = new ArrayList<>();
        oresToMine = new ArrayList<>();

        initializeKeyLocations(startingBoard, boardSize);
        assignOreReferenceLocation();
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {

        Point currentLocation = boardView.getYourLocation();


       currentPrices = economy.getCurrentPrices();
       getOptimalOres(currentLocation);

       return null;
    }

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
    }

    @Override
    public void onSoldInventory(int totalSellPrice) {

    }

    @Override
    public String getName() {
        return "ChuStrategy";
    }

    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {

    }

    /* instantiates arraylist for all of the key tiles on the board */
    private void initializeKeyLocations(PlayerBoardView currentBoard, int boardSize) {

        rechargeStationLocations = new ArrayList<>();
        shopLocations = new ArrayList<>();
        allOres = new ArrayList<>();

        for(int row = 0; row < boardSize; row++) {
            for(int col = 0; col < boardSize; col++) {

                Point targetLocation = new Point(row, col);
                TileType targetTile = currentBoard.getTileTypeAtLocation(targetLocation);

                if (targetTile.equals(TileType.RECHARGE)){
                    rechargeStationLocations.add(targetLocation);
                } else if (isCorrectShopColor(targetTile)) {
                    shopLocations.add(targetLocation);
                } else if(isOre(targetTile)) {
                    allOres.add(new Ore(targetLocation,boardSize,targetTile));
                }
            }
        }
    }

    /** Ore Helper Methods */

    /* determine the angle the ore is at in reference to the center*/
    private void assignOreReferenceLocation() {

        for(Ore targetOre: allOres) {
            Point oreLocation = targetOre.getLocation();
            double referenceAngle = Vector.getAngleFromReference(referencePoint, oreLocation);
            targetOre.setReferenceAngle(referenceAngle);
        }
    }

    private void getOptimalOres(Point currentLocation) {

        double locationAngle = Vector.getAngleFromReference(referencePoint, currentLocation);
        ArrayList<Ore> closeAngles = OreFilters.filterByReferenceAngle(locationAngle, OFFSET_PROXIMITY ,allOres);

        setOrePriority(currentLocation, closeAngles);
        closeAngles.sort(comparePriority);

        for(int i = 0; i < maxInventorySize; i++) {
            oresToMine.add(closeAngles.get(i));
            allOres.remove(closeAngles.get(i));
        }
    }

    /* sets ore priority given location and prices*/
    private void setOrePriority(Point currentLocation, ArrayList<Ore> targetOres) {

        for(Ore toUpdate: targetOres) {

            int sellValue = currentPrices.get(toUpdate.getResourceType());
            toUpdate.setOrePriority(sellValue, currentLocation);
        }
    }

    /** Movement Helper Methods */

    /* find the closest point in an arrayList of points */
    private Point getClosestPoint(Point currentLocation, ArrayList<Point> pointsToCheck) {

        Point closestPoint = null;
        int smallestDistance = Integer.MAX_VALUE;
        for(Point targetPoint: pointsToCheck) {
            int distance = Distance.getDistanceBetweenPoints(currentLocation, targetPoint).getMagnitude();
            if(distance < smallestDistance) {
                smallestDistance = distance;
                closestPoint = targetPoint;
            }
        }

        return closestPoint;
    }

    /* given two points, find the optimal path between them */
    private void getDirectionsToNewLocation(Point currentLocation, Point newLocation) {

        addDirectionsToDesiredActions(currentLocation.getX() - newLocation.getX(), TurnAction.MOVE_RIGHT, TurnAction.MOVE_LEFT);
        addDirectionsToDesiredActions(currentLocation.getY() - newLocation.getY(), TurnAction.MOVE_UP, TurnAction.MOVE_DOWN);
    }

    /* add optimal directions to actions that need to be taken */
    private void addDirectionsToDesiredActions(double netDirection, TurnAction positiveAction, TurnAction negativeAction) {

        TurnAction actionsToAdd;
        int increment;

        if(netDirection > 0) {
            actionsToAdd = negativeAction;
            increment = -1;
        } else {
            actionsToAdd = positiveAction;
            increment = 1;
        }

        while(netDirection != 0) {
            actionsToTake.add(actionsToAdd);
            netDirection += increment;
        }
    }

    /** TileType Helper Methods*/

    /* check if a tile is an ore */
    private boolean isOre(TileType toEvaluate) {
        return toEvaluate.equals(TileType.RESOURCE_DIAMOND) || toEvaluate.equals(TileType.RESOURCE_RUBY) || toEvaluate.equals(TileType.RESOURCE_EMERALD);
    }

    /* check if the correct shop*/
    public boolean isCorrectShopColor(TileType shopType) {
        return (shopType.equals(TileType.RED_MARKET) && isRedPlayer) || (shopType.equals(TileType.BLUE_MARKET) && !isRedPlayer );
    }
}
