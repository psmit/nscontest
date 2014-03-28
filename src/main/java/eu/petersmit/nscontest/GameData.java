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
    INTERCITY,
    SPRINTERA,
    SPRINTERB
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
        int speed = 130;
        switch (trainType) {
            case INTERCITY:
            case SPRINTERB:
                speed = 140;
                break;
            case SPRINTERA:
                speed = 125;
        }

        return (int) floor((double) trackDistances[fromStation][toStation] / ((double) speed / 60.0));
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
