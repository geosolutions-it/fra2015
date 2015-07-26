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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import static it.geosolutions.fra2015.importer.ReservedHeaderNames.*;
/**
 * @author DamianoG
 *
 */
public class CSVLoader {

    private final static Logger LOGGER = Logger.getLogger(CSVLoader.class);
    
    /**
     * A field called <b>Country</b> must be present.
     */
    private final static Set<String> supportedHeaders;
    static{
        supportedHeaders = new LinkedHashSet<String>();
        supportedHeaders.add("{_1000_=0, Country=1, Year=2,  1a_2=3, 9x_1_1=4, 9x_1_2=5,  1a_3=6, 9x_2_1=7, 9x_2_2=8,  1a_4=9,  1a_5=10,  1a_6=11, 1b_2=12, 9x_3_1=13, 9x_3_2=14, 1b_3=15, 1b_4=16, 1b_5=17, 9x_6_1=18, 9x_6_2=19, 1b_6=20, 1b_7=21, 9x_4_1=22, 9x_4_2=23, 1b_8=24, 2a_2=25, 21x_1_1 =26, 21x_1_2 =27, 2a_3 =28, 21x_2_1 =29, 21x_2_2 =30, 2a_4=31, 2a_5=32, 2a_6=33, 21x_3_1=34, 21x_3_2 =35, 2a_7=36, 2c_2=37, 21x_4_1 =38, 21x_4_2 =39, 2c_3=40, 3a_3=41, 35_1_1 =42, 35_1_2 =43, 3a_4 =44, 3a_5 =45, 3a_3(6)=46, 3a_4(6)=47, 3a_5(6)=48, 3b_2 =49, 3b_3 =50, 3b_4 =51, 3b_5 =52, 3b_6 =53, 3b_7 =54, 3b_8 =55, 3b_9 =56, 3b_10 =57, 3b_11 =58, 3b_12 =59, 3c_3 =60, 35_2_1 =61, 35_2_2 =62, 3c_4 =63, 3c_5 =64, 3d_3 =65, 3d_3(6)=66, 35_3_1 =67, 35_3_2 =68, 3d_4=69, 3d_4(6)=70, 35_4_1 =71, 35_4_2 =72, 3d_5=73, 35_5_1=74, 35_5_2=75, 3d_5(6)=76, 3e_3 =77, 3e_3(6)=78, 35_6_1=79, 35_6_2=80, 3e_4 =81, 3e_4(6)=82, 35_7_1=83, 35_7_2=84, 3e_5 =85, 3e_5(6)=86, 3e_6 =87, 3e_6(6)=88, 3e_7 =89, 3e_7(6)=90, 3e_8 =91, 3e_8(7)=92, 35_8_1=93, 35_8_2=94, 3e_9 =95, 3e_9(6)=96, 35_9_1=97, 35_9_2=98, 4a_2=99, 48_1_1=100, 48_1_2=101, 4a_3=102, 48_2_1=103, 48_2_2=104, 5a_2=105, 60_1_1=106, 60_1_2=107, 5a_3=108, 5a_4=109, 5a_5=110, 5a_6=111, 5a_7=112, 5a_8=113, 5b_2=114, 60_2_1=115, 60_2_2=116, 5b_3=117, 5b_4=118, 5b_5=119, 5b_6=120, 6_2=121, 71_1_1=122, 71_1_2=123, 6_3=124, 71_2_1=125, 71_2_2=126, 7_2=127, 7_3=128, 7_4=129, 7_5=130, 7_6=131, 7_7=132, 7_8=133, 7_9=134, 7_10=135, 7_11=136, 7_12=137, 83_1_1=138, 83_1_2=139, 8b_1=140, 8b_2=141, 8b_3=142, 8b_4=143, 8b_5=144, 8b_6=145, 8b_7=146, 8b_8=147, 8b_9=148, 8b_10=149, 95_2_1=150, 95_2_2=151, 9_1=152, 900_1_2=153, 10_3_1=154, 10_3_3=155, 10_3_5=156, 10_3_7=157, 10_5_1=158, 10_5_3=159, 10_5_5=160, 10_5_7=161, 10_7_1=162, 10_7_3=163, 10_7_5=164, 10_7_7=165, 10_9_1=166, 10_9_3=167, 10_9_5=168, 10_9_7=169, 10_10_1=170, 10_10_3=171, 10_10_5=172, 10_10_7=173, 10_11_1=174, 10_11_3=175, 10_11_5=176, 10_11_7=177, 11_0_1=178, 12_1_1=179, 116_1_1=180, 12_2_1=181, 116_2_1=182, 13a_2_1=183, 13a_2_2=184, 13a_2_3=185, 13a_2_4=186, 13a_2_5=187, 13a_2_6=188, 13a_2_7=189, 13a_2_8=190, 13a_3_1=191, 13a_3_2=192, 13a_3_3=193, 13a_3_4=194, 13a_3_5=195, 13a_3_6=196, 13a_3_7=197, 13a_3_8=198, 13a_4_1=199, 13a_4_2=200, 13a_4_3=201, 13a_4_4=202, 13a_4_5=203, 13a_4_6=204, 13a_4_7=205, 13a_4_8=206, 13a_5_1=207, 13a_5_2=208, 13b_1_1=209, 13b_3_1=210, 13b_5_1=211, 13b_7_1=212, 14_1=213, 126_1_1=214, 14_2=215, 14_3=216, 14b_1_1=217, 14b_3_1=218, 14b_5_1=219, 14c_0_1=220, 126_2_1=221, 15_1_1=222, 15_3_1=223, 15_5_1=224, 131_1_1=225, 17_2=226, 17_3=227, 18a_2=228, 149_1_1=229, 149_1_2=230, 18a_3=231, 18a_4=232, 18a_5=233, 149_2_1=234, 149_2_2=235, 18a_6=236, 18a_7=237, 18a_8=238, 18a_9=239, 149_3_1=240, 149_3_2=241, 18b_2=242, 18b_3=243, 18b_4=244, 18b_5=245, 18b_6=246, 19_2=247, 19_3=248, 20_1_1=249}");
        supportedHeaders.add("{Country=0, Year=1, 4c_2_1=2, 4c_2_2=3, 8a_3*=4, 8a_4*=5, 16a_2*=6, 16a_3*=7, 16a_4*=8, 16b_2*=9, 16b_3*=10, 16b_4*=11, 21b_2_1=12}");
    }
    
