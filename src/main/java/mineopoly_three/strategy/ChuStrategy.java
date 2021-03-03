package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * ChuStrategy is a strategy for playing Mineopoloy that attempts to target ores based off of clustering
 * The priority of an ore is calculated by the weighted current price in the economy and the percentage
 * away the ore is from the current location. That means that the closer the ore is, the higher priority
 * it is. Then based off of the angle of the high priority ore, it finds ores within a offset angle away
 * because if the angle of the ore from a reference point is similar, they must be close.
 */
public class ChuStrategy implements MinePlayerStrategy {

    private static final double OFFSET_PROXIMITY = 45;

    private int maxInventorySize;
    private int maxCharge;
    private int winningScore;
    private int inventoryWorth;
    private int remainingInventorySlots;

    private boolean isRedPlayer;
    private boolean hasTask;
    private boolean isRecharging;

    private Point previousLocation;
    private TurnAction previousAction;

    private Map<ItemType, Integer> currentPrices;
    private ArrayList<Point> rechargeStationLocations;
    private ArrayList<Point> shopLocations;
    private ArrayList<Ore> allOres;
    private ArrayList<Ore> oresToMine;
    private ArrayList<TurnAction> actionsToTake;
    private OrePriorityComparator comparePriority;

    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore, PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {
        System.out.println(winningScore);
        this.maxInventorySize = maxInventorySize;
        this.maxCharge = maxCharge;
        this.winningScore = winningScore;
        this.isRedPlayer = isRedPlayer;
        comparePriority = new OrePriorityComparator();
        previousLocation = startingBoard.getYourLocation();
        remainingInventorySlots = maxInventorySize;

        currentPrices = new HashMap<>();
        actionsToTake = new ArrayList<>();
        oresToMine = new ArrayList<>();

        initializeKeyLocations(startingBoard, boardSize);
        assignOreReferenceLocation(boardSize);
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {

        Point currentLocation = boardView.getYourLocation();
        currentPrices = economy.getCurrentPrices();

        if(hasNotMoved(currentLocation)) {
            return previousAction;
        }

        TurnAction movementAction = alreadyHasAction(currentLocation);
        if(movementAction != null) {
            return movementAction;
        }

        TileType currentTile = boardView.getTileTypeAtLocation(currentLocation);
        specialTileAction(currentTile, currentLocation);

        TurnAction rechargeAction = checkRecharge(currentCharge, currentLocation);
        if(rechargeAction == null) {
            return null;
        } else if(!rechargeAction.equals(TurnAction.MINE)) {
            return rechargeAction;
        }

        TurnAction sellAction = sellItems(currentLocation);
        if(sellAction != null) {
            return sellAction;
        }

        if(oresToMine.isEmpty()) {
            getOptimalOres(findValuableOre(currentLocation));
        }

        hasTask = true;
        Ore toMine = getClosestOre(currentLocation, oresToMine);
        getDirectionsToNewLocation(currentLocation, toMine.getLocation());

        for(int i = 0; i < toMine.getTurnsToMine(); i++) {
            actionsToTake.add(TurnAction.MINE);
        }
        actionsToTake.add(TurnAction.PICK_UP_RESOURCE);

        return updateAction(currentLocation);
    }

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        remainingInventorySlots--;
        updateInventoryWorth(itemReceived.getItemType());
    }

