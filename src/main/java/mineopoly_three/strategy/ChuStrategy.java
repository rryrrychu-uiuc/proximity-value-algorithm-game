package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.tiles.TileType;

import java.awt.*;
import java.util.*;

public class ChuStrategy implements MinePlayerStrategy {

    private int boardSize;
    private int maxInventorySize;
    private int maxCharge;
    private int winningScore;
    private boolean isRedPlayer;

    private ArrayList<Point> rechargeStationLocations;
    private ArrayList<Point> shopLocations;
    private ArrayList<Ore> allOres;
    private ArrayList<TurnAction> actionsToTake;


    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore, PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {

        this.boardSize = boardSize;
        this.maxInventorySize = maxInventorySize;
        this.maxCharge = maxCharge;
        this.winningScore = winningScore;
        this.isRedPlayer = isRedPlayer;
        actionsToTake = new ArrayList<>();

        getKeyLocations(startingBoard, boardSize);
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
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

    /* instantiates arraylist for all of the key tiles on the board */
    private void getKeyLocations(PlayerBoardView currentBoard, int boardSize) {

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
                    allOres.add(new Ore(targetLocation,targetTile));
                }
            }
        }
    }

    /* check if a tile is an ore */
    private boolean isOre(TileType toEvaluate) {
        return toEvaluate.equals(TileType.RESOURCE_DIAMOND) || toEvaluate.equals(TileType.RESOURCE_RUBY) || toEvaluate.equals(TileType.RESOURCE_EMERALD);
    }

    /* check if the correct shop*/
    public boolean isCorrectShopColor(TileType shopType) {
        return (shopType.equals(TileType.RED_MARKET) && isRedPlayer) || (shopType.equals(TileType.BLUE_MARKET) && !isRedPlayer );
    }
}