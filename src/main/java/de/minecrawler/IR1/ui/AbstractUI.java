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

package de.minecrawler.IR1.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.minecrawler.IR1.Crawler;
import de.minecrawler.IR1.InformationRetrievalSystem;
import de.minecrawler.IR1.data.CrawledWebsiteResult;

public abstract class AbstractUI {

    protected AbstractUI() {

    }

    protected void startCrawler(URL seed, int maxDepth, boolean printOnConsole, String query) {
        InformationRetrievalSystem irSystem = null;
        long time = System.nanoTime();
        try {
            irSystem = new InformationRetrievalSystem();
            Crawler crawler = new Crawler(maxDepth, seed.toURI(), irSystem);
            crawler.run();
        } catch (Exception e) {
            System.out.println("Error while parsing the XML file");
            e.printStackTrace();
        }
        time = System.nanoTime() - time;
        System.out.println("Max Depth: " + maxDepth);
        System.out.println("Query: " + query);
        List<CrawledWebsiteResult> results = irSystem.search(query);
        showResults(printOnConsole, results, time);
    }

    /**
     * Shows the results
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
        printTime(time);

        String lineSeparator = System.getProperty("line.separator");
        StringBuilder sBuilder = new StringBuilder();
        for (CrawledWebsiteResult result : results) {
            sBuilder.append("Rank: ").append(result.getRank()).append(lineSeparator);
            sBuilder.append("Score: ").append(result.getScore()).append(lineSeparator);
            sBuilder.append("Title: ").append(result.getWebsite().getText()).append(lineSeparator);
            sBuilder.append("Text: ").append(result.getWebsite().getBody()).append(lineSeparator);
            sBuilder.append(lineSeparator);
        }

        if (showInConsole) {
            System.out.println(sBuilder.toString());
        } else {
            String fileName = writeToFile(lineSeparator);
            System.out.println("Resuls were written to " + fileName);
        }
    }

    private void printTime(long time) {
        long seconds = TimeUnit.NANOSECONDS.toSeconds(time);
        time = time - TimeUnit.SECONDS.toNanos(seconds);

        long millis = TimeUnit.NANOSECONDS.toMillis(time);
        time = time - TimeUnit.MILLISECONDS.toNanos(millis);

        long micros = TimeUnit.NANOSECONDS.toMicros(time);
        time = time - TimeUnit.MICROSECONDS.toNanos(micros);

        System.out.println("Query executed in " + seconds + "s " + millis + "ms " + micros + "micro");
    }

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
            File f = new File("result" + i + ".xml");
            if (!f.exists())
                return f;
        }
    }
}
