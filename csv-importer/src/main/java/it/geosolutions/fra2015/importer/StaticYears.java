/*
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.fra2015.importer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author DamianoG
 *
 */
public class StaticYears {

    public static Map<String, Integer> yearsMap = null;
    static{
        yearsMap = new HashMap<String, Integer>();
        yearsMap.put("2003", 1);
        yearsMap.put("2004", 2);
        yearsMap.put("2005", 3);
        yearsMap.put("2006", 17);
        yearsMap.put("2007", 18);
        yearsMap.put("2008", 19);
        yearsMap.put("2009", 20);
        yearsMap.put("2010", 21);
        yearsMap.put("2011", 22);
        yearsMap.put("2012", 23);
    }
    
    public static Map<String, Integer> yearsMapEntries16 = null;
    static{
        yearsMapEntries16 = new HashMap<String, Integer>();
        yearsMapEntries16.put("2000", 1);
        yearsMapEntries16.put("2001", 2);
        yearsMapEntries16.put("2002", 3);
        yearsMapEntries16.put("2003", 4);
        yearsMapEntries16.put("2004", 5);
        yearsMapEntries16.put("2005", 6);
        yearsMapEntries16.put("2006", 7);
        yearsMapEntries16.put("2007", 1);
        yearsMapEntries16.put("2008", 2);
        yearsMapEntries16.put("2009", 3);
        yearsMapEntries16.put("2010", 4);
        yearsMapEntries16.put("2011", 5);
        yearsMapEntries16.put("2012", 6);
    }
    
    public static Map<String, Integer> yearsMapEntry8 = null;
    static{
        yearsMapEntry8 = new HashMap<String, Integer>();
        yearsMapEntry8.put("2003", 1);
        yearsMapEntry8.put("2004", 3);
        yearsMapEntry8.put("2005", 5);
        yearsMapEntry8.put("2006", 7);
        yearsMapEntry8.put("2007", 9);
        yearsMapEntry8.put("2008", 1);
        yearsMapEntry8.put("2009", 3);
        yearsMapEntry8.put("2010", 5);
        yearsMapEntry8.put("2011", 7);
        yearsMapEntry8.put("2012", 9);
    }
    
    public static Map<String, Integer> yearsMap2 = null;
    static {
        yearsMap2 = new HashMap<String, Integer>();
        yearsMap2.put("1990", 1);
        yearsMap2.put("2000", 2);
        yearsMap2.put("2005", 3);
        yearsMap2.put("2010", 4);
        yearsMap2.put("2015", 5);
        yearsMap2.put("2020", 6);
        yearsMap2.put("2030", 7);
    }
    
    public static Map<String, Integer> yearsMap3b = null;
    static {
        yearsMap3b = new HashMap<String, Integer>();
        yearsMap3b.put("1990", 3);
        yearsMap3b.put("2000", 4);
        yearsMap3b.put("2005", 5);
        yearsMap3b.put("2010", 6);
    }
    
    public static Map<String, Integer> yearsMap3 = null;
    static {
        yearsMap3 = new HashMap<String, Integer>();
        yearsMap3.put("2005", 1);
        yearsMap3.put("2010", 2);
    }
    
    public static Map<String, Integer> yearsMap4 = null;
    static {
        yearsMap4 = new HashMap<String, Integer>();
        yearsMap4.put("2000", 1);
        yearsMap4.put("2005", 2);
        yearsMap4.put("2010", 3);
    }
    
    public static Set<String> specialYears = null;
    static {
        specialYears = new LinkedHashSet<String>();
        specialYears.add("9999");
    }
}
