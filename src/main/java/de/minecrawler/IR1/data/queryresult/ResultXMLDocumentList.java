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

package de.minecrawler.IR1.data.queryresult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.minecrawler.IR1.data.XMLDocument;

/**
 * Wrapper class to hold a list of the type {@link ResultXMLDocument}.
 */
@XmlRootElement(name = "resultList")
public class ResultXMLDocumentList implements Iterable<ResultXMLDocument> {

    @XmlElement(name = "results")
    private List<ResultXMLDocument> results;

    /**
     * Constructs an empty wrapper class for results. Adds them via
     * {@link #addResult(XMLDocument, int, float)}.
     */
    public ResultXMLDocumentList() {
        results = new ArrayList<ResultXMLDocument>();
    }

    /**
     * Adds a single result to the wrapper class
     * 
     * @param doc
     *            The document itself
     * @param rank
     *            The rank of the document in the result list
     * @param relevanceScore
     *            The relevanceScore of the document considering the query
     */
    public void addResult(XMLDocument doc, int rank, float relevanceScore) {
        this.results.add(new ResultXMLDocument(doc, rank, relevanceScore));
    }

    /**
     * @return Copy of the results
     */
    public List<ResultXMLDocument> getResults() {
        return new ArrayList<ResultXMLDocument>(results);
    }

    public Iterator<ResultXMLDocument> iterator() {
        return results.iterator();
    }

    /**
     * @return Size of the result list
     */
    public int size() {
        return results.size();
    }

}
