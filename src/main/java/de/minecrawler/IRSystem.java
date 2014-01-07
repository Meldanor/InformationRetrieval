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

public class IRSystem {

    private CacheManager cacheManager;
    private AbstractSearchEngine searchEngine;

    public IRSystem(URL seed, int deepth, boolean forceCrawling) throws Exception {
        cacheManager = new CacheManager();
        File cacheFile = cacheManager.getCache(seed, deepth);
        if (cacheFile == null) {
            System.out.println("Use website crawler - this will take a moment!");
            useNotCachedIndex(seed, deepth);
        } else if (forceCrawling) {
            System.out.println("Enforce website crawling and delete old cache");
            cacheManager.removeCache(seed, deepth);
            useNotCachedIndex(seed, deepth);
        } else {
            System.out.println("Use cached websites (Websites are not older than an hour!)");
            useCachedIndex(cacheFile);
        }
    }

    private void useNotCachedIndex(URL seed, int deepth) throws Exception {
        System.out.println("Started crawling the websites...");
        this.searchEngine = new LiveSearchEngine(cacheManager.addURL(seed, deepth));
        Crawler crawler = new Crawler(deepth, seed);
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

    private void useCachedIndex(File cacheFile) throws Exception {
        System.out.println("Loading cache...");
        this.searchEngine = new CachedSearchEngine(cacheFile);
        System.out.println("Finished loading!");
        System.out.println();
    }

    public List<CrawledWebsiteResult> search(String query) {
        return search(query, 10);
    }

    public List<CrawledWebsiteResult> search(String query, int resultLimit) {
        return searchEngine.search(query, resultLimit);
    }
}
