/*
 * Copyright (C) 2014
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

import java.io.File;
import java.net.URL;
import java.util.List;

import de.minecrawler.cache.CacheManager;
import de.minecrawler.data.CrawledWebsite;
import de.minecrawler.data.CrawledWebsiteResult;
import de.minecrawler.search.AbstractSearchEngine;
import de.minecrawler.search.CachedSearchEngine;
import de.minecrawler.search.LiveSearchEngine;

/**
 * An IR system using a cache manager, a website crawler and an search engine to
 * run a search on the indexed data
 */
public class IRSystem {

    private CacheManager cacheManager;
    private AbstractSearchEngine searchEngine;

    /**
     * Create an IRSystem using a seed and depth of crawling. The IRSystem will
     * select a cache-based search(fast) or a non-cache-based search(slow)
     * depending on cache existing for the search parameters(url and seed) and
     * if crawling is enforced by the user.
     * 
     * @param seed
     *            The initial url to start the crawling on
     * @param depth
     *            The maximum search depth the crawler goes
     * @param forceCrawling
     *            Delete possible cache and start crawler.
     * @throws Exception
     *             An error occurede
     */
    public IRSystem(URL seed, int depth, boolean forceCrawling) throws Exception {
        cacheManager = new CacheManager();
        File cacheFile = cacheManager.getCache(seed, depth);
        if (cacheFile == null) {
            System.out.println("Use website crawler - this will take a moment!");
            useNotCachedIndex(seed, depth);
        } else if (forceCrawling) {
            System.out.println("Enforce website crawling and delete old cache");
            cacheManager.removeCache(seed, depth);
            useNotCachedIndex(seed, depth);
        } else {
            System.out.println("Use cached websites (Websites are not older than an hour!)");
            useCachedIndex(cacheFile);
        }
    }

    /**
     * Use the crawler to index the websites. This will take very long depending
     * on your network connection and the webserver.
     * 
     * @param seed
     *            The initial url the crawler starts
     * @param depth
     *            The maxmium search depth the crawler goes
     * @throws Exception
     *             An error occured
     */
    private void useNotCachedIndex(URL seed, int depth) throws Exception {
        System.out.println("Started crawling the websites...");
        this.searchEngine = new LiveSearchEngine(cacheManager.addURL(seed, depth));
        Crawler crawler = new Crawler(depth, seed);
        List<CrawledWebsite> result = crawler.run();
        if (result == null) {
            System.err.println("Something bad happend!");
            return;
        }
        System.out.println("Sites crawled: " + result.size());
        System.out.println("Finished crawling!");
        System.out.println();

        System.out.println("Start indexing  and creating cache...");
        ((LiveSearchEngine) searchEngine).addWebsites(result);
        System.out.println("Finished indexing!");
        System.out.println();
    }

    /**
     * Use a cache based search engine and do NOT crawl the website. Very fast!
     * 
     * @param cacheFile
     *            The file to the cache
     * @throws Exception
     *             An error occured while loading the cache
     */
    private void useCachedIndex(File cacheFile) throws Exception {
        System.out.println("Loading cache...");
        this.searchEngine = new CachedSearchEngine(cacheFile);
        System.out.println("Finished loading!");
        System.out.println();
    }

    /**
     * Runs a query on the indexed data using a result limit of 10.
     * 
     * @param query
     *            The query itself, see <a href=
     *            "http://lucene.apache.org/core/4_1_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html"
     *            >Query Format</a>
     * @return List containing the results of the search
     */
    public List<CrawledWebsiteResult> search(String query) {
        return search(query, 10);
    }

    /**
     * Runs a query on the indexed data.
     * 
     * @param query
     *            The query itself, see <a href=
     *            "http://lucene.apache.org/core/4_1_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html"
     *            >Query Format</a>
     * @param resultLimit
     *            The maximum number of results to retrieve
     * @return List containing the results of the search
     */
    public List<CrawledWebsiteResult> search(String query, int resultLimit) {
        return searchEngine.search(query, resultLimit);
    }
}
