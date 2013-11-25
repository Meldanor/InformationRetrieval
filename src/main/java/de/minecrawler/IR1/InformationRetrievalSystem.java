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

public class InformationRetrievalSystem {

    private static final String FIELD_BODY = "body";
    private static final String FIELD_TOPIC = "title";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_ID = "id";

    private static final String[] FIELDS = {FIELD_BODY, FIELD_TOPIC, FIELD_DATE};

    private List<XMLDocument> xmlDocuments;
    private Map<BigInteger, XMLDocument> xmlDocumentMap;

    private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
    private Directory dir;

    public InformationRetrievalSystem(File xmlFile) throws Exception {
        this(new FileInputStream(xmlFile));
    }

    public InformationRetrievalSystem(InputStream stream) throws Exception {
        this.xmlDocumentMap = new HashMap<BigInteger, XMLDocument>();

        init(stream);
    }

    private void init(InputStream source) throws Exception {
        xmlDocuments = loadXML(source);
        createIndex(xmlDocuments);
    }

    private List<XMLDocument> loadXML(InputStream source) throws JAXBException, Exception {
        // Nutzen der Object Factory
        JAXBContext jc = JAXBContext.newInstance(Core.XML_ENTITY_PACKAGE);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        // Parsing of XML
        XMLDocumentList sp = (XMLDocumentList) unmarshaller.unmarshal(source);
        if (sp == null || sp.getDocuments().isEmpty()) {
            throw new Exception("No documents found");
        }
        return sp.getDocuments();
    }

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

    private void addFields(Document doc, XMLDocument xmlDocument) {
        doc.add(new Field(FIELD_ID, xmlDocument.getNewid().toString(), TextField.TYPE_STORED));

        if (xmlDocument.getDate() != null)
            doc.add(new Field(FIELD_DATE, DateTools.dateToString(xmlDocument.getDate().toDate(), Resolution.SECOND), TextField.TYPE_STORED));
        if (xmlDocument.getText().getTitle() != null)
            doc.add(new Field(FIELD_TOPIC, xmlDocument.getText().getTitle(), TextField.TYPE_STORED));
        if (xmlDocument.getText().getBody() != null)
            doc.add(new Field(FIELD_BODY, xmlDocument.getText().getBody(), TextField.TYPE_STORED));
    }

    public ResultXMLDocumentList search(String queryString) {
        try {
            DirectoryReader ireader = DirectoryReader.open(this.dir);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_45, FIELDS, analyzer);
            Query query = parser.parse(queryString);
            ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
            if (hits.length == 0)
                return new ResultXMLDocumentList();

            ResultXMLDocumentList result = new ResultXMLDocumentList();
//            List<XMLDocument> result = new ArrayList<XMLDocument>(hits.length);
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
            // TODO: Use logger
            System.out.println("Wrong query! Check your query format!");
            System.out.println(e.getMessage());
            return new ResultXMLDocumentList();
        }
    }
}
