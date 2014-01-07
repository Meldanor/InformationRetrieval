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

public class CacheManager {

    private static final String CACHE_INDEX_FILENAME = "cacheIndex.txt";
    private static final String CACHE_FILE_ENDING = "cache";

    private Map<URL, List<CachedIndex>> urlIndex;
    private int currentIndex = 0;
    private File cacheDir;

    public CacheManager() {
        this.urlIndex = new HashMap<URL, List<CachedIndex>>();
        this.cacheDir = new File("cache");
        cacheDir.mkdir();
        loadIndex();
    }

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

                List<CachedIndex> list = urlIndex.get(url);
                if (list == null) {
                    list = new ArrayList<CachedIndex>();
                    urlIndex.put(url, list);
                }

                list.add(new CachedIndex(fileNumber, depth, date, url));

                if (fileNumber > maxIndex)
                    maxIndex = fileNumber;
            }

            bReader.close();
            this.currentIndex = maxIndex;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File addURL(URL url, int depth) {
        List<CachedIndex> list = urlIndex.get(url);
        if (list == null) {
            list = new ArrayList<CachedIndex>();
            urlIndex.put(url, list);
        }
        CachedIndex index = new CachedIndex(++currentIndex, depth, new DateTime(), url);

        list.add(index);

        writeIndex();
        return getFile(index);
    }

    public File getCache(URL url, int depth) {
        CachedIndex index = getCachedIndex(url, depth);
        if (index == null)
            return null;

        if (isExpired(index)) {
            removeCache(index);
            return null;
        } else
            return getFile(index);
    }

    private File getFile(CachedIndex index) {
        return new File(cacheDir, index.fileNumber + "." + CACHE_FILE_ENDING);
    }

    private CachedIndex getCachedIndex(URL url, int depth) {
        List<CachedIndex> list = urlIndex.get(url);
        if (list == null)
            return null;

        for (CachedIndex cachedIndex : list) {
            if (cachedIndex.depth == depth)
                return cachedIndex;
        }
        return null;
    }

    private void removeCache(CachedIndex index) {
        List<CachedIndex> list = urlIndex.get(index.url);
        if (list == null) {
            return;
        }
        list.remove(index);

    }

    private static final long EXPIRE_HOUR = 1L;

    private boolean isExpired(CachedIndex index) {

        Duration dur = new Duration(index.creationDate, null);
        return dur.getStandardHours() >= EXPIRE_HOUR;
    }

    private void writeIndex() {
        try {
            File cacheIndexFile = new File(CACHE_INDEX_FILENAME);

            BufferedWriter bWriter = new BufferedWriter(new FileWriter(cacheIndexFile));
            for (Entry<URL, List<CachedIndex>> entry : urlIndex.entrySet()) {
                for (CachedIndex index : entry.getValue()) {
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

    private class CachedIndex {

        int fileNumber;
        int depth;
        DateTime creationDate;
        URL url;

        public CachedIndex(int fileNumber, int depth, DateTime creationDate, URL url) {
            this.fileNumber = fileNumber;
            this.depth = depth;
            this.creationDate = creationDate;
            this.url = url;
        }

    }
}
