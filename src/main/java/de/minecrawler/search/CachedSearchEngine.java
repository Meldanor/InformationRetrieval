/*
 * Copyright (C) 2014
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

package de.minecrawler.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

public class CachedSearchEngine extends AbstractSearchEngine {

    public CachedSearchEngine(File cacheFile) throws Exception {
        super(cacheFile);
    }

    @Override
    protected Directory createDirectory(Object... args) {
        try {
            return new NIOFSDirectory((File) args[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
