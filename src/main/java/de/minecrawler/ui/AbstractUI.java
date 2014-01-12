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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.minecrawler.IRSystem;
import de.minecrawler.data.CrawledWebsiteResult;

/**
 * General class to provide several methods for searching, using retrieved
 * parameters.
 */
public abstract class AbstractUI {

    protected AbstractUI() {

    }

    /**
     * Use a {@link IRSystem} to run a search
     * 
     * @param seed
     *            The initial url seed
     * @param maxDepth
     *            The maximum crawl depth
     * @param printOnConsole
     *            Should the result be printed on the console or to a new created
     *            file
     * @param resultLimit
     *            How many results shall printed
     * @param forceCrawling
     *            Enforce a crawl(ignoring cache)
     * @param query
     *            * The query itself, see <a href=
     *            "http://lucene.apache.org/core/4_1_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html"
     *            >Query Format</a>
     */
    protected void startSearch(URL seed, int maxDepth, boolean printOnConsole, int resultLimit, boolean forceCrawling, String query) {
        long time = System.nanoTime();
        IRSystem irSystem;
        try {
            irSystem = new IRSystem(seed, maxDepth, forceCrawling);
        } catch (Exception e) {
            System.err.println("Error while executing the search!");
            e.printStackTrace();
            return;
        }

        System.out.println("Max Depth: " + maxDepth);
        System.out.println("Query: " + query);
        System.out.println();

        List<CrawledWebsiteResult> results = irSystem.search(query, resultLimit);
        time = System.nanoTime() - time;

        showResults(printOnConsole, results, time);
    }

    /**
     * Shows the results.
     * 
     * @param showInConsole
     *            Show the output on the console<br>
     *            If <code>false</code> the output will be written into a file
     * @param result
     *            The result of the query
     * @param time
     *            The time the query needs to be executed
     */
    private void showResults(boolean showInConsole, List<CrawledWebsiteResult> results, long time) {
        System.out.println("Results: " + results.size());
        System.out.println(printTime(time));
        System.out.println();

        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sBuilder = new StringBuilder();
        for (CrawledWebsiteResult result : results) {
            sBuilder.append("Rank: ").append(result.getRank()).append(lineSeparator);
            sBuilder.append("Score: ").append(result.getScore()).append(lineSeparator);
            sBuilder.append("URL: ").append(result.getWebsite().getURL().toString()).append(lineSeparator);
            sBuilder.append("Title: ").append(result.getWebsite().getTitle()).append(lineSeparator);
            sBuilder.append("Text: ").append(result.getWebsite().getBody()).append(lineSeparator);
            sBuilder.append(lineSeparator);
        }

        if (showInConsole) {
            System.out.print(sBuilder.toString());
        } else {
            String fileName = writeToFile(sBuilder.toString());
            System.out.println("Resuls were written to " + fileName);
        }
    }

    /**
     * Formats the execution time to a readable format with seconds, milliseconds
     * and microseconds.
     * 
     * @param time
     *            The execution time
     */
    private String printTime(long time) {
        long seconds = TimeUnit.NANOSECONDS.toSeconds(time);
        time = time - TimeUnit.SECONDS.toNanos(seconds);

        long millis = TimeUnit.NANOSECONDS.toMillis(time);
        time = time - TimeUnit.MILLISECONDS.toNanos(millis);

        long micros = TimeUnit.NANOSECONDS.toMicros(time);
        time = time - TimeUnit.MICROSECONDS.toNanos(micros);

        return "Query executed in " + seconds + "s " + millis + "ms " + micros + "micro";
    }

    /**
     * Writes the formatted result text into a file.
     * 
     * @param text
     *            Formatted result
     * @return The file name
     */
    private String writeToFile(String text) {
        try {
            File f = createResultFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(text);
            writer.close();

            return f.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return A not used file as output
     */
    private File createResultFile() {
        for (int i = 0;; ++i) {
            File f = new File("result" + i + ".txt");
            if (!f.exists())
                return f;
        }
    }
}
