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

package de.minecrawler.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import de.minecrawler.data.CrawledWebsite;

/**
 * Class handling the parsing of the xml document and providing a search method.
 */
public class LiveSearchEngine extends AbstractSearchEngine {

    // Only stored but not index type (reference on the uri)
    private static FieldType TYPE_ONLY_STORED = new FieldType();

    static {
        TYPE_ONLY_STORED.setStored(true);
        TYPE_ONLY_STORED.setIndexed(false);
        TYPE_ONLY_STORED.setTokenized(false);
        TYPE_ONLY_STORED.freeze();
    }

    private IndexWriter indexWriter;

    public LiveSearchEngine() throws Exception {
        super();
    }

    @Override
    protected Directory createDirectory(Object... args) {
        return new RAMDirectory();
    }

    public void addWebsite(CrawledWebsite website) {
        Document document = new Document();

        try {
            addFields(document, website);
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWebsites(List<CrawledWebsite> websites) {
        for (CrawledWebsite website : websites) {
            addWebsite(website);
        }
    }
    
    private void addFields(Document doc, CrawledWebsite website) {
        doc.add(new Field(FIELD_BODY, website.getBody(), TextField.TYPE_STORED));
        doc.add(new Field(FIELD_TITLE, website.getText(), TextField.TYPE_STORED));
        doc.add(new Field(FIELD_URI, website.getURI().toString(), TYPE_ONLY_STORED));
    }
}
