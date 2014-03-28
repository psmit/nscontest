package eu.petersmit.nscontest;


import org.apache.commons.cli.*;

import java.io.*;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {

    private final static Logger LOG = Logger.getLogger("test");

    private String input_directory;
    private Writer out_writer;

    public App(String input_directory, Writer out_writer) {
        this.input_directory = input_directory;
        this.out_writer = out_writer;
    }

    public void run() throws IOException {

        out_writer.write("Hello");
    }

    public static void main( String[] args )
    {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("d", "input-directory", true, "directory to find the input csv files. Default: Current working directory");
        options.addOption("o", "output", true, "output-file. If not given or '-', output to terminal (std out)");

        CommandLineParser parser = new BasicParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("nscontest", options);
                return;
            }

            String directory = ".";
            if (line.hasOption('d')) directory = line.getOptionValue('d');

            Writer w = new PrintWriter(System.out);
            if (line.hasOption('o') && !line.getOptionValue('o').equals("-")) {
                w = new FileWriter(line.getOptionValue('o'));
            }

            App app = new App(directory, w);
            app.run();

        }
        catch( ParseException exp ) {
            // oops, something went wrong
            LOG.severe("Parsing failed.  Reason: " + exp.getMessage());
        } catch (IOException e) {
            LOG.severe("File IO error.  Reason: " + e.getMessage());
        }
    }


}
