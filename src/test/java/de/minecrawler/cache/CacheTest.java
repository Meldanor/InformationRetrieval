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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CacheTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        new File("cacheIndex.txt").delete();
        new File("cache/").delete();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        new File("cacheIndex.txt").delete();
        new File("cache/").delete();
    }
    
    @Test
    public void xmlTest() throws MalformedURLException {
        CacheManager cmg = new CacheManager();
        cmg.addURL(new URL("http://www.gamestar.de"), 5);
        
        File cache = cmg.getCache(new URL("http://www.gamestar.de"), 5);
        assertNotNull(cache);
    }

}
