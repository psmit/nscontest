package eu.petersmit.nscontest;

import org.apache.commons.lang.ArrayUtils;

import static java.lang.Math.floor;
import static java.lang.Math.min;
import static java.util.Arrays.fill;

/**
 * Created by psmit on 3/28/14.
 */

enum PersonnelType {
    DRIVER,
    CONDUCTOR
}

enum TrainType {
    INTERCITY(140, 600),
    SPRINTERA(125, 400),
    SPRINTERB(140, 150);

    public final int speed;
    public final int capacity;

    private TrainType(int speed, int capacity) {
        this.speed = speed;
        this.capacity = capacity;
    }
}

public class GameData {

    PersonnelType[] personnelTypes;
    String[] personnelIds;
    int[] personnelStations;
    int[] personnelEndTimes;

    String[] stationNames;
    int[][] trackDistances;
    boolean[][] trackBlocked;

    int[][] numPassengers;

    String[] trainIds;
    TrainType[] trainTypes;
    int[] trainStartStation;
    int[] trainEndStation;

    int[][] minDistance;

    int getStationId(String name) {
        return ArrayUtils.indexOf(stationNames, name);
    }

    public int getTravelTime(int fromStation, int toStation, TrainType trainType) {
        return (int) floor((double) trackDistances[fromStation][toStation] / ((double) trainType.speed / 60.0));
    }

    public String getStationName(int toStation) {
        return stationNames[toStation];
    }

    public String getTrainId(int train) {
        return trainIds[train];
    }

    public String getPersonnelId(int personnel) {
        return personnelIds[personnel];
    }

    public void fillMinDistances() {
        minDistance = new int[stationNames.length][stationNames.length];
        for (int[] row : minDistance) fill(row, -1);
        for (int station = 0; station < stationNames.length; ++station) {
            minDistance[station][station] = 0;
            for (int target = 0; target < stationNames.length; ++target) {
                if (!trackBlocked[station][target]) {
                    minDistance[station][target] = minDistance[target][station] = trackDistances[station][target];
                }
            }
        }

        boolean updated = true;
        while (updated) {
            updated = false;

            for (int station = 0; station < stationNames.length; ++station) {
                for (int target = 0; target < stationNames.length; ++target) {
                    if (target == station) continue;
                    int min_distance = minDistance[station][target];
                    if (min_distance < 0) min_distance = Integer.MAX_VALUE;
                    int orig_mindistance = min_distance;


                    for (int neighbour = 0; neighbour < stationNames.length; ++neighbour) {
                        if (neighbour == station) continue;
                        if (trackBlocked[station][neighbour]) continue;
                        if (minDistance[neighbour][target] > 0) {
                            min_distance = min(min_distance, trackDistances[station][neighbour] + minDistance[neighbour][target]);
                        }
                    }

                    if (min_distance < orig_mindistance) {
                        updated = true;
                        minDistance[station][target] = minDistance[target][station] = min_distance;
                    }
                }
            }
        }
    }
}
