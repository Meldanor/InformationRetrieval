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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import de.minecrawler.data.CrawledWebsite;
import de.minecrawler.data.CrawledWebsiteResult;

/**
 * Search engine provides methodes to add documents to the index.
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

    /**
     * Creates a search engine with a file to write the index in.
     * 
     * @param toCacheFile
     *            Empty file, will contain after the indexing the index itself.
     *            Used later by {@link CachedSearchEngine}
     * @throws Exception
     */
    public LiveSearchEngine(File toCacheFile) throws Exception {
        super(toCacheFile);
        this.indexWriter = new IndexWriter(this.dir, new IndexWriterConfig(LUCENE_VERSION, ANALYZER));
    }

    @Override
    protected Directory createDirectory(Object... args) {
        try {
            return new NIOFSDirectory((File) args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a single crawled website to the index. All attributes are index (not
     * the url).
     * 
     * @param website
     *            The crawled website
     */
    public void addWebsite(CrawledWebsite website) {
        Document document = new Document();

        try {
            addFields(document, website);
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a collection of websites to the index. See
     * {@link #addWebsite(CrawledWebsite)}.
     * 
     * @param websites
     *            The crawled websites
     */
    public void addWebsites(List<CrawledWebsite> websites) {
        for (CrawledWebsite website : websites) {
            addWebsite(website);
        }
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see de.minecrawler.search.AbstractSearchEngine#search(java.lang.String)
     */
    public List<CrawledWebsiteResult> search(String queryString) {
        // Close index writer before the search starts
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return super.search(queryString);
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see de.minecrawler.search.AbstractSearchEngine#search(java.lang.String,
     * int)
     */
    public List<CrawledWebsiteResult> search(String queryString, int limit) {
        // Close index writer before the search starts
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return super.search(queryString, limit);
    }

    /**
     * Adds the attributes of the website to the document fields
     * 
     * @param doc
     *            The document for the index
     * @param website
     *            The crawled website attributes are used from
     */
    private void addFields(Document doc, CrawledWebsite website) {
        doc.add(new Field(FIELD_BODY, website.getBody(), TextField.TYPE_STORED));
        doc.add(new Field(FIELD_TITLE, website.getTitle(), TextField.TYPE_STORED));
        doc.add(new Field(FIELD_URL, website.getURL().toString(), TYPE_ONLY_STORED));
    }
}
