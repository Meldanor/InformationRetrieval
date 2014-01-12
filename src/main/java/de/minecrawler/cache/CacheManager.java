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

package de.minecrawler.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Manages cache for crawled and indexed websites to provide an easier interface
 * for this. A cached search is always faster then a crawled base search!
 */
public class CacheManager {

    private static final String CACHE_INDEX_FILENAME = "cacheIndex.txt";
    private static final String CACHE_FILE_ENDING = "cache";

    private Map<URL, List<CacheIndex>> urlIndex;
    private int currentIndex = 0;
    private File cacheDir;

    /**
     * Creates a CacheManager and loads the cache index.
     */
    public CacheManager() {
        this.urlIndex = new HashMap<URL, List<CacheIndex>>();
        this.cacheDir = new File("cache");
        cacheDir.mkdir();
        loadIndex();
    }

    /**
     * Parses the cache index and loads the needed information from the cache
     * to the map.
     */
    private void loadIndex() {
        try {
            File cacheIndexFile = new File(CACHE_INDEX_FILENAME);
            if (!cacheIndexFile.exists())
                return;

            BufferedReader bReader = new BufferedReader(new FileReader(cacheIndexFile));
            String line = "";

            int maxIndex = 0;

            while ((line = bReader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                String[] split = line.split(" ");
                int pos = 0;
                URL url = new URL(split[pos++]);
                int depth = Integer.valueOf(split[pos++]);
                DateTime date = DateTime.parse(split[pos++]);
                int fileNumber = Integer.valueOf(split[pos++]);

                List<CacheIndex> list = urlIndex.get(url);
                if (list == null) {
                    list = new ArrayList<CacheIndex>();
                    urlIndex.put(url, list);
                }

                list.add(new CacheIndex(fileNumber, depth, date, url));

                if (fileNumber > maxIndex)
                    maxIndex = fileNumber;
            }

            bReader.close();
            this.currentIndex = maxIndex;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provides a file where to save the cache for the url and its depth. Same urls
     * with different depth have different caches!
     * 
     * @param url
     *            The seed of the crawling
     * @param depth
     *            The depth of the crawling
     * @return A file where to save the cache
     */
    public File addURL(URL url, int depth) {
        List<CacheIndex> list = urlIndex.get(url);
        if (list == null) {
            list = new ArrayList<CacheIndex>();
            urlIndex.put(url, list);
        }
        CacheIndex index = new CacheIndex(++currentIndex, depth, new DateTime(), url);

        list.add(index);

        writeIndex();
        return index.getFile();
    }

    /**
     * Retrieves the file to the cache. Same urls with different depth have
     * different caches!
     * 
     * @param url
     *            The seed of the crawling
     * @param depth
     *            The depth of the crawling
     * @return <code>Null</code> when no cache was found or the cache is
     *         expired (it will be deleted!). Otherwise the file to
     *         cache
     */
    public File getCache(URL url, int depth) {
        CacheIndex index = getCacheIndex(url, depth);
        if (index == null)
            return null;

        if (isExpired(index)) {
            removeCache(index);
            return null;
        } else
            return index.getFile();
    }

    /**
     * Searches for the cache index.
     * 
     * @param url
     *            The url
     * @param depth
     *            The depth of crawling
     * @return <code>null</code> if no cache was found for the url and its
     *         depth. Otherwise the CacheIndex
     */
    private CacheIndex getCacheIndex(URL url, int depth) {
        List<CacheIndex> list = urlIndex.get(url);
        if (list == null)
            return null;

        for (CacheIndex cachedIndex : list) {
            if (cachedIndex.depth == depth)
                return cachedIndex;
        }
        return null;
    }

    /**
     * Removes the cache.
     * 
     * @param url
     *            The url
     * @param depth
     *            The depth of crawling
     * @return <code>true</code> when the cache exists and was successfully
     *         removed, otherwise <code>false</code>
     */
    public boolean removeCache(URL url, int depth) {
        CacheIndex index = getCacheIndex(url, depth);
        if (index == null)
            return false;
        else
            return removeCache(index);
    }

    /**
     * Removes and deletes the cache from the cacheManager and the filesystem
     * 
     * @param index
     *            The index to remove
     * @return <code>true</code> when the index was found, otherwhise
     *         <code>false</code>
     */
    private boolean removeCache(CacheIndex index) {
        List<CacheIndex> list = urlIndex.get(index.url);
        if (list == null) {
            return false;
        }
        File file = index.getFile();
        deleteDir(file);
        list.remove(index);

        writeIndex();

        return true;
    }

    /**
     * Helper function to delete a complete directory
     * 
     * @param dir
     *            Directory to clear and delete
     */
    private void deleteDir(File dir) {
        File[] listFiles = dir.listFiles();
        for (int i = 0; i < listFiles.length; ++i) {
            File file = listFiles[i];
            if (file.isDirectory())
                deleteDir(file);
            else
                file.delete();
        }
        dir.delete();
    }

    private static final long EXPIRE_HOUR = 1L;

    /**
     * @param index
     *            Index to check
     * @return <code>true</code> when the cache is expired, otherwhise
     *         <code>false</code>
     */
    private boolean isExpired(CacheIndex index) {

        Duration dur = new Duration(index.creationDate, null);
        return dur.getStandardHours() >= EXPIRE_HOUR;
    }

    /**
     * Writes the complete cache index to the cacheindex file
     */
    private void writeIndex() {
        try {
            File cacheIndexFile = new File(CACHE_INDEX_FILENAME);

            BufferedWriter bWriter = new BufferedWriter(new FileWriter(cacheIndexFile));
            for (Entry<URL, List<CacheIndex>> entry : urlIndex.entrySet()) {
                for (CacheIndex index : entry.getValue()) {
                    bWriter.append(index.url.toString()).append(' ');
                    bWriter.append(Integer.toString(index.depth)).append(' ');
                    bWriter.append(index.creationDate.toString()).append(' ');
                    bWriter.append(Integer.toString(index.fileNumber));
                    bWriter.append(System.lineSeparator());
                }
            }
            bWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Container class for a single cache index. Containg the filenumber, the
     * creationdate and the url and depth of the crawled cache.
     */
    private class CacheIndex {

        int fileNumber;
        int depth;
        DateTime creationDate;
        URL url;

        public CacheIndex(int fileNumber, int depth, DateTime creationDate, URL url) {
            this.fileNumber = fileNumber;
            this.depth = depth;
            this.creationDate = creationDate;
            this.url = url;
        }

        private File getFile() {
            return new File(cacheDir, fileNumber + "." + CACHE_FILE_ENDING);
        }

    }
}
