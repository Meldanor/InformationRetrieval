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

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import de.minecrawler.IR1.data.Document;
import de.minecrawler.IR1.data.DocumentList;

public class XMLTest {

    @Test
    public void testNormalXML() throws JAXBException {

        // Nutzen der Object Factory
        JAXBContext jc = JAXBContext.newInstance("de.minecrawler.IR1.data");
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        // Parsing of XML
        DocumentList sp = (DocumentList) unmarshaller.unmarshal(new File("xml/reut2-000.xml"));
        // Get First document
        Document r = sp.getDocuments().get(0);
        System.out.println(r.hasTopics());
        System.out.println(r.getPlaces());
        System.out.println(r.getTopics());
        System.out.println(r.getUnknown());
        System.out.println(r.getDate());
        System.out.println(r.getText().getBody());
    }

}
