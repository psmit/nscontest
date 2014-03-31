package eu.petersmit.nscontest;

/**
 * Created by psmit on 3/28/14.
 */
public class Move {
    int train;
    int fromStation;
    int toStation;
    int driver;
    int conductor;

    int timeStart;
    int timeEnd;

    int[] passengers;
    int[] personnelPassengers;

    long cost;

    public Move() {
    }

    public Move(Move orig) {
        train = orig.train;
        fromStation = orig.fromStation;
        toStation = orig.toStation;
        driver = orig.driver;
        conductor = orig.conductor;
        timeStart = orig.timeStart;
        timeEnd = orig.timeEnd;

        if (orig.passengers != null)
            passengers = orig.passengers.clone();
        if (orig.personnelPassengers != null)
            personnelPassengers = orig.personnelPassengers.clone();

        cost = 0;
    }

}
