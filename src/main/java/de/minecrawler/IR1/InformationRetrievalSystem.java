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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.minecrawler.IR1.data.XMLDocument;
import de.minecrawler.IR1.data.XMLDocumentList;
import de.minecrawler.IR1.data.queryresult.ResultXMLDocumentList;

/**
 * Class handling the parsing of the xml document and providing a search method.
 */
public class InformationRetrievalSystem {

    // Fields for the indices
    private static final String FIELD_BODY = "body";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_ID = "id";

    private static final String[] FIELDS = {FIELD_BODY, FIELD_TITLE, FIELD_DATE};

    // Parsed xml documents to start the search on
    private List<XMLDocument> xmlDocuments;
    private Map<BigInteger, XMLDocument> xmlDocumentMap;

    private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
    private Directory dir;

    /**
     * Creates an information retrieval system based on the data of the file.
     * The file must be parseable.
     * 
     * @param xmlFile
     *            XML File to be parsed
     * @throws Exception
     */
    public InformationRetrievalSystem(File xmlFile) throws Exception {
        this(new FileInputStream(xmlFile));
    }

    /**
     * Creates an information retrieval system based on the data of the
     * stream. The stream must provide xml based text data.
     * 
     * @param stream
     *            The stream providing xml based text data
     * @throws Exception
     */
    public InformationRetrievalSystem(InputStream stream) throws Exception {
        this.xmlDocumentMap = new HashMap<BigInteger, XMLDocument>();

        init(stream);
    }

    /**
     * Parses the stream and creates the POJO from it. Based on the parsed
     * documents, the index for searching will be created.
     * 
     * @param source
     *            Stream providing xml based text data
     * @throws Exception
     */
    private void init(InputStream source) throws Exception {
        xmlDocuments = loadXML(source);
        createIndex(xmlDocuments);
    }

    /**
     * Parses the XML data using JAXB and the classes {@link XMLDocument} and
     * {@link XMLDocumentList}
     * 
     * @param source
     *            Stream providing xml based text data
     * @return List of parsed documents
     * @throws JAXBException
     * @throws Exception
     */
    private List<XMLDocument> loadXML(InputStream source) throws JAXBException, Exception {
        // Using the Object Factory
        JAXBContext jc = JAXBContext.newInstance(Core.XML_ENTITY_PACKAGE);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        // Parsing the XML
        XMLDocumentList sp = (XMLDocumentList) unmarshaller.unmarshal(source);
        if (sp == null || sp.getDocuments().isEmpty()) {
            throw new Exception("No documents found");
        }
        return sp.getDocuments();
    }

    /**
     * Creates indices for the documents for the information retrieval system.
     * 
     * @param list
     *            The document collection
     * @throws IOException
     */
    private void createIndex(List<XMLDocument> list) throws IOException {

        this.dir = new RAMDirectory();
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_45, analyzer);
        IndexWriter iWriter = new IndexWriter(this.dir, conf);

        for (XMLDocument xmlDocument : list) {
            Document doc = new Document();
            addFields(doc, xmlDocument);
            iWriter.addDocument(doc);

            xmlDocumentMap.put(xmlDocument.getNewid(), xmlDocument);
        }
        iWriter.close();
    }

    /**
     * Adds interesting information to the index fields from the xml document to the
     * information retrieval documents. <br>
     * The fields are: <br>
     * ID <br>
     * DATE <br>
     * TITLE <br>
     * BODY <br>
     * 
     * @param doc
     *            The document to add fields to
     * @param xmlDocument
     *            The xml document to extract the fields
     */
    private void addFields(Document doc, XMLDocument xmlDocument) {
        doc.add(new Field(FIELD_ID, xmlDocument.getNewid().toString(), TextField.TYPE_STORED));

        if (xmlDocument.getDate() != null)
            doc.add(new Field(FIELD_DATE, DateTools.dateToString(xmlDocument.getDate().toDate(), Resolution.SECOND), TextField.TYPE_STORED));
        if (xmlDocument.getText().getTitle() != null)
            doc.add(new Field(FIELD_TITLE, xmlDocument.getText().getTitle(), TextField.TYPE_STORED));
        if (xmlDocument.getText().getBody() != null)
            doc.add(new Field(FIELD_BODY, xmlDocument.getText().getBody(), TextField.TYPE_STORED));
    }

    /**
     * Starts a search on the parsed documents using a search query. The default maximum number of results
     * is 10.
     * 
     * @param queryString
     *            The query string <a href=
     *            "http://lucene.apache.org/core/4_1_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html"
     *            >Query Format</a>
     * 
     * @return List of results
     */
    public ResultXMLDocumentList search(String queryString) {
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
    public ResultXMLDocumentList search(String queryString, int limit) {
        try {
            DirectoryReader ireader = DirectoryReader.open(this.dir);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_45, FIELDS, analyzer);
            Query query = parser.parse(queryString);
            ScoreDoc[] hits = isearcher.search(query, null, limit).scoreDocs;
            if (hits.length == 0)
                return new ResultXMLDocumentList();

            ResultXMLDocumentList result = new ResultXMLDocumentList();
            for (int i = 0; i < hits.length; ++i) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                XMLDocument xmlDoc = xmlDocumentMap.get(new BigInteger(hitDoc.get(FIELD_ID)));
                result.addResult(xmlDoc, (i + 1), hits[i].score);
            }

            ireader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultXMLDocumentList();
        } catch (ParseException e) {
            System.out.println("Wrong query! Check your query format!");
            System.out.println(e.getMessage());
            return new ResultXMLDocumentList();
        }
    }
}
