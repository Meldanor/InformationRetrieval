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

package de.minecrawler.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Class to provide a user friendly, console based interface.
 */
public class WizardUI extends AbstractUI {

    private Scanner scanner;

    /**
     * Starts the wizard interface. Asks the user for the different values and
     * options and executes the query.
     */
    public WizardUI() {
        scanner = new Scanner(System.in);
        URL seed = askSeed();
        int depth = askDepth();
        boolean printOnConsole = askConsole();
        int resultLimit = askResultLimit();
        boolean forceCrawling = askEnforceCrawling();
        String query = askQuery();

        scanner.close();
        startSearch(seed, depth, printOnConsole, resultLimit, forceCrawling, query);
    }

    /**
     * Ask for a seed URL where the crawling starts.
     * 
     * @return An URL where the crawler begins.
     */
    private URL askSeed() {
        System.out.println("URL to start the website crawling");

        while (true) {
            try {
                URL url = new URL(scanner.nextLine());
                return url;
            } catch (MalformedURLException e) {
                System.out.println("This is not a valid url! Please enter a nother one!");
            }
        }
    }

    /**
     * Asks the user whether to print the results on the console or write them
     * into a file.
     * 
     * @return <code>True</code> only , and only if, when the users answers with
     *         Y or y.
     */
    private boolean askConsole() {
        System.out.println("Shall the results printed on the console (Y) or written to a file (N)?");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Y");
    }

    /**
     * Ask the user how many results are maximal returned
     * 
     * @return Result limit
     */
    private int askResultLimit() {
        System.out.println("Max number of results to print(Enter nothing for default 10 results)");
        String limitString = scanner.nextLine();
        if (limitString == null || limitString.isEmpty())
            return 10;
        try {
            return Integer.parseInt(limitString);
        } catch (Exception e) {
            System.out.println("Not a number - use 10 as limit");
            return 10;
        }
    }

    /**
     * Ask the user about the max depth of websites to crawl (the recursion
     * depth)
     * 
     * @return Recursion depth of crawling, 5 by default
     */
    private int askDepth() {
        System.out.println("Max depth of websites to crawl(Enter nothing for default 5)");
        String limitString = scanner.nextLine();
        if (limitString == null || limitString.isEmpty())
            return 5;
        try {
            return Integer.parseInt(limitString);
        } catch (Exception e) {
            System.out.println("Not a number - use 10 as limit");
            return 5;
        }
    }

    /**
     * @return The query to be executed
     */
    private String askQuery() {
        System.out.println("Enter your search query");
        String query = scanner.nextLine();
        return query;
    }

    /**
     * Asks the user to ignore possible cache and always start the website
     * crawler or use the cache if this is possible.
     * 
     * @return <code>true</code> If, and only if, the user answers with Y.
     *         Otherwise <code>false</code>
     */
    private boolean askEnforceCrawling() {
        System.out.println("Force website crawling and ignoring possible cache (Y) or use cache if possible (N)?");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Y");
    }

}
