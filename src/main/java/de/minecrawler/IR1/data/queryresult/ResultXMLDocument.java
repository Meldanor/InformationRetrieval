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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.minecrawler.IR1.data.XMLDocument;

/**
 * Wrapper class to store the rank and the relevance score within the xml
 * document.
 */
@XmlRootElement(name = "result")
@XmlType(name = "", propOrder = {"rank", "relevanceScore", "doc"})
public class ResultXMLDocument {

    @XmlElement(name = "Document")
    private XMLDocument doc;
    @XmlElement
    private int rank;
    @XmlElement
    private float relevanceScore;

    /**
     * Used for the object factory
     */
    public ResultXMLDocument() {

    }

    /**
     * Creates a wrapper class to hold also the information about the rank and
     * the relevance.
     * 
     * @param doc
     *            The document itself to wrap
     * @param rank
     *            The rank of the search
     * @param relevanceScore
     *            The score
     */
    public ResultXMLDocument(XMLDocument doc, int rank, float relevanceScore) {
        this.doc = doc;
        this.rank = rank;
        this.relevanceScore = relevanceScore;
    }

    /**
     * @return The document itself to wrap
     */
    public XMLDocument getDoc() {
        return doc;
    }

    /**
     * @return The rank in the result list
     */
    public int getRank() {
        return rank;
    }

    /**
     * @return The score in the list determining its rank
     */
    public float getRelevanceScore() {
        return relevanceScore;
    }

}
