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

import static it.geosolutions.fra2015.importer.StaticYears.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.geosolutions.fra2015.mvc.controller.utils.ReviewerUtils;
import it.geosolutions.fra2015.server.model.xmlexport.BasicValue;

import org.apache.commons.lang3.StringUtils;

/**
 * This class create a BasicValue object taking as input the information gathered in the CSV for a certain value of an EntryItem.
 * 
 * @author DamianoG
 *
 */
public class CSVBasicValueCreator {

    private static final String A_BRACKET_SIX = "(6)";
    private static final String COLNAME_SEPARATOR = "_";
    private static final Integer INCREMENT = 6-1;
    private static final String DESK_STUDY_VALUE = "1000";
    
    public static BasicValue createBasicValue(String colName, String value, String year, String prettyHeaderCell){
        BasicValueCreator bvc = makeCreator(colName);
        return bvc.create(colName, value, year, prettyHeaderCell);
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
        } 
        if((StringUtils.startsWith(colName, COLNAME_SEPARATOR) || StringUtils.endsWith(colName, COLNAME_SEPARATOR))){
            throw new IllegalArgumentException("The colName parameter passed starts or ends with '_' and that's not acceptable at all!");
        }
        
        CSVBasicValueCreator csvBVC = new CSVBasicValueCreator();
        
        if(colName.endsWith(A_BRACKET_SIX)){
            return csvBVC.new BracketCreator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 3 && colName.startsWith("4c_2")){
            return csvBVC.new Entry4cCreator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 2 && colName.startsWith("3b_")){
            return csvBVC.new Entry3bCreator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 2 && colName.startsWith("8a_")){
            return csvBVC.new Entry8Creator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 2 && (colName.startsWith("16a_") || colName.startsWith("16b_"))){
            return csvBVC.new Entry16Creator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 2 && colName.startsWith("7_")){
            return csvBVC.new TwoYearsCreator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 2 && (colName.startsWith("17_") || colName.startsWith("140_"))){
            return csvBVC.new ThreeYearsCreator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 2){
            return csvBVC.new BasicYearCreator();
        }
        else if(colName.split(COLNAME_SEPARATOR).length == 3){
            return csvBVC.new StandardCreator();
        }
        else if(colName.equals(DESK_STUDY_VALUE)){
            return csvBVC.new DeskStudyCreator();
        }
        else{
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
        
        public BasicValue create(String colName, String value, String year, String prettyHeaderCell){
            if(colName == null || value == null || year == null){
                throw new IllegalArgumentException("colName, value or year are null");
            }
            colName = colName.trim(); 
            value = value.trim(); 
            year = year.trim();
            return createInternal(colName, value, year, prettyHeaderCell);
        }
        
        public abstract BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell);
        
        public String chooseType(String value){
            String[] tmpValue = value.split("\\.");
            if(tmpValue != null && tmpValue.length == 1 && StringUtils.isNumeric(value)){
                return "numeric";
            }
            if(tmpValue != null && tmpValue.length == 2 && StringUtils.isNumeric(tmpValue[0]) && StringUtils.isNumeric(tmpValue[1])){
                return "numeric";
            }
            return "text";
        }
    }
    
    public class BracketCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            Integer index = yearsMap2.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear2)!!!");
            }
            index = index+INCREMENT;
            String reference = colName.replace(A_BRACKET_SIX, COLNAME_SEPARATOR+index+COLNAME_SEPARATOR);
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, reference, chooseType(value));
        }
    }
    
    public class DeskStudyCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), StringUtils.lowerCase(value), colName+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class BasicYearCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            Integer index = yearsMap2.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear2)!!!");
            }
            String reference = colName+COLNAME_SEPARATOR+index;
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, reference+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class Entry3bCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            Integer index = yearsMap3b.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear2)!!!");
            }
            String reference = colName+COLNAME_SEPARATOR+index;
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, reference+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class TwoYearsCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            Integer index = yearsMap3.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear3)!!!");
            }
            String reference = colName+COLNAME_SEPARATOR+index;
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, reference+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class ThreeYearsCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            Integer index = yearsMap4.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear4)!!!");
            }
            String reference = colName+COLNAME_SEPARATOR+index;
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, reference+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class Entry16Creator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            Integer index = yearsMapEntries16.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear16a)!!!");
            }
            int row = 0;
            String [] colNameParts = colName.split("_");
            if(year.equals("2007") || year.equals("2008") || year.equals("2009") || year.equals("2010") || year.equals("2011") || year.equals("2012")){
                row = Integer.parseInt(colNameParts[1])+4; 
            }
            else{
                row = Integer.parseInt(colNameParts[1]);
            }
            String reference = colNameParts[0]+COLNAME_SEPARATOR+row+COLNAME_SEPARATOR+index;
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, reference+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class Entry8Creator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year,String prettyHeaderCell) {
            Integer index = yearsMapEntry8.get(year);
            if(index == null){
                if(specialYears.contains(year)){
                    return null;
                }
                throw new IllegalArgumentException("The passed year has no references in the map (mapYear16a)!!!");
            }
            int row = 0;
            String [] colNameParts = colName.split("_");
            if(year.equals("2008") || year.equals("2009") || year.equals("2010") || year.equals("2011") || year.equals("2012")){
                row = Integer.parseInt(colNameParts[1])+4; 
            }
            else{
                row = Integer.parseInt(colNameParts[1]);
            }
            String reference = colNameParts[0]+COLNAME_SEPARATOR+row+COLNAME_SEPARATOR+index;
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, reference+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class StandardCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            String tiers = "";
            if(prettyHeaderCell.contains("Tiers")){
                tiers = "Tier ";
            }
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), tiers+value, colName+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    public class Entry4cCreator extends BasicValueCreator{
        @Override
        public BasicValue createInternal(String colName, String value, String year, String prettyHeaderCell) {
            
            int yearNum = 0;
            try{
                yearNum = Integer.parseInt(year);
            }
            catch(Exception e){
                throw new IllegalArgumentException("the year for a value in the table 4c is not a valid number!");
            }
            if(yearNum < 1990 || yearNum > 2011){
                throw new IllegalArgumentException("the year for a value in the table 4c is not between 1990 and 2011");
            }
            int rowNumber = Math.abs(1990-yearNum);
            int offset=(rowNumber<20)?2:3;
            colName = colName.replace("4c_2_", "4c_"+(rowNumber+offset)+"_");
            return ReviewerUtils.buildBasicValue(extractQuestionName(prettyHeaderCell), value, colName+COLNAME_SEPARATOR, chooseType(value));
        }
    }
    
    private static long extractQuestionName(String value){
        Pattern pattern = Pattern.compile("^Q(\\d*)");
        Matcher matcher = pattern.matcher(value);
        matcher.find();
        return Long.parseLong(matcher.group(1));
    }
}
