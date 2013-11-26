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

package de.minecrawler.IR1;

/*
 * Information:
 * XML -> XSD mit trang (http://www.thaiopensource.com/relaxng/trang.html)
 * XSD -> JABX POJO mit xjc
 */

public class Core {

    public static final String XML_ENTITY_PACKAGE = "de.minecrawler.IR1.data";

    public static void main(String[] args) {
        System.out.println("===============");
        System.out.println("= MINECRAWLER =");
        System.out.println("===============");

        if (args.length > 0) {
            new ArgumentInterface(args);
        } else {
            new WizardInterface();
        }
    }
}
