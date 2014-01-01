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

import java.net.URI;

public class CrawledWebsite {

    private String body;
    private String title;
    private URI uri;

    public CrawledWebsite(String body, String title, URI uri) {
        this.body = body;
        this.title = title;
        this.uri = uri;
    }

    public CrawledWebsite(String body, String text, String uri) {
        this(body, text, URI.create(uri));
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public URI getURI() {
        return uri;
    }
}
