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

import static it.geosolutions.fra2015.importer.ReservedHeaderNames.COUNTRY;
import static it.geosolutions.fra2015.importer.ReservedHeaderNames.YEAR;
import it.geosolutions.fra2015.server.model.xmlexport.BasicValue;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author DamianoG
 *
 */
public class CSVParser {

    private final static Logger LOGGER = Logger.getLogger(CSVParser.class);
    
    private List<CSVRecord> records;
    private Set<Map<String, Integer>> headers;
    private String country;
    private List<CSVRecord> prettyHeaders;
    private List<CSVRecord> descriptionHeaders;

    /**
     * @param csv1
     * @param csv2
     */
    public CSVParser(String country, Set<Map<String, Integer>> headerList, List<CSVRecord>... recordsLists) {
        
        records = new LinkedList<CSVRecord>();
        headers = new LinkedHashSet<Map<String, Integer>>();
        for(List<CSVRecord> list : recordsLists){
            records.addAll(list);
        }
        headers = headerList;
        headerList = evaluateHeaders();
        headerList = null;
        recordsLists = null;
        records = Collections.unmodifiableList(records);
        headers = Collections.unmodifiableSet(headers);
        this.country = country;
        this.prettyHeaders = new LinkedList<CSVRecord>();
    }

    /**
     * The core method: Iterate over the records and create a list of BasicValue to be marshalled
     */
    public List<BasicValue> toBasicValuesList(){
        
        if(prettyHeaders == null || prettyHeaders.size() != headers.size()){
            throw new IllegalStateException("The prettyHeders list is not initialized properly! (null or it has a different size than headers list...)");
        }
        
        List<BasicValue> surveyValues = new LinkedList<BasicValue>();
        
        for(CSVRecord record : records){
            Map<String, Integer> header = findHeader(record);
            CSVRecord prettyHeader = findPrettyHeader(record);
            Iterator<String> iter = header.keySet().iterator();

            try {
                while (iter.hasNext()) {
                    String colName = iter.next();
                    if (record.isSet(colName) && !StringUtils.isBlank(record.get(colName))) {
                        if (!ReservedHeaderNames.isReserved(colName)) {
                            BasicValue bv = CSVBasicValueCreator.createBasicValue(colName, record.get(colName), record.get(YEAR), prettyHeader.get(header.get(colName)));
                            if (bv != null) {
                                surveyValues.add(bv);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error parsing record: " + e.getMessage());
                LOGGER.error("Error parsing record " + record + " : ");
                throw new RuntimeException(e);
            }
        }
        return surveyValues;
    }
    
    /**
     * Search from the available headers the one which match with the provided record
     * 
     * <b>NOTE</b> the logic is very simple: compare the size of the record with the size of each header and it selects the first one.
     *  
     * @return a Map<String, Integer> which represent an header fits the provided record
     */
    public Map<String, Integer> findHeader(CSVRecord record){
        if(record == null){
            throw new IllegalArgumentException("input CSVRecord is null...");
        }
        for(Map<String, Integer> header : headers){
            if(header.size() == record.size()){
                return header;
            }
        }
        return null;
    }
    
    /**
     * Search from the available prettyHeaders (that are CSVRecord) the one which match with the provided record
     * 
     * <b>NOTE</b> the logic is very simple: compare the size of the record with the size of each header and it selects the first one.
     *  
     * @return a Map<String, Integer> which represent an header fits the provided record
     */
    private CSVRecord findPrettyHeader(CSVRecord record){
        if(record == null){
            throw new IllegalArgumentException("input CSVRecord is null...");
        }
        for(CSVRecord header : prettyHeaders){
            if(header.size() == record.size()){
                return header;
            }
        }
        return null;
    }
    
    /**
     * To be accepted an header must be:
     * <ul>
     *  <li>Not null nor empty</li>
     *  <li></li>
     *  <li></li>
     * </ul>
     * 
     * @return
     */
    public Set<Map<String, Integer>> evaluateHeaders(){
        Set<Map<String, Integer>> cleanedHeaderList = new LinkedHashSet<Map<String, Integer>>();
        for(Map<String, Integer> header : headers){
            if(evaluateHeader(header)){
                cleanedHeaderList.add(header);
                LOGGER.info("header accepted! --> {" + header.toString() + "}");
            }
            else{
                LOGGER.warn("header NOT ACCEPTED! --> {" + header.toString() + "}");
            }
            
        }
        return cleanedHeaderList;
    }
    
    private boolean evaluateHeader(Map<String, Integer> header){
        if(header == null || header.size() == 0){
            return false;
        }
        if(header.get(YEAR.toString()) == null || header.get(COUNTRY.toString()) == null){
            return false;
        }
        return true;
    }
    
    /**
     * @param prettyHeader the prettyHeader to set
     */
    public void setPrettyHeader(CSVRecord... prettyHeader) {
        for(CSVRecord record : prettyHeader){
            this.prettyHeaders.add(record);
        }
    }

    /**
     * @param descriptionHeader the descriptionHeader to set
     */
    public void setDescriptionHeader(List<CSVRecord> descriptionHeader) {
        this.descriptionHeaders = descriptionHeader;
    }
    
    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }
    
    public int getRecordsNumber(){
        return records.size();
    }
    
    public int getHeadersNumber(){
        return headers.size();
    }
}
