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

package de.minecrawler.data;

import java.net.MalformedURLException;
import java.net.URL;

import de.minecrawler.Crawler;

/**
 * Container class containing information about a crawled website from the
 * {@link Crawler}
 */
public class CrawledWebsite {

    private String body;
    private String title;
    private URL url;

    /**
     * @param body
     *            The readable text of the website
     * @param title
     *            The title of the website
     * @param url
     *            The URL of the website
     */
    public CrawledWebsite(String body, String title, URL url) {
        this.body = body;
        this.title = title;
        this.url = url;
    }

    /**
     * @param body
     *            The readable text of the website
     * @param title
     *            The title of the website
     * @param url
     *            String encoded URL of the website. Must be well formed
     *            otherwise an error occures!
     */
    public CrawledWebsite(String body, String title, String url) {
        this.body = body;
        this.title = title;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The readable text of the website
     */
    public String getBody() {
        return body;
    }

    /**
     * @return The title of the website
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The URL of the website
     */
    public URL getURL() {
        return url;
    }
}
