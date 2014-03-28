package eu.petersmit.nscontest;

import org.apache.commons.lang.ArrayUtils;

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
    int[] personnelIds;
    int[] personnelStations;
    int[] personnelEndTimes;

    String[] stationNames;
    int[][] trackDistances;
    boolean[][] trackBlocked;

    int[][] numPassengers;

    int[] trainIds;
    TrainType[] trainTypes;
    int[] trainStartStation;
    int[] trainEndStation;

    int getStationId(String name) {
        return ArrayUtils.indexOf(stationNames, name);
    }
}
