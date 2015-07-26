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

import it.geosolutions.fra2015.mvc.controller.utils.ReviewerUtils;
import it.geosolutions.fra2015.server.model.xmlexport.BasicValue;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * This class create a BasicValue object taking as input the information gathered in the CSV for a certain value of an EntryItem.
 * 
 * @author DamianoG
 *
 */
public class CSVBasicValueCreator {

    private static final String ASTERISK = "*";
    private static final String A_BRACKET_SIX = "(6)";
    private static final String COLNAME_SEPARATOR = "_";
    private static final Integer INCREMENT = 6-1;
    private static final String DESK_STUDY_VALUE = "1000";
    
    private static Map<String, Integer> yearsMap = null;
    static{
        yearsMap = new HashMap<String, Integer>();
        yearsMap.put("1990", 1);
        yearsMap.put("1991", 2);
        yearsMap.put("1992", 3);
        yearsMap.put("1993", 4);
        yearsMap.put("1994", 5);
        yearsMap.put("1995", 6);
        yearsMap.put("1996", 7);
        yearsMap.put("1997", 8);
        yearsMap.put("1998", 9);
        yearsMap.put("1999", 10);
        yearsMap.put("2000", 11);
        yearsMap.put("2001", 12);
        yearsMap.put("2002", 13);
        yearsMap.put("2003", 14);
        yearsMap.put("2004", 15);
        yearsMap.put("2005", 16);
        yearsMap.put("2006", 17);
        yearsMap.put("2007", 18);
        yearsMap.put("2008", 19);
        yearsMap.put("2009", 20);
        yearsMap.put("2010", 21);
        yearsMap.put("2011", 22);
        yearsMap.put("2012", 23);
        yearsMap.put("2013", 24);
        yearsMap.put("2014", 25);
        yearsMap.put("2015", 26);
    }
    
    private static Map<String, Integer> yearsMap2 = null;
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
    
    private static Set<String> specialYears = null;
    static {
        specialYears = new LinkedHashSet<String>();
        specialYears.add("9999");
    }

    
    public static BasicValue createBasicValue(String colName, String value, String year, int questionNumber){
        BasicValueCreator bvc = makeCreator(colName);
        return bvc.create(colName, value, year, questionNumber);
    }
    
    /**
     * Create the needed BasicValueCreator instance 
     * 
     * @param colName
     * @return
     */
    public static BasicValueCreator makeCreator(String colName){
        
        if(StringUtils.isBlank(colName)){
            throw new IllegalArgumentException("The colName parameter passed is null!!!");
        }                                                                                                                    //   O M G ! ! ! 
        if((StringUtils.startsWith(colName, COLNAME_SEPARATOR) || StringUtils.endsWith(colName, COLNAME_SEPARATOR)) && !StringUtils.contains(colName,DESK_STUDY_VALUE)){
            throw new IllegalArgumentException("The colName parameter passed starts or ends with '_' and that's not acceptable at all!");
        }
        
        CSVBasicValueCreator csvBVC = new CSVBasicValueCreator();
        
        if(colName.endsWith(ASTERISK)){
            return csvBVC.new AsterikCreator();
        }else if(colName.endsWith(A_BRACKET_SIX)){
            return csvBVC.new BracketCreator();
        }else if(colName.split(COLNAME_SEPARATOR).length == 2){
            return csvBVC.new BasicYearCreator();
        }else if(colName.split(COLNAME_SEPARATOR).length == 3){
            return csvBVC.new StandardCreator();
        }else{
            throw new IllegalArgumentException("No way, the colName '" + colName + "' is not a standard one...");
        }
    }
    
    public abstract static class BasicValueCreator{

        public enum ValueType {
            NUMBER ("Number"),
            STRING ("String");
            
            private final String name; // in meters
            
            ValueType(String name) {
                this.name = name;
            }
            
            @Override
            public String toString() { 
                return name; 
            }
        }
        
        public abstract BasicValue create(String colName, String value, String year, long questionNumber);
        
        public String chooseType(String value){
            if(StringUtils.isNumeric(value)){
                return ValueType.NUMBER.toString();
            }
            return ValueType.STRING.toString();
        } 
    }
    
    public class AsterikCreator extends BasicValueCreator{
        
        @Override
        public BasicValue create(String colName, String value, String year, long questionNumber) {
            Integer index = yearsMap.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear)!!!");
            }
            String reference = colName.replace(ASTERISK, COLNAME_SEPARATOR+index);
            return ReviewerUtils.buildBasicValue(questionNumber, value, reference, chooseType(value));
        }
    }
    
    public class BracketCreator extends BasicValueCreator{
        @Override
        public BasicValue create(String colName, String value, String year, long questionNumber) {
            Integer index = yearsMap2.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear2)!!!");
            }
            index = index+INCREMENT;
            String reference = colName.replace(A_BRACKET_SIX, COLNAME_SEPARATOR+index);
            return ReviewerUtils.buildBasicValue(questionNumber, value, reference, chooseType(value));
        }
    }
    
    public class BasicYearCreator extends BasicValueCreator{
        @Override
        public BasicValue create(String colName, String value, String year, long questionNumber) {
            Integer index = yearsMap2.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear2)!!!");
            }
            String reference = colName+COLNAME_SEPARATOR+index;
            return ReviewerUtils.buildBasicValue(questionNumber, value, reference, chooseType(value));
        }
    }
    
    public class StandardCreator extends BasicValueCreator{
        @Override
        public BasicValue create(String colName, String value, String year, long questionNumber) {
            return ReviewerUtils.buildBasicValue(questionNumber, value, colName, chooseType(value));
        }
    }
}
