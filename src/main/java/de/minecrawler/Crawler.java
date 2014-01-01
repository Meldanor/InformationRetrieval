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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.minecrawler.data.CrawledWebsite;

public class Crawler {

    private static final int MAX_TIMEOUT = 2000;

    private int maxRecursiveDepth;
    private URI seed;

    public Crawler(int maxRecursiveDepth, URI seed) {
        this.maxRecursiveDepth = maxRecursiveDepth;
        this.seed = seed;
    }

    public Crawler(int maxRecursiveDepth, String seedURL) {
        this(maxRecursiveDepth, URI.create(seedURL));
    }

    public List<CrawledWebsite> run() {
        try {
            List<CrawledWebsite> websites = new ArrayList<CrawledWebsite>();
            parseWebsite(websites, seed.toURL(), 1);
            return websites;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseWebsite(List<CrawledWebsite> websites, URL url, int depth) throws Exception {
        if (depth == maxRecursiveDepth) {
            return;
        }

        Document document = Jsoup.parse(url, MAX_TIMEOUT);

        String title = document.title();
        String text = document.text();

        CrawledWebsite webSite = new CrawledWebsite(text, title, url.toURI());
        websites.add(webSite);

        Elements links = document.getElementsByAttribute("href");
        for (Element link : links) {
            String subLink = link.absUrl("href");
            parseWebsite(websites, new URL(subLink), depth + 1);
        }
    }
}
