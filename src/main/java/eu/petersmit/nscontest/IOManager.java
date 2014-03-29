package eu.petersmit.nscontest;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.StringUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.fill;
import static org.apache.commons.lang.ArrayUtils.toObject;

/**
 * IOManager is a collection of static methods that read values from
 * csv files into a gamedata object and write moves back to csv
 * <p/>
 * No checks are done, but the connections file (verbindingen.csv)
 * should be read first to initialize the station names.
 */
public class IOManager {

    // Private constructor to prevent creating instance of class
    private IOManager() {
    }

    /**
     * Read a connection file into the gamedata object.
     * <p/>
     * Expected fields:
     * station name (string), station name (string), distance (int), blocked (0/1)
     * <p/>
     * First line of csv file is expected to be headers and is ignored.
     *
     * @param gameData GameData object to fill
     * @param filename File to read from
     * @throws IOException In case the file can not be read
     */
    static void readConnection(GameData gameData, String filename) throws IOException {
        List<String[]> connectionEntries = new ArrayList<String[]>();

        Set<String> stationNames = new HashSet<String>();

        CSVReader csvReader = new CSVReader(new FileReader(filename), ',', '"', 1);
        for (String[] line : csvReader.readAll()) {
            if (line.length >= 4) {
                connectionEntries.add(line);
                stationNames.add(line[0]);
                stationNames.add(line[1]);
            }
        }

        gameData.stationNames = new String[stationNames.size()];
        gameData.trackDistances = new int[stationNames.size()][stationNames.size()];
        gameData.trackBlocked = new boolean[stationNames.size()][stationNames.size()];

        for (int[] arr : gameData.trackDistances) fill(arr, -1);
        for (boolean[] arr : gameData.trackBlocked) fill(arr, true);

        int j = 0;
        for (String name : stationNames) {
            gameData.stationNames[j] = name;
            j++;
        }

        for (String[] line : connectionEntries) {
            int from = gameData.getStationId(line[0]);
            int to = gameData.getStationId(line[1]);
            int distance = Integer.parseInt(line[2]);
            boolean blocked = Integer.parseInt(line[3]) == 1;

            gameData.trackDistances[from][to] = gameData.trackDistances[to][from] = distance;
            gameData.trackBlocked[from][to] = gameData.trackBlocked[to][from] = blocked;
        }
    }

    /**
     * Read a personnel file into the gamedata object.
     * <p/>
     * Expected fields:
     * id (int), type (Machinist/Conducteur), station (string), endtime (time string)
     * <p/>
     * First line of csv file is expected to be headers and is ignored.
     *
     * @param gameData GameData object to fill
     * @param filename File to read from
     * @throws IOException In case the file can not be read
     */
    static void readPersonnel(GameData gameData, String filename) throws IOException {
        List<String[]> personnelEntries = new ArrayList<String[]>();

        CSVReader csvReader = new CSVReader(new FileReader(filename), ',', '"', 1);
        for (String[] line : csvReader.readAll()) {
            if (line.length >= 4) personnelEntries.add(line);
        }

        gameData.personnelTypes = new PersonnelType[personnelEntries.size()];
        gameData.personnelIds = new String[personnelEntries.size()];
        gameData.personnelStations = new int[personnelEntries.size()];
        gameData.personnelEndTimes = new int[personnelEntries.size()];

        for (int i = 0; i < personnelEntries.size(); ++i) {
            String[] line = personnelEntries.get(i);
            gameData.personnelIds[i] = line[0];
            gameData.personnelTypes[i] = "Machinist".equals(line[1]) ? PersonnelType.DRIVER : PersonnelType.CONDUCTOR;
            gameData.personnelStations[i] = gameData.getStationId(line[2]);
            gameData.personnelEndTimes[i] = getInputTime(line[3]);
        }
    }

