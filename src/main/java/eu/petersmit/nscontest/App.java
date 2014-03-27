package eu.petersmit.nscontest;


import org.apache.commons.cli.*;

import java.io.*;

/**
 * Hello world!
 *
 */
public class App 
{
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

        }
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
