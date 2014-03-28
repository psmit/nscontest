package eu.petersmit.nscontest;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.fill;
import static java.util.Collections.sort;
import static org.apache.commons.lang.math.NumberUtils.max;
import static org.apache.commons.lang.math.NumberUtils.min;

/**
 * Search tree contains the node structure for finding a solution. A
 * Search algorithm is using the public methods to expand through the tree
 */
public class SearchTree {
    /**
     * Initialize the tree with it's root node
     *
     * @param gameData GameData object
     */
    public SearchTree(GameData gameData) {
        this.gameData = gameData;

        root = new Node(gameData);
    }

    /**
     * Discover the children of a Node n
     *
     * @param n Node
     */
    void expandNode(Node n) {
        if (n.children != null) return;
        List<Move> moves = new ArrayList<Move>();


        for (int train = 0; train < gameData.trainIds.length; ++train) {

            for (int targetStation = 0; targetStation < gameData.stationNames.length; ++targetStation) {
                if (n.trainStations[train] == targetStation) continue;
                if (gameData.trackBlocked[n.trainStations[train]][targetStation]) continue;

                for (int driver = 0; driver < gameData.personnelIds.length; ++driver) {
                    if (n.personnelStations[driver] != n.trainStations[train]) continue;
                    if (gameData.personnelTypes[driver] != PersonnelType.DRIVER) continue;

                    for (int conductor = 0; conductor < gameData.personnelIds.length; ++conductor) {
                        if (n.personnelStations[conductor] != n.trainStations[train]) continue;
                        if (gameData.personnelTypes[conductor] != PersonnelType.CONDUCTOR) continue;

                        Move m = new Move();
                        m.train = train;
                        m.fromStation = n.trainStations[train];
                        m.toStation = targetStation;
                        m.driver = driver;
                        m.conductor = conductor;

                        m.timeStart = max(n.trainTimes[train], n.personnelTimes[conductor], n.personnelTimes[driver]);
                        m.timeEnd = m.timeStart + gameData.getTravelTime(m.fromStation, m.toStation, gameData.trainTypes[train]);

                        m.personnelPassengers = new int[0];

                        moves.add(m);
                    }
                }
            }
        }
        sort(moves);
        n.children = new ArrayList<Node>();

        for (Move m : moves) {
            if (n.move != null && (n.move.compareTo(m) > 0)) continue;
            n.children.add(new Node(n, m));
        }
    }

    /**
     * Check if the node is a solution to the problem.
     *
     * @param n Node to check
     * @return True if the node is a solution
     */
    boolean isFinalNode(Node n) {
        for (int[] passengers : n.stationPassengers) {
            for (int passenger : passengers) {
                if (passenger > 0) return false;
            }
        }

        for (int train = 0; train < gameData.trainIds.length; ++train) {
            if (n.trainStations[train] != gameData.trainEndStation[train]) return false;
        }

        return true;
    }

    /**
     * Get the cost in euros of this node. Makes only sense for Final nodes
     *
     * @param n Node to check
     * @return Cost in euros
     */
    int getRealCost(Node n) {
        return 0;
    }

    /**
     * Get the root node
     *
     * @return
     */
    public Node getRoot() {
        return root;
    }

    public int countNodes() {
        return root.countChildren() + 1;
    }

    /**
     * Data structure for a node in the search tree
     */
    public class Node {
        Move move;
        int[][] stationPassengers;
        int[] trainTimes;
        int[] trainStations;
        int[][] trainPassengers;
        int[] personnelTimes;
        int[] personnelStations;

        List<Node> children = null;

        Node(GameData gameData) {
            move = null;

            stationPassengers = Util.clone(gameData.numPassengers);

            trainTimes = new int[gameData.trainIds.length];
            fill(trainTimes, 0);

            trainStations = gameData.trainStartStation.clone();

            trainPassengers = new int[gameData.trainIds.length][gameData.stationNames.length];
            for (int[] a : trainPassengers) fill(a, 0);

            personnelTimes = new int[gameData.personnelIds.length];
            fill(personnelTimes, 0);

            personnelStations = gameData.personnelStations.clone();
        }

        Node(Node parent, Move move) {
            this.move = move;
            stationPassengers = Util.clone(parent.stationPassengers);
            trainTimes = parent.trainTimes.clone();
            trainStations = parent.trainStations.clone();
            trainPassengers = Util.clone(parent.trainPassengers);
            personnelTimes = parent.personnelTimes.clone();
            personnelStations = parent.personnelStations.clone();

            trainTimes[move.train] = move.timeEnd;
            trainStations[move.train] = move.toStation;

            personnelTimes[move.driver] = move.timeEnd;
            personnelStations[move.driver] = move.toStation;

            personnelTimes[move.conductor] = move.timeEnd;
            personnelStations[move.conductor] = move.toStation;


        }

        int countChildren() {
            int count = 0;
            if (children == null) return count;

            for (Node child : children) {
                count += 1 + child.countChildren();
            }

            return count;
        }
    }

    private Node root;
    private GameData gameData;
}
