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

package de.minecrawler.xml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.minecrawler.IR1.data.XMLDocument;
import de.minecrawler.IR1.data.XMLDocumentList;

public class QueryTest {

    private static XMLDocumentList list;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Nutzen der Object Factory
        JAXBContext jc = JAXBContext.newInstance("de.minecrawler.IR1.data");
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        // Parsing of XML
        list = (XMLDocumentList) unmarshaller.unmarshal(new File("xml/reut2-000.xml"));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);

        Directory dir = new RAMDirectory();
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_45, analyzer);
        IndexWriter iWriter = new IndexWriter(dir, conf);
        for (XMLDocument xmlDocument : list) {
            Document doc = new Document();
            doc.add(new Field("id", xmlDocument.getNewid().toString(), TextField.TYPE_STORED));
            if (xmlDocument.getDate() != null)
                doc.add(new Field("date", DateTools.dateToString(xmlDocument.getDate().toDate(), Resolution.SECOND), TextField.TYPE_STORED));
            if (xmlDocument.getText().getTitle() != null)
                doc.add(new Field("title", xmlDocument.getText().getTitle(), TextField.TYPE_STORED));
            if (xmlDocument.getText().getBody() != null)
                doc.add(new Field("body", xmlDocument.getText().getBody(), TextField.TYPE_STORED));
            iWriter.addDocument(doc);
        }
        iWriter.close();

        DirectoryReader ireader = DirectoryReader.open(dir);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser(Version.LUCENE_45, "title", analyzer);
        Query query = parser.parse("date:[19870226 TO 19870227]");
        ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
        if (hits.length == 0)
            System.out.println("No results");
        assertTrue("Hits should be 229, but was, " + hits.length, hits.length == 229);
        // Iterate through the results:
//        for (int i = 0; i < hits.length; i++) {
//            Document hitDoc = isearcher.doc(hits[i].doc);
//            System.out.println(hitDoc.get("id"));
////          assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
//        }
        ireader.close();
        dir.close();
    }
}
