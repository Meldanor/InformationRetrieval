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

package de.minecrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.minecrawler.data.CrawledWebsite;

/**
 * Website crawler based on Jsoup. Uses deep first search recursion strategy for
 * crawling.
 */
public class Crawler {

    private static final int MAX_TIMEOUT = 2000;

    private int maxSearchDepth;
    private URL seed;

    /**
     * Creates a website crawler with an initial url to crawl and the
     * limitation for crawling.
     * 
     * @param maxSearchDepth
     *            The maximum search depth the crawler goes before terminiating
     * @param seed
     *            The initial url the crawler begins at
     */
    public Crawler(int maxSearchDepth, URL seed) {
        this.maxSearchDepth = maxSearchDepth;
        this.seed = seed;
    }

    /**
     * Starts the crawler.
     * 
     * @return List containing crawled websites retrieved from the seed url.
     *         <code>Null</code> if, and only if, an error occured!
     */
    public List<CrawledWebsite> run() {
        try {
            List<CrawledWebsite> websites = new ArrayList<CrawledWebsite>();
            parseWebsite(websites, seed, 0);
            return websites;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The deep first search of the website. Terminates when the maxSearchDepth
     * is reached.
     * 
     * @param websites
     *            List of current crawled websites
     * @param url
     *            The current url to scan
     * @param depth
     *            The current depth of crawling
     * @throws Exception
     *             An error occurred
     */
    private void parseWebsite(List<CrawledWebsite> websites, URL url, int depth) throws Exception {
        if (depth == maxSearchDepth) {
            return;
        }
        Document document;
        try {
            document = Jsoup.parse(url, MAX_TIMEOUT);
        } catch (UnsupportedMimeTypeException e) {
            return;
        } catch (HttpStatusException e) {
            return;
        }

        String title = document.title();
        String body = document.text();

        CrawledWebsite webSite = new CrawledWebsite(body, title, url);
        websites.add(webSite);

        Elements links = document.getElementsByAttribute("href");
        for (Element link : links) {
            String subLink = link.absUrl("href");
            URL newUrl;
            try {
                newUrl = new URL(subLink);
            } catch (MalformedURLException e) {
                return;
            }
            parseWebsite(websites, newUrl, depth + 1);
        }
    }
}
