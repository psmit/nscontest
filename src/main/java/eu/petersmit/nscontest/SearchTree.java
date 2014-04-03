package eu.petersmit.nscontest;

import java.util.*;

import static eu.petersmit.nscontest.Util.powerset;
import static java.util.Arrays.fill;
import static java.util.Collections.sort;
import static org.apache.commons.lang.ArrayUtils.toPrimitive;

/**
 * Search tree contains the node structure for finding a solution. A
 * Search algorithm is using the public methods to expand through the tree
 */
public class SearchTree {
    private Node root;
    private GameData gameData;

    /**
     * Initialize the tree with it's root node
     *
     * @param gameData GameData object
     */
    public SearchTree(GameData gameData) {
        this.gameData = gameData;

        root = new Node(gameData);
    }

    private static int departureTime(Node n, Set<Integer> persons) {
        int time = 0;
        for (int person : persons) {
            time = Math.max(time, n.personnelTimes[person]);
        }
        return time;
    }

    /**
     * Discover the children of a Node n
     *
     * @param n Node
     */
    void expandNode(Node n) {
        if (n.children != null) return;

        List<Move> moves = getTrainMoves(n);
        moves = addDestinationToMoves(n, moves);
        moves = addDriverToMoves(n, moves);
        moves = addConductorToMoves(n, moves);
        moves = addOtherPersonnelToMoves(n, moves);
        moves = addPassengersToMoves(n, moves);
        calculateTravelTimes(moves);
        calculateCosts(moves);


        sort(moves, new MoveCompare());
        n.children = new ArrayList<Node>();

        for (Move m : moves) {
            if (n.move != null && (new MoveCompare().compare(n.move, m) > 0)) continue;
            n.children.add(new Node(n, m));
        }
    }

    private void calculateCosts(List<Move> moves) {
        for (Move move : moves) {
            move.cost = move.timeEnd - move.timeStart;
        }

    }

    public int minCost(Node n) {
        int cost = 0;
        for (int station = 0; station < gameData.stationNames.length; ++station) {
            for (int target_station = 0; target_station < gameData.stationNames.length; ++target_station) {
                if (n.stationPassengers[station][target_station] > 0) {
                    cost += (int) Math.floor((double) n.stationPassengers[station][target_station] *
                            (double) gameData.minDistance[station][target_station] / (TrainType.INTERCITY.speed / 60.0) / (double) TrainType.INTERCITY.capacity);
                }
            }
        }

        for (int train = 0; train < gameData.trainIds.length; ++train) {
            int min_distance = Integer.MAX_VALUE;
            for (int station = 0; station < gameData.stationNames.length; ++station) {
                if (station == gameData.trainEndStation[train] || hasPassengers(n, station)) {
                    min_distance = Math.min(min_distance, gameData.minDistance[n.trainStations[train]][station]);
                }
            }
            cost += (int) Math.floor((double) min_distance / (gameData.trainTypes[train].speed / 60.0));
        }

        return cost;
    }

    private List<Move> getTrainMoves(Node node) {
        List<Move> moves = new ArrayList<Move>();
        for (int train = 0; train < gameData.trainIds.length; ++train) {
            Move move = new Move();
            move.train = train;
            move.fromStation = node.trainStations[train];
            move.timeStart = node.trainTimes[train];
            moves.add(move);
        }
        return moves;
    }

    private List<Move> addDestinationToMoves(Node node, List<Move> origMoves) {
        List<Move> newMoves = new ArrayList<Move>();
        for (Move origMove : origMoves) {
            for (int station = 0; station < gameData.stationNames.length; ++station) {
                if (gameData.trackBlocked[origMove.fromStation][station]) continue;

                Move newMove = new Move(origMove);
                newMove.toStation = station;
                newMoves.add(newMove);
            }
        }

        return newMoves;
    }

    private List<Move> addDriverToMoves(Node node, List<Move> origMoves) {
        List<Move> newMoves = new ArrayList<Move>();
        for (Move origMove : origMoves) {
            for (int person = 0; person < gameData.personnelIds.length; ++person) {
                if (gameData.personnelTypes[person] != PersonnelType.DRIVER || node.personnelStations[person] != origMove.fromStation)
                    continue;

                Move newMove = new Move(origMove);
                newMove.driver = person;
                newMove.timeStart = Math.max(origMove.timeStart, node.personnelTimes[person]);
                newMoves.add(newMove);
            }
        }
        return newMoves;
    }

    private List<Move> addConductorToMoves(Node node, List<Move> origMoves) {
        List<Move> newMoves = new ArrayList<Move>();
        for (Move origMove : origMoves) {
            for (int person = 0; person < gameData.personnelIds.length; ++person) {
                if (gameData.personnelTypes[person] != PersonnelType.CONDUCTOR || node.personnelStations[person] != origMove.fromStation)
                    continue;

                Move newMove = new Move(origMove);
                newMove.conductor = person;
                newMove.timeStart = Math.max(origMove.timeStart, node.personnelTimes[person]);
                newMoves.add(newMove);
            }
        }
        return newMoves;
    }