    /**
     * Read a passenger file into the gamedata object.
     * <p/>
     * Expected fields:
     * from (string), to(string), amount (int)
     * <p/>
     * First line of csv file is expected to be headers and is ignored.
     *
     * @param gameData GameData object to fill
     * @param filename File to read from
     * @throws IOException In case the file can not be read
     */
    static void readPassengers(GameData gameData, String filename) throws IOException {
        gameData.numPassengers = new int[gameData.stationNames.length][gameData.stationNames.length];
        for (int[] arr : gameData.numPassengers) fill(arr, 0);

        CSVReader csvReader = new CSVReader(new FileReader(filename), ',', '"', 1);
        for (String[] line : csvReader.readAll()) {
            if (line.length < 3) continue;
            gameData.numPassengers[gameData.getStationId(line[0])][gameData.getStationId(line[1])] = Integer.parseInt(line[2]);
        }
    }

    /**
     * Read a train file into the gamedata object.
     * <p/>
     * Expected fields:
     * id (int), type (Intercity/SprinterA/SprinterB), start station (string), end station (string)
     * <p/>
     * First line of csv file is expected to be headers and is ignored.
     *
     * @param gameData GameData object to fill
     * @param filename File to read from
     * @throws IOException In case the file can not be read
     */
    static void readTrains(GameData gameData, String filename) throws IOException {
        List<String[]> trainEntries = new ArrayList<String[]>();

        CSVReader csvReader = new CSVReader(new FileReader(filename), ',', '"', 1);
        for (String[] line : csvReader.readAll()) {
            if (line.length >= 4) trainEntries.add(line);
        }

        gameData.trainTypes = new TrainType[trainEntries.size()];
        gameData.trainIds = new String[trainEntries.size()];
        gameData.trainStartStation = new int[trainEntries.size()];
        gameData.trainEndStation = new int[trainEntries.size()];

        for (int i = 0; i < trainEntries.size(); ++i) {
            String[] line = trainEntries.get(i);
            gameData.trainIds[i] = line[0];
            gameData.trainTypes[i] = getTrainType(line[1]);
            gameData.trainStartStation[i] = gameData.getStationId(line[2]);
            gameData.trainEndStation[i] = gameData.getStationId(line[3]);
        }
    }

    /**
     * Write moves
     *
     * @param moves A list of moves to write
     * @param w     Writer to write the moves to
     */
    static void writeMoves(GameData gameData, List<Move> moves, Writer w) {
        CSVWriter csvWriter = new CSVWriter(w, ',', CSVWriter.NO_QUOTE_CHARACTER);
        for (Move move : moves) {

            List<String> parts = new ArrayList<String>();
            parts.add(getOutputTime(move.timeStart));
            parts.add(gameData.getStationName(move.fromStation));
            parts.add(getOutputTime(move.timeEnd));
            parts.add(gameData.getStationName(move.toStation));
            parts.add(gameData.getTrainId(move.train));
            parts.add(gameData.getPersonnelId(move.conductor));
            parts.add(gameData.getPersonnelId(move.driver));
            if (move.personnelPassengers == null) {
                parts.add("");
            } else {
                List<String> list = new ArrayList<String>();
                for (int i : move.personnelPassengers) {
                    list.add(gameData.personnelIds[i]);
                }
                parts.add(StringUtils.join(list, '|'));
            }

            csvWriter.writeNext(parts.toArray(new String[parts.size()]));
            // do something with move
            csvWriter.writeNext(new String[0]);
        }
    }

    private static int getInputTime(String time) {
        String[] parts = time.split(":", 2);
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        if (hours < 18) hours += 24;

        if (hours == 18) return minutes - 30;

        return (hours - 18) * 60 + minutes - 30;
    }

    private static String getOutputTime(int time) {
        int tempTime = time + 30;
        int hours = (18 + tempTime / 60) % 24;
        int minutes = tempTime % 60;

        return String.format("%02d%02d", hours, minutes);
    }

    private static TrainType getTrainType(String name) {
        if ("Intercity".equals(name)) return TrainType.INTERCITY;
        if ("SprinterA".equals(name)) return TrainType.SPRINTERA;
        return TrainType.SPRINTERB;
    }
}
