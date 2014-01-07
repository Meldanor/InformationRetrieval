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

package de.minecrawler.data;

/**
 * Wrapper class to contain the result information (score and rank) of the
 * search and its corresponding website
 */
public class CrawledWebsiteResult {

    private CrawledWebsite website;
    private int rank;
    private float score;

    /**
     * @param website
     *            The crawled website having the rank and the score
     * @param rank
     *            Rank of the search (lower is better)
     * @param score
     *            Score of the search (higher score is a better rank)
     */
    public CrawledWebsiteResult(CrawledWebsite website, int rank, float score) {
        this.website = website;
        this.rank = rank;
        this.score = score;
    }

    /**
     * @return Rank of the search (lower is better)
     */
    public int getRank() {
        return rank;
    }

    /**
     * @return Rank of the search (lower is better)
     */
    public float getScore() {
        return score;
    }

    /**
     * @return The crawled website having the rank and the score
     */
    public CrawledWebsite getWebsite() {
        return website;
    }
}