    private List<Move> addOtherPersonnelToMoves(Node node, List<Move> origMoves) {
        List<Move> newMoves = new ArrayList<Move>();
        for (Move origMove : origMoves) {
            Set<Integer> eligibles = new HashSet<Integer>();

            for (int person = 0; person < gameData.personnelIds.length; ++person) {
                if (node.personnelStations[person] != origMove.fromStation) continue;
                if (gameData.personnelTypes[person] == PersonnelType.DRIVER && person > origMove.driver) {
                    eligibles.add(person);
                }
                if (gameData.personnelTypes[person] == PersonnelType.CONDUCTOR && person > origMove.conductor) {
                    eligibles.add(person);
                }
            }

            for (Set<Integer> subset : powerset(eligibles)) {
                Move newMove = new Move(origMove);
                Integer[] persons = new Integer[subset.size()];
                subset.toArray(persons);
                newMove.personnelPassengers = toPrimitive(persons);
                newMove.timeStart = Math.max(origMove.timeStart, departureTime(node, subset));
                newMoves.add(newMove);
            }
        }
        return newMoves;
    }

    private boolean hasPassengers(Node node, int station) {
        for (int target_station = 0; target_station < gameData.stationNames.length; ++target_station) {
            if (node.stationPassengers[station][target_station] > 0) return true;
        }
        return false;
    }

    private boolean isOkEmptyPassengerMove(Node node, Move move) {
        for (int realTarget = 0; realTarget < gameData.stationNames.length; ++realTarget) {
            if ((realTarget == gameData.trainEndStation[move.train] || hasPassengers(node, realTarget)) &&
                    (gameData.minDistance[move.fromStation][realTarget] > gameData.minDistance[move.toStation][realTarget])) {

                return true;

            }
        }
        //TODO, make sure train goes closer to passengers or end station
        return false;
    }
    private List<Move> addPassengersToMoves(Node node, List<Move> origMoves) {
        //TODO: at this moment only one type of passengers go with one train. Fix this later
        //TODO: no check if passengers have actually arrived at station
        List<Move> newMoves = new ArrayList<Move>();
        for (Move origMove : origMoves) {
            for (int passengerDestination = 0; passengerDestination < gameData.stationNames.length; ++passengerDestination) {
                if (passengerDestination == origMove.fromStation ||
                        node.stationPassengers[origMove.fromStation][passengerDestination] == 0) continue;

                Move newMove = new Move(origMove);
                newMove.passengers = new int[gameData.stationNames.length];
                fill(newMove.passengers, 0);
                newMove.passengers[passengerDestination] = Math.min(gameData.trainTypes[origMove.train].capacity,
                        node.stationPassengers[origMove.fromStation][passengerDestination]);

                newMoves.add(newMove);
            }
            if (isOkEmptyPassengerMove(node, origMove)) {
                Move newMove = new Move(origMove);
                newMove.passengers = new int[gameData.stationNames.length];
                fill(newMove.passengers, 0);
                newMoves.add(newMove);
            }
        }
        return newMoves;
    }

    private void calculateTravelTimes(List<Move> moves) {
        for (Move move : moves) {
            move.timeEnd = move.timeStart +
                    gameData.getTravelTime(move.fromStation, move.toStation, gameData.trainTypes[move.train]);
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
     * @return root node
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

        long cost;

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

            cost = 0;
        }

        Node(Node parent, Move move) {
            this.move = move;
            stationPassengers = Util.clone(parent.stationPassengers);
            trainTimes = parent.trainTimes.clone();
            trainStations = parent.trainStations.clone();
            trainPassengers = Util.clone(parent.trainPassengers);
            personnelTimes = parent.personnelTimes.clone();
            personnelStations = parent.personnelStations.clone();

            for (int destination = 0; destination < gameData.stationNames.length; ++destination) {
                stationPassengers[move.fromStation][destination] -= move.passengers[destination];
                stationPassengers[move.toStation][destination] += move.passengers[destination];
            }

            trainTimes[move.train] = move.timeEnd;
            trainStations[move.train] = move.toStation;

            personnelTimes[move.driver] = move.timeEnd;
            personnelStations[move.driver] = move.toStation;

            personnelTimes[move.conductor] = move.timeEnd;
            personnelStations[move.conductor] = move.toStation;

            for (int person : move.personnelPassengers) {
                personnelTimes[person] = move.timeEnd;
                personnelStations[person] = move.toStation;
            }

            cost = parent.cost + move.cost;

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

    static class MoveCompare implements Comparator<Move> {

        @Override
        public int compare(Move move, Move move2) {
            if (move.timeStart != move2.timeStart) {
                return move.timeStart - move2.timeStart;
            } else if (move.train != move2.train) {
                return move.train - move2.train;
            }
            return 0;
        }
    }
}
