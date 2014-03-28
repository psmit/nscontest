package eu.petersmit.nscontest;

/**
 * Created by psmit on 3/28/14.
 */
public class Move implements Comparable<Move> {
    int train;
    int fromStation;
    int toStation;
    int driver;
    int conductor;

    int timeStart;
    int timeEnd;

    int[] passengers;
    int[] personnelPassengers;

    @Override
    public int compareTo(Move otherMove) {
        if (timeStart != otherMove.timeStart) return timeStart - otherMove.timeStart;
        if (train != otherMove.train) return train - otherMove.train;
        if (toStation != otherMove.toStation) return toStation - otherMove.toStation;
        if (driver != otherMove.driver) return driver - otherMove.driver;
        if (conductor != otherMove.conductor) return conductor - otherMove.conductor;

        return 0;
    }
}
