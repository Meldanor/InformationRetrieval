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

package de.minecrawler.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import de.minecrawler.data.CrawledWebsite;
import de.minecrawler.data.CrawledWebsiteResult;

public abstract class AbstractSearchEngine {

    public AbstractSearchEngine(Object... args) throws Exception {
        this.dir = createDirectory(args);
    }

    // Fields for the indices
    protected static final String FIELD_TITLE = "title";
    protected static final String FIELD_BODY = "body";
    protected static final String FIELD_URL = "url";

    private static final String[] FIELDS = {FIELD_BODY, FIELD_TITLE};

    protected final static Version LUCENE_VERSION = Version.LUCENE_46;
    protected final static Analyzer ANALYZER = new StandardAnalyzer(LUCENE_VERSION);

    protected Directory dir;

    protected abstract Directory createDirectory(Object... args);

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

            List<CrawledWebsiteResult> result = new ArrayList<CrawledWebsiteResult>();
            for (int i = 0; i < hits.length; ++i) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                CrawledWebsite website = extractWebsite(hitDoc);
                result.add(new CrawledWebsiteResult(website, i + 1, hits[i].score));
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

    protected CrawledWebsite extractWebsite(Document doc) {
        return new CrawledWebsite(doc.get(FIELD_BODY), doc.get(FIELD_TITLE), doc.get(FIELD_URL));
    }

}