    private List<CSVRecord> csvList;
    
    private Map<String,Integer> header;
    
    private CSVRecord prettyHeader;
    
    private CSVRecord descriptionHeader;
    
    private Set<String> countriesCode;
    
    public CSVLoader(String pathCSV){
        
        Reader fileReader = null;
        Reader reader = null;
        CSVParser parser = null;
        countriesCode = new LinkedHashSet<String>();
        
        try {
            fileReader = new FileReader(pathCSV);
            reader = new BufferedReader(fileReader);
            parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withAllowMissingColumnNames());
            
        } catch (IOException e) {
            
            if(fileReader != null){
                try {
                    fileReader.close();
                } catch (IOException e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e1) {
                    LOGGER.error(e1.getMessage(), e1);
                }
            }
            throw new IllegalArgumentException("Error loading the input files");
        }
        
        header = parser.getHeaderMap();
        if(header == null || !supportedHeaders.contains(header.toString())){
            throw new IllegalArgumentException("The provided CSV doesn't support the header");
        }
        
        try {
            csvList = parser.getRecords();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading the CSV records");
        }
        
        this.prettyHeader = csvList.remove(0);
        this.descriptionHeader = csvList.remove(0);
        
        for(CSVRecord record : csvList){
            String tempCode = record.get(COUNTRY.toString());
            if(!countriesCode.contains(tempCode) && !COUNTRY.toString().equals(tempCode)){
                countriesCode.add(tempCode);
            }
        }
        
    }
    
    /**
     * @return the prettyHeader
     */
    public CSVRecord getPrettyHeader() {
        return prettyHeader;
    }

    /**
     * @return the descriptionHeader
     */
    public CSVRecord getDescriptionHeader() {
        return descriptionHeader;
    }
    
    public int countCSVRecords(){
        return csvList.size();
    }
    
    public Set<String> getCountries(){
        return Collections.unmodifiableSet(countriesCode);
    }
    
    public Map<String, Integer> getHeader(){
        return Collections.unmodifiableMap(header);
    }
    
    public List<CSVRecord> groupCSVByCountry(String country){
        
        List<CSVRecord> outList = new LinkedList<CSVRecord>();
        
        if(StringUtils.isBlank(country)){
            throw new IllegalArgumentException("A not-null and nor-whitespace country is required!");
        }
        for(CSVRecord record : csvList){
            if(country.equals(record.get(COUNTRY.toString()))){
                outList.add(record);
            }
        }
        return Collections.unmodifiableList(outList);
    }
}
