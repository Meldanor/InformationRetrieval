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

import org.junit.BeforeClass;
import org.junit.Test;

import de.minecrawler.IR1.InformationRetrievalSystem;
import de.minecrawler.IR1.data.XMLDocument;

public class InformationRetrievalTest {

    private static InformationRetrievalSystem IRsystem;

    private final static File XML_FILE = new File("xml/reut2-000.xml");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        IRsystem = new InformationRetrievalSystem(XML_FILE);
    }

    @Test
    public void testNormalQuery() {
        String query = "Tokyo";
        List<XMLDocument> result = IRsystem.search(query);
        assertTrue("Result for query '" + query + "' was null, but shouldn't!", result.size() > 0);
    }

    @Test
    public void testTitleOnlyQuery() {
        String query = "title:\"Tokyo\"";
        List<XMLDocument> result = IRsystem.search(query);
        assertTrue("Result for query '" + query + "' was null, but shouldn't!", result.size() > 0);
    }

    @Test
    public void testDateIntervalQuery() {
        String query = "date:[19870226 TO 19870227]";
        List<XMLDocument> result = IRsystem.search(query);
        assertTrue("Result for query '" + query + "' was null, but shouldn't!", result.size() > 0);

    }

}