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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import de.minecrawler.IR1.data.XMLDocument;
import de.minecrawler.IR1.data.XMLDocumentList;

public class XMLTest {

    @Test
    public void testNormalXML() throws JAXBException {

        // Nutzen der Object Factory
        JAXBContext jc = JAXBContext.newInstance("de.minecrawler.IR1.data");
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        // Parsing of XML
        XMLDocumentList sp = (XMLDocumentList) unmarshaller.unmarshal(new File("xml/reut2-000.xml"));
        // Get First document
        XMLDocument r = sp.getDocuments().get(0);

        // Run some tests
        // Check the attribute "TOPICS"
        assertTrue("First document should have topics, but hasn't!", r.hasTopics());

        // Check the element places
        List<String> places = r.getPlaces();
        assertTrue("First document should have the topics [cocoa], but was " + places, places.toString().equals("[el-salvador, usa, uruguay]"));

        // Check the element topics (not the attribute!)
        List<String> topics = r.getTopics();
        assertTrue("First document should have the topics [cocoa], but was " + topics, topics.toString().equals("[cocoa]"));

        // Check the date of the document
        LocalDateTime shouldDate = new LocalDateTime(1987, 2, 26, 15, 1, 1, 790);
        assertTrue("First document shoud have the date " + shouldDate + ", but was " + r.getDate(), shouldDate.equals(r.getDate()));

    }

}