    @Override
    public void onSoldInventory(int totalSellPrice) {
        remainingInventorySlots = maxInventorySize;
        inventoryWorth = 0;
        winningScore -= totalSellPrice;
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

    /** TurnAction Helper Methods */

    //checks if theres already an action
    private TurnAction alreadyHasAction(Point currentLocation) {

        if(actionsToTake.isEmpty()) {
            return null;
        }

        if(hasTask) {
            hasTask = !(actionsToTake.size() == 1);
            return updateAction(currentLocation);
        }

        return null;
    }

    /* checks if inventory is full*/
    private TurnAction sellItems(Point currentLocation) {

        if(remainingInventorySlots == 0 || inventoryWorth >= winningScore) {
            Point desiredShop = getClosestPoint(currentLocation, shopLocations);
            getDirectionsToNewLocation(currentLocation, desiredShop);
            hasTask = true;
            return updateAction(currentLocation);
        }

        return null;
    }

    private TurnAction checkRecharge(int currentCharge, Point currentLocation) {

        if(isRecharging && currentCharge != maxCharge) {
            return null;
        } else if(currentCharge == maxCharge) {
            getOptimalOres(findValuableOre(currentLocation));
            isRecharging = false;
        }

        if(isAlmostOutOfCharge(currentLocation, currentCharge)) {
            return updateAction(currentLocation);
        }

        return TurnAction.MINE;
    }

    /* saves previousLocation and action then updates action*/
    private TurnAction updateAction(Point currentLocation) {
        previousAction = actionsToTake.remove(0);
        previousLocation = currentLocation;
        return previousAction;
    }

    /* assigns specific actions for specific tiles */
    private void specialTileAction(TileType currentTile, Point currentLocation) {

        if(currentTile.equals(TileType.RECHARGE)) {
            isRecharging = true;
        }

        if(currentTile.equals(getCorrectShop())) {
            getOptimalOres(findValuableOre(currentLocation));
        }
    }

    /** Charging Helper Methods */

    private boolean isAlmostOutOfCharge(Point currentLocation, int currentCharge) {

        Point closestRecharge = getClosestPoint(currentLocation, rechargeStationLocations);
        Distance distanceFromRecharge = Distance.getDistanceBetweenPoints(closestRecharge, currentLocation);
        if(currentCharge <= 1.5*distanceFromRecharge.getMagnitude()) {
            getDirectionsToNewLocation(currentLocation, closestRecharge);
            hasTask = true;
            return true;
        }

        return false;
    }

    /** Shopping Helper Methods */

    private void updateInventoryWorth(ItemType obtainedItem) {

        inventoryWorth += currentPrices.get(obtainedItem);
    }

    /** Ore Helper Methods */

    /* determine the angle the ore is at in reference to the center*/
    private void assignOreReferenceLocation(int boardSize) {

        Point referencePoint = new Point(boardSize/2, boardSize/2);
        for(Ore targetOre: allOres) {
            Point oreLocation = targetOre.getLocation();
            double referenceAngle = Vector.getAngleFromReference(referencePoint, oreLocation);
            targetOre.setReferenceAngle(referenceAngle);
        }
    }

    /* adds the highest value ores to ores to mine*/
    private void getOptimalOres(Ore valueOre) {

        oresToMine.add(valueOre);
        ArrayList<Ore> closeAngles = OreFilters.filterByReferenceAngle(valueOre.getReferenceAngle(), OFFSET_PROXIMITY , allOres);
        closeAngles.sort(comparePriority);

        for(int i = closeAngles.size()-1; i >= 0; i--) {
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

    /* given a location and an array of ores, retrieve the closest ore */
    private Ore getClosestOre(Point currentLocation, ArrayList<Ore> oresToCheck) {

        int closestOreIndex = 0;
        int smallestDistance = Integer.MAX_VALUE;
        for(Ore targetOre: oresToCheck) {
            Point oreLocation = targetOre.getLocation();
            int distance = Distance.getDistanceBetweenPoints(currentLocation, oreLocation).getMagnitude();
            if(distance < smallestDistance) {
                smallestDistance = distance;
                closestOreIndex = oresToCheck.indexOf(targetOre);
            }
        }

        return oresToCheck.remove(closestOreIndex);
    }

    private Ore findValuableOre(Point currentLocation) {
        clearToMine();
        setOrePriority(currentLocation, allOres);
        allOres.sort(comparePriority);
        return allOres.remove(allOres.size()-1);
    }

    private void clearToMine() {
        for(int i = 0; i < oresToMine.size(); i++) {
            allOres.add(oresToMine.remove(0));
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

    private boolean hasNotMoved(Point currentLocation) {

        return previousAction != null && isMovingAction(previousAction) && currentLocation.equals(previousLocation);
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

    public boolean isMovingAction(TurnAction targetAction) {

        return targetAction.equals(TurnAction.MOVE_UP) || targetAction.equals(TurnAction.MOVE_DOWN)|| targetAction.equals(TurnAction.MOVE_LEFT)|| targetAction.equals(TurnAction.MOVE_RIGHT);
    }

    public TileType getCorrectShop() {
        if(isRedPlayer) {
            return TileType.RED_MARKET;
        } else {
            return TileType.BLUE_MARKET;
        }
    }
}
