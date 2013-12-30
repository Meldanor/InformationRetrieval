/*
 * Copyright (C) 2013
 * 
 * This file is part of InformationRetrieval.
 * 
 * InformationRetrieval is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * InformationRetrieval is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with InformationRetrieval.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minecrawler.IR1;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Handler for using console arguments to start the information retrieval.
 * 
 * @author Meldanor
 * 
 */
public class ArgumentInterface extends AbstractUI {

    /**
     * Possible argument options
     */
    private Options options;

    /**
     * Starts the interface using the console arguments.
     * 
     * @param args
     *            The arguments from the console
     */
    public ArgumentInterface(String[] args) {
        this.options = createOptions();
        handleArguments(args);
    }

    /**
     * Creates possible options to parse.
     * 
     * @return Container with all information about possible options
     */
    private Options createOptions() {
        Options options = new Options();

        // Option -f or --file defining the xml file to parse
        options.addOption("u", "url", true, "The seed url");
        // Option -c or --console says, that the output will be presented on the
        // console instead of creating a result file.
        options.addOption("c", "console", false, "Query output is in the console");

        options.addOption("d", "depth", true, "Max recursivce deepth to crawl");

        return options;
    }

    private void handleArguments(String[] args) {
        // Create Possix compatible arguments parser
        CommandLineParser parser = new PosixParser();
        // If no other file is defined, use the standard file
        URL seed = null;
        // InputStream xmlStream =
        // getClass().getResourceAsStream("/reut2-000.xml");
        // Shows the query output on the console
        boolean showInConsole = false;

        int depth = 5;

        try {
            CommandLine line = parser.parse(options, args);
            if (!line.hasOption("url")) {
                System.out.println("You have to define a seed url!");
                return;
            }

            seed = new URL(line.getOptionValue("url"));
            System.out.println("SeedUrL = " + seed);

            if (line.hasOption("depth")) {
                String tmp = line.getOptionValue("depth");
                try {
                    depth = Integer.parseInt(tmp);
                } catch (Exception e) {
                    System.out.println(tmp + " is not a number!");
                }
            }

            // Shows the query output on the console
            showInConsole = line.hasOption("console");

            // Non parsed arguments are the query
            args = line.getArgs();
            String query = buildQuery(args);

            startCrawler(seed, depth, showInConsole, query);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("", options);
            return;
        } catch (MalformedURLException e) {
            System.out.println("Wrong url format!");
            return;
        }

    }

    /**
     * Connects all non parsed arguments from the console to one single string,
     * the query.
     * 
     * @param args
     *            The non parsed arguments
     * @return One string containing all other strings separated by a whitespace
     */
    private String buildQuery(String[] args) {
        StringBuilder sBuilder = new StringBuilder();
        int i = 0;
        for (; i < args.length - 1; ++i) {
            sBuilder.append(args[i]);
            sBuilder.append(' ');
        }
        sBuilder.append(args[i]);

        return sBuilder.toString();
    }

}
