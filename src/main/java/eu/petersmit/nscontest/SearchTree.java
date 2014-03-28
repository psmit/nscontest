package eu.petersmit.nscontest;

/**
 * Created by psmit on 3/28/14.
 */
public class SearchTree {
    public SearchTree(GameData gameData) {
        this.gameData = gameData;
    }

    private class Node {
        Move move;
        int[][] stationPassengers;
        int[] trainTimes;
        int[] trainStations;
        int[][] trainPassengers;
        int[] personnelTimes;
        int[] personnelStations;
    }

    private Node root;
    private GameData gameData;


}
