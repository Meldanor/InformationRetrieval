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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.minecrawler.data.CrawledWebsite;
import de.minecrawler.data.CrawledWebsiteResult;

/**
 * Class handling the parsing of the xml document and providing a search method.
 */
public class InformationRetrievalSystem {

    // Fields for the indices
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_BODY = "body";
    private static final String FIELD_URI = "uri";

    private static final String[] FIELDS = {FIELD_BODY, FIELD_TITLE};

    // Only stored but not index type (reference on the uri)
    private static FieldType TYPE_ONLY_STORED = new FieldType();

    static {
        TYPE_ONLY_STORED.setStored(true);
        TYPE_ONLY_STORED.setIndexed(false);
        TYPE_ONLY_STORED.setTokenized(false);
        TYPE_ONLY_STORED.freeze();
    }

    private static Version LUCENE_VERSION = Version.LUCENE_46;
    private static Analyzer ANALYZER = new StandardAnalyzer(LUCENE_VERSION);

    private Directory dir;
    private IndexWriter indexWriter;
    private Map<String, CrawledWebsite> websiteMap;

    public InformationRetrievalSystem() throws Exception {
        createDirectory();
        this.websiteMap = new HashMap<String, CrawledWebsite>();
    }

    private void createDirectory() throws Exception {
        this.dir = new RAMDirectory();

        IndexWriterConfig conf = new IndexWriterConfig(LUCENE_VERSION, ANALYZER);
        this.indexWriter = new IndexWriter(this.dir, conf);
    }

    public void addWebsite(CrawledWebsite website) {
        Document document = new Document();

        try {
            this.websiteMap.put(website.getURI().toString(), website);
            addFields(document, website);
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFields(Document doc, CrawledWebsite website) {
        doc.add(new Field(FIELD_BODY, website.getBody(), TextField.TYPE_STORED));
        doc.add(new Field(FIELD_TITLE, website.getText(), TextField.TYPE_STORED));
        doc.add(new Field(FIELD_URI, website.getURI().toString(), TYPE_ONLY_STORED));
    }

    /**
     * Starts a search on the parsed documents using a search query. The default
     * maximum number of results is 10.
     * 
     * @param queryString
     *            The query string <a href=
     *            "http://lucene.apache.org/core/4_1_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html"
     *            >Query Format</a>
     * 
     * @return List of results
     */
    public List<CrawledWebsiteResult> search(String queryString) {
        return search(queryString, 10);
    }

    /**
     * Starts a search on the parsed documents using a search query.
     * 
     * @param queryString
     *            The query string <a href=
     *            "http://lucene.apache.org/core/4_1_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html"
     *            >Query Format</a>
     * @param limit
     *            The maximum numer of results
     * @return List of results
     */
    public List<CrawledWebsiteResult> search(String queryString, int limit) {
        try {
            DirectoryReader ireader = DirectoryReader.open(this.dir);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            QueryParser parser = new MultiFieldQueryParser(LUCENE_VERSION, FIELDS, ANALYZER);
            Query query = parser.parse(queryString);
            ScoreDoc[] hits = isearcher.search(query, null, limit).scoreDocs;
            if (hits.length == 0)
                return Collections.<CrawledWebsiteResult> emptyList();

            List<CrawledWebsiteResult> result = new ArrayList<CrawledWebsiteResult>();
            for (int i = 0; i < hits.length; ++i) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                CrawledWebsite webSite = websiteMap.get(hitDoc.get(FIELD_URI));
                result.add(new CrawledWebsiteResult(webSite, i + 1, hits[i].score));
            }

            ireader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.<CrawledWebsiteResult> emptyList();
        } catch (ParseException e) {
            System.out.println("Wrong query! Check your query format!");
            System.out.println(e.getMessage());
            return Collections.<CrawledWebsiteResult> emptyList();
        }
    }
}
