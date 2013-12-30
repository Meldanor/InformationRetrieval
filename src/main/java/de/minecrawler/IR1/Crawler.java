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

import java.net.URI;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.minecrawler.IR1.data.CrawledWebsite;

public class Crawler {

    private static final int MAX_TIMEOUT = 2000;

    private int maxRecursiveDepth;
    private URI seed;

    private InformationRetrievalSystem infoSystem;

    public Crawler(int maxRecursiveDepth, URI seed, InformationRetrievalSystem infoSystem) {
        this.maxRecursiveDepth = maxRecursiveDepth;
        this.seed = seed;
        this.infoSystem = infoSystem;
    }

    public Crawler(int maxRecursiveDepth, String seedURL, InformationRetrievalSystem infoSystem) {
        this(maxRecursiveDepth, URI.create(seedURL), infoSystem);
    }

    public void run() {
        try {
            parseWebsite(seed.toURL(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseWebsite(URL url, int depth) throws Exception {
        if (depth == maxRecursiveDepth) {
            return;
        }

        Document document = Jsoup.parse(url, MAX_TIMEOUT);

        String title = document.title();
        String text = document.text();
        CrawledWebsite webSite = new CrawledWebsite(text, title, url.toURI());
        this.infoSystem.addWebsite(webSite);

        Elements links = document.getElementsByAttribute("href");
        for (Element link : links) {
            String subLink = link.absUrl("href");
            parseWebsite(new URL(subLink), depth + 1);
        }
    }
}
