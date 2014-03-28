package eu.petersmit.nscontest;

import org.apache.commons.lang.ArrayUtils;

import static java.lang.Math.floor;

/**
 * Created by psmit on 3/28/14.
 */

enum PersonnelType {
    DRIVER,
    CONDUCTOR
}

enum TrainType {
    INTERCITY(140,600),
    SPRINTERA(125,400),
    SPRINTERB(140,150);

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
}
