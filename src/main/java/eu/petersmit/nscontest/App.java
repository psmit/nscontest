package eu.petersmit.nscontest;


import org.apache.commons.cli.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {

    private final static Logger LOG = Logger.getLogger("test");

    private String inputDirectory;
    private Writer outWriter;

    public App(String inputDirectory, Writer outWriter) {
        this.inputDirectory = inputDirectory;
        this.outWriter = outWriter;
    }

    public void run() throws IOException {

        GameData gameData = new GameData();
        IOManager.readConnection(gameData, inputDirectory + "/verbindingen.csv");
        IOManager.readPassengers(gameData, inputDirectory + "/reizigers.csv");
        IOManager.readPersonnel(gameData, inputDirectory + "/personeel.csv");
        IOManager.readTrains(gameData, inputDirectory + "/treinstellen.csv");

        gameData.fillMinDistances();

        SearchTree searchTree = new SearchTree(gameData);

        DepthFirstSearch dfs = new DepthFirstSearch(searchTree, gameData);
        IOManager.writeMoves(gameData, dfs.search(), outWriter);
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("d", "input-directory", true, "directory to find the input csv files. Default: Current working directory");
        options.addOption("o", "output", true, "output-file. If not given or '-', output to terminal (std out)");

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("nscontest", options);
                return;
            }

            String directory = line.getOptionValue('d', ".");

            Writer w = line.hasOption('o') && !"-".equals(line.getOptionValue('o'))
                    ? new FileWriter(line.getOptionValue('o'))
                    : new PrintWriter(System.out);

            App app = new App(directory, w);
            app.run();
            w.close();

        } catch (ParseException exp) {
            // oops, something went wrong
            LOG.severe("Parsing failed.  Reason: " + exp.getMessage());
        } catch (IOException e) {
            // An IO execption is critical. Let's just log it
            LOG.log(Level.SEVERE, "File IO error", e);
        }
    }


}
