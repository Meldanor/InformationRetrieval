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

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Class to provide a user friendly, console based interface.
 */
public class WizardInterface {

    private Scanner scanner;

    /**
     * Starts the wizard interface. Asks the user for the different values and
     * options and executes the query.
     */
    public WizardInterface() {
        scanner = new Scanner(System.in);
//        InputStream source = askSource();
//        String output = askOutput();
//        int limit = askLimit();
//        String query = askQuery();
//
//        startQuery(source, output, limit, query);
        scanner.close();
    }

//    /**
//     * Asks for the data source (the xml formatted data).
//     * 
//     * @return User defined filestream written to the xml file. If no one is defined,
//     *         the default one (reut2-000.xml) will be used.
//     */
//    private InputStream askSource() {
//        System.out.println("File to XML file(enter nothing for standard file in jar)");
//
//        do {
//            String filePath = scanner.nextLine();
//            if (filePath == null || filePath.isEmpty())
//                return this.getClass().getResourceAsStream("/reut2-000.xml");
//            try {
//                return new FileInputStream(filePath);
//            } catch (FileNotFoundException e) {
//                System.out.println("File '" + filePath + "' does not exists! Enter a valid one!");
//
//                e.printStackTrace();
//            }
//        } while (true);
//    }
//
//    /**
//     * Asks the user for a filepath, where the results should be saved.
//     * 
//     * @return User defined filepath, <code>null</code> if the user enters
//     *         nothing. It will be printed on console.
//     */
//    private String askOutput() {
//        System.out.println("Path to output file. Will override if existing. (Enter nothing for console output)");
//        String s = scanner.nextLine();
//        if (s == null || s.isEmpty())
//            return null;
//        return s;
//    }
//
//    /**
//     * Asks the user for the maximum number of results to return.
//     * 
//     * @return The maximum number of results of the search, 10 by default
//     */
//    private int askLimit() {
//        System.out.println("Max number of results to print(Enter nothing for default 10 results)");
//        String limitString = scanner.nextLine();
//        if (limitString == null || limitString.isEmpty())
//            return 10;
//        try {
//            return Integer.parseInt(limitString);
//        } catch (Exception e) {
//            System.out.println("Not a number - use 10 as limit");
//            return 10;
//        }
//    }
//
//    /**
//     * @return The query to be executed
//     */
//    private String askQuery() {
//        System.out.println("Enter your search query");
//        String query = scanner.nextLine();
//        return query;
//    }
//
//    /**
//     * Executes the query on the information retrieval system using the entered
//     * values.
//     * 
//     * @param source
//     *            The stream to read from the xml file
//     * @param output
//     *            Path to the output file, <code>null</code> if to print on
//     *            console
//     * @param limit
//     *            The maximum number of results
//     * @param query
//     *            The query to be executed
//     */
//    private void startQuery(InputStream source, String output, int limit, String query) {
//        System.out.println("Query: " + query);
//        long time = System.nanoTime();
//        InformationRetrievalSystem irSystem = null;
//        try {
//            irSystem = new InformationRetrievalSystem(source);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        ResultXMLDocumentList results = irSystem.search(query, limit);
//        time = System.nanoTime() - time;
//        System.out.println("Results: " + results.size());
//        printTime(time);
//
//        try {
//
//            JAXBContext jc = JAXBContext.newInstance(Core.XML_ENTITY_PACKAGE);
//            Marshaller marshaller = jc.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//            // Print on console
//            if (output == null) {
//                marshaller.marshal(results, System.out);
//            } else {
//                File outputFile = new File(output);
//                marshaller.marshal(results, outputFile);
//                System.out.println("Results are saved in " + outputFile);
//            }
//
//        } catch (Exception e) {
//            System.out.println("Error occured!");
//            e.printStackTrace();
//        }
//    }

    /**
     * Prints the time, formatted as seconds, milliseconds and microseconds, it
     * took to execute the query.
     * 
     * @param time
     *            The time in nano seconds
     */
    private void printTime(long time) {
        long seconds = TimeUnit.NANOSECONDS.toSeconds(time);
        time = time - TimeUnit.SECONDS.toNanos(seconds);

        long millis = TimeUnit.NANOSECONDS.toMillis(time);
        time = time - TimeUnit.MILLISECONDS.toNanos(millis);

        long micros = TimeUnit.NANOSECONDS.toMicros(time);
        time = time - TimeUnit.MICROSECONDS.toNanos(micros);

        System.out.println("Query executed in " + seconds + "s " + millis + "ms " + micros + "micro");
    }
}
